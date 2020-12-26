package org.jekajops.vk;

import com.vk.api.sdk.objects.audio.Audio;
import com.vk.api.sdk.objects.base.Link;
import com.vk.api.sdk.objects.base.LinkButton;
import com.vk.api.sdk.objects.base.LinkButtonActionType;
import com.vk.api.sdk.objects.docs.Doc;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.messages.MessageAttachment;
import org.jekajops.call_api.call_managers.callbine.selenium.UploadRobot;
import org.jekajops.call_api.exceptions.LoaderException;
import org.jekajops.core.context.Settings;
import org.jekajops.core.database.Database;
import org.jekajops.core.entities.Categories;
import org.jekajops.core.entities.Pair;
import org.jekajops.core.entities.Prank;
import org.jekajops.core.utils.files.PropertiesManager;
import org.jekajops.core.utils.parsers.TransliteratorRusToLatin;
import org.jekajops.vk.answer_listeners.AnswerListener;
import org.jekajops.vk.buttons.Button;
import org.jekajops.vk.buttons.KeyboardBuilder;
import org.jekajops.vk.buttons.KeyboardManager;
import org.jekajops.vk.buttons.keyboards.Keyboard;
import org.jekajops.vk.commands.PayCommand;
import org.jekajops.vk.commands.admin.*;
import org.jekajops.vk.commands.BackCommand;
import org.jekajops.vk.commands.Command;
import org.jekajops.core.context.Context;
import org.jekajops.vk.answer_listeners.PhoneAnswerListener;
import org.jekajops.core.entities.User;
import org.jekajops.vk.answer_listeners.AnswerListenerManager;
import org.jekajops.core.utils.files.FileManager;
import org.jekajops.core.utils.parsers.VkJsonParser;
import org.openqa.selenium.WebDriverException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.jekajops.core.context.Context.SETTINGS;

public class Messenger implements Runnable {
    private final Message message;
    private final KeyboardManager keyboardManager;
    private boolean needToUpdateKeyboardManager = false;
    private Database database;
    private static final Logger logger = Logger.getGlobal();
    private static final List<String> startWords = List.of("start",
            "begin",
            "начать",
            "привет",
            "старт",
            "начни",
            "ответь",
            "здарова",
            "го",
            "Начать",
            "Привет",
            "Старт",
            "Начни",
            "Ответь");

    public Messenger(Message message, KeyboardManager keyboardManager) {
        this.keyboardManager = keyboardManager;
        this.message = message;
        try {
            database = new Database();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            logger.log(Level.INFO, VkJsonParser.getUserRealNameById(message.getUserId()) + " написал команду: " + message.getBody());
            if (onStart()) return;
            if (onOrder()) return;
            if (onPay()) return;
            if (onCommand()) return;
            if (onAnswer()) return;

            User user;
            user = getUser();
            if (isAdmin(user)) {
                if (onAudio()) return;
                if (onDeleteAudio()) return;
                if (onConfigFile()) return;
            }

            misc();
            onSubscribe(user);
            keyboardManager.updateMainKeyboard();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void misc() {
        keyboardManager.useMainKeyboard();
        sendMessage(SETTINGS.DONT_UNDERSTAND.getDATA(), keyboardManager.buildKeyboard());
    }


    private boolean onStart() {
        if (startWords.contains(message.getBody().toLowerCase())) {
            sendMessage(SETTINGS.HELLO_TEXT.getDATA());
            keyboardManager.updateMainKeyboard();
            return true;
        }
        return false;
    }

    private User getUser() {
        var user = database.getUserByUserId(message.getUserId());
        if (user == null) user = saveUser(message.getUserId());
        return user;
    }

    private Set<AdminCommand> getAdminCommands() {
        var adminCommands = new HashSet<>(Arrays.asList(
                new GetSettingsCommand(keyboardManager),
                new HelpAdminCommand(),
                new DistributionCommand()
        ));
        adminCommands.add(new GetAdminCommandsCommand(adminCommands, keyboardManager));
        return adminCommands;
    }

    private Command getCommand(Collection<Button> buttons, Button lastButton, Message message) {
        String body = message.getBody();

        for (Button button : buttons) {
            if (button.getName().equals(body)) {
                return button.getCommand();
            }
        }
        if (lastButton.getName().equals(body)) return lastButton.getCommand();
        Command back = new BackCommand(keyboardManager);
        Command pay = new PayCommand(keyboardManager);
        if (back.name.equals(body)) return back;
        if (pay.name.equals(body)) return pay;
        for (Button button : keyboardManager.getAllMainButtons()) {
            if (button.getName().equals(body)) {
                return button.getCommand();
            }
        }
        var adminCommands = getAdminCommands();
        if (isAdmin(getUser())) {
            for (Command command : adminCommands) {
                if (command.name.equals(body)) {
                    return command;
                }
            }
        }
        return null;
    }

    private boolean onCommand() {
        Keyboard keyboard = keyboardManager.getKeyboard();
        Collection<Button> buttons = keyboard.getButtons();
        Button lastBtn = keyboardManager.getKeyboard().lastButton();
        Command command = getCommand(buttons, lastBtn, message);
        if (command != null) {
            command.execute(message);
            return true;
        }
        return false;
    }


    protected static record AudioCategory(String category, String subcategory, List<MessageAttachment> messageAttachments) { }

    private List<AudioCategory> getAudioCategories(Message message, Message parent, List<AudioCategory> audioCategories) {
        String text = message.getBody();
        if (text.isEmpty() || text.contains("Чтобы заказать эту открытку, отправьте:")) {
            text = parent.getBody();
        }
        String[] cats = text.split(";");
        String category = "";
        String subcategory = "";
        if (cats.length > 0) {
            category = cats[0];
        }
        if (cats.length == 2) {
            subcategory = cats[1];
        }
        List<MessageAttachment> attachments = message.getAttachments();
        if (attachments != null) {
            audioCategories.add(new AudioCategory(
                    category,
                    subcategory,
                    attachments
            ));
        }
        List<Message> fwdMessages = message.getFwdMessages();
        if (fwdMessages != null && !fwdMessages.isEmpty()) {
            for (Message fwd : fwdMessages) {
                getAudioCategories(fwd, message, audioCategories);
            }
        }
        return audioCategories;
    }

    private String constructAudiofileName(String cat, String subcat, String audioName) {
        String audioFileName = cat + "-" + subcat + "-" + audioName;
        audioFileName = TransliteratorRusToLatin.transliterate(audioFileName);
        audioFileName = audioFileName.replaceAll("[.,/\\\\|!@#$%^&*()=<>?\"':;]", "");
        audioFileName = audioFileName.replaceAll(" ", "_");
        return audioFileName;
    }

    private synchronized boolean onAudio() {
        List<AudioCategory> audioCategories = getAudioCategories(message, message, new ArrayList<>());
        if (audioCategories.isEmpty()
                || audioCategories.get(0).messageAttachments().get(0).getAudio() == null) {
            return false;
        }
        UploadRobot uploadRobot;
        try {
            uploadRobot = new UploadRobot();
        } catch (WebDriverException e) {
            e.printStackTrace();
            return true;
        }
        for (AudioCategory audioCategory : audioCategories) {
            String category = audioCategory.category();
            String subcategory = audioCategory.subcategory();
            for (MessageAttachment attachment : audioCategory.messageAttachments()) {
                Audio audio = attachment.getAudio();
                if (audio == null) continue;
                String vkAudioId = "audio" + audio.getOwnerId() + "_" + audio.getId();
                String audioFileName = constructAudiofileName(category, subcategory, audio.getTitle());
                String audioId = null;
                List<Prank> pranks = database.getPranksByVkAudioId(vkAudioId);
                for (Prank prank : pranks) {
                    if (prank != null
                            && vkAudioId.equals(prank.getVkAudioId())
                            && !prank.getCategory().equals(category)
                            && !prank.getSubcategory().equals(subcategory)) {
                        audioId = prank.getAudioUrl();
                        if (audioId == null) break;
                        database.insertPrank(category, subcategory, audioFileName, vkAudioId, audioId);
                        sendMessage("файл " + audioFileName + " загружен в новую категорию");
                        break;
                    }
                }
                if (pranks.isEmpty() || audioId == null) {
                    sendMessage("Загрузка началась...");
                    try {
                        audioId = uploadAudio(uploadRobot, audio.getUrl(), audioFileName);
                    } catch (LoaderException e) {
                        continue;
                    }
                    if (audioId != null) {
                        database.insertPrank(category, subcategory, audioFileName, vkAudioId, audioId);
                        sendFileDownloaded(audioFileName);
                        logger.log(Level.INFO, audioFileName + " downloaded from vk");
                        keyboardManager.updateMainKeyboard();
                        needToUpdateKeyboardManager = true;
                    } else {
                        sendMessage("Error! Failed to upload file " + audioFileName);
                    }
                } else {
                    sendMessage("файл " + " уже существует");
                }

            }
        }
        sendMessage("загрузка всех файлов окончена.");
        uploadRobot.close();
        return true;
    }

    public String uploadAudio(UploadRobot uploadRobot, String audioUrl, String audioFileName) throws LoaderException {
        String audioId = null;
        try {
            File file = FileManager.download(audioUrl, audioFileName, ".mp3");
            try {
                audioId = uploadRobot.uploadFile(file, audioFileName);
            } catch (WebDriverException e) {
                e.printStackTrace();
                sendMessage("Error! Failed to upload file " + audioFileName);
                throw new LoaderException();
            }
            file.delete();
            this.notify();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (audioId == null) {
            sendMessage("Error! Failed to upload file " + audioFileName);
            throw new LoaderException();
        }
        return audioId;
    }

    private boolean onDeleteAudio() {
        if (!message.getBody().contains("удалить id:")) return false;
        String[] lineSplit = message.getBody().split(":");
        if (lineSplit.length > 1) {
            int prankId = Integer.parseInt(lineSplit[1]);
            database.deletePrank(prankId);
            keyboardManager.updateMainKeyboard();
            sendMessage("Аудиоролик удален!");
            return true;
        }
        return false;
    }

    private boolean onOrder() {
        if (!message.getBody().contains("отправить id:")) return false;
        String[] lineSplit = message.getBody().split(":");
        if (lineSplit.length > 1) {
            int prankId = Integer.parseInt(lineSplit[1]);
            KeyboardBuilder keyboardBuilder = new KeyboardBuilder(false, true);
            keyboardBuilder.addTextButton(new Button(new BackCommand("отмена", keyboardManager), Button.NEGATIVE), 0);
            sendMessage("отправьте в ответ номер телефона жертвы", keyboardBuilder);
            AnswerListenerManager.addAnswerListener(new PhoneAnswerListener(getUser(), prankId));
            return true;
        }
        return false;
    }

    private boolean onAnswer() {
        if (message.getBody().equals("В главное меню")
                || message.getBody().equals("отмена")) {
            AnswerListenerManager.removeAnswerListener(message.getUserId());
            keyboardManager.useMainKeyboard();
            sendMessage("отмена операции", keyboardManager.buildKeyboard());
            return true;
        }
        if (AnswerListenerManager.hasAnswerListeners()) {
            AnswerListener answerListener = AnswerListenerManager.getAnswerListener(message.getUserId());
            if (answerListener != null) {
                answerListener.onAnswer(message);
                return true;
            }
        }
        return false;
    }

    private boolean onPay() {
        if (message.getAttachments() == null) return false;
        if (message.getAttachments().size() < 1) return false;
        MessageAttachment ma = message.getAttachments().get(0);
        if (ma == null) return false;
        Link link = ma.getLink();
        if (link == null) return false;
        logger.log(Level.INFO, link.toString());
        boolean isCaptionOk = link.getCaption().contains("Перевод получен");
        if (!isCaptionOk) return false;
        boolean hasPhoto = link.getPhoto() != null;
        if (!hasPhoto) return false;
        LinkButton lb = link.getButton();
        if (lb == null) return false;
        boolean isLinkBtnHasRightAction = lb.getAction().getType().equals(LinkButtonActionType.OPEN_URL);
        boolean isLinkBtnHasRightUrl = lb.getAction().getUrl().contains("https://vk.com/vkpay#action=history");
        if (!isLinkBtnHasRightAction || !isLinkBtnHasRightUrl) return false;
        String strAmount = link.getTitle().replaceAll("[\\D]", "");
        if (strAmount.isEmpty()) return false;
        int amount = Integer.parseInt(strAmount);
        User user = getUser();
        if (user != null) {
            user.updatePayment(amount);
            String msg = "Баланс пополнен на " + amount + " рублей. Вам доступно " + user.getPranksAvailable() + " розыгрышей";
            sendMessage(msg);
            return true;
        }
        return false;
    }

    private boolean onConfigFile() {
        String kcName = "keyboardConfig";
        String scName = "settings";
        String acName = "audioList";
        List<MessageAttachment> messageAttachments = message.getAttachments();
        if (messageAttachments == null) return false;
        for (MessageAttachment ma : messageAttachments) {
            Doc doc = ma.getDoc();
            if (doc == null) return false;
            String title = doc.getTitle();
            String url = doc.getUrl();
            try {
                if (title.contains(kcName)) {
                    File file = FileManager.download(url, kcName, ".txt");
                    List<String> lines = Files.readAllLines(file.toPath());
                    Categories categories = database.getCategories();

                    Set<Pair<String>> pairs = lines.stream().map(s -> {
                        var pair = s.split(";");
                        var cat = pair[0];
                        var subcat = pair.length > 1 ? pair[1] : "";
                        return new Pair<>(cat, subcat);
                    }).collect(Collectors.toSet());

                    pairs.forEach(pair -> {
                        var cat = pair.getO1();
                        var subcat = pair.getO2();
                        if (!categories.containsPair(cat, subcat)) {
                            database.insertCategory(cat, subcat);
                        }
                    });

                    categories.forEach((k, set) -> set.forEach(v -> {
                        if (!pairs.contains(new Pair<>(k, v))) {
                            database.deleteCategory(k, v);
                        }
                    }));

                    sendFileDownloaded(title);
                    keyboardManager.updateMainKeyboard();
                    keyboardManager.useMainKeyboard();
                    sendMessage("клавиатура обновлена", keyboardManager.buildKeyboard());
                } else if (title.contains(scName)) {
                    File file = FileManager.download(url, scName, ".properties");
                    Properties properties = PropertiesManager.getProperties(file);
                    Settings settings = new Settings();
                    properties.forEach((k, v) -> {
                        var settingsArray = settings.settings;
                        boolean contains = false;
                        for (Settings.Setting setting : settingsArray) {
                            if (setting.getKey().equals(k.toString()) &&
                                    setting.getDATA().toString().equals(v.toString())) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains) {
                            database.insertSetting((String) k, (String) v);
                        }
                    });
                    sendFileDownloaded(title);
                    SETTINGS.update();
                } else if (title.contains(acName)) {
                    File forDelete = Context.AUDIO_LIST_FILE;
                    Context.AUDIO_LIST_FILE = FileManager.download(url, acName, ".txt");
                    forDelete.delete();
                    sendFileDownloaded(title);
                    List<String> newAudios = FileManager.readFile(FileManager.getFileFromResources("config/" + acName + ".txt"));
                    List<String> oldAudios = database.getAudios();
                    int rym = Math.max(newAudios.size(), oldAudios.size());
                    for (int i = 0; i < rym; i++) {
                        String na = newAudios.get(i);
                        String oa = oldAudios.get(i);
                        if (!newAudios.contains(oa)) {
                            database.deletePrank(oa);
                        } else if (!oldAudios.contains(na)) {
                            sendMessage("Ошибка в конфиг файле! Нет аудиозаписи '" + na + "' в базе данных! Добавьте аудиозапись через сообщения бота с сообщением: категория;подкатегория");
                        }
                    }
                }
            } catch (IOException | SQLException mue) {
                mue.printStackTrace();
                sendMessage("Ошибка! Не удалось сохранить конфиг");
            }
        }
        return true;
    }

    private void onSubscribe(User user) {
        if (SETTINGS.GIFTS_ON.getDATA()) {
            if (!user.hasRole(User.Role.SUBSCRIBED)) {
                boolean isSubscriber = new VKManager().isSubscriber(message.getUserId());
                if (isSubscriber) {
                    user.addRole(User.Role.SUBSCRIBED);
                    database.updateUserBalance(message.getUserId(), user.getBalance() + SETTINGS.PRANK_COST.getDATA());
                    database.updateUserRoles(message.getUserId(), user.getRoles());
                    sendMessage("Вам подарок за подписку! Теперь вам доступен один пранк бесплатно!");
                }
            }
        }
    }

    private boolean isAdmin(User user) {
        return user != null && user.isAdmin();
    }

    public synchronized boolean isNeedToUpdateKeyboardManager() {
        return needToUpdateKeyboardManager;
    }

    public synchronized void setNeedToUpdateKeyboardManager(boolean needToUpdateKeyboardManager) {
        this.needToUpdateKeyboardManager = needToUpdateKeyboardManager;
    }

    private void sendMessage(String msg, KeyboardBuilder keyboardBuilder) {
        new VKManager().sendMessage(msg, message.getUserId(), keyboardBuilder);
    }

    private void sendMessage(String msg) {
        sendMessage(msg, null);
    }

    private User saveUser(int userId) {
        return database.insertUser(VkJsonParser.getUserRealNameById(userId), userId);
    }

    private void sendFileDownloaded(String audioFileName) {
        sendMessage("Файл " + audioFileName + " успешно загружен!");
    }
}
