package org.jekajops.core.context;

import org.jekajops.core.database.Database;

import java.sql.SQLException;
import java.util.*;

@lombok.ToString(exclude = "settingsMap")
public class Settings {
    volatile public Setting<Integer> PRANK_COST;
    volatile public Setting<Integer> MAX_PRANKS_TO_SEND;
    volatile public Setting<Boolean> GIFTS_ON;
    volatile public Setting<Integer> DISCOUNT_PRANK_COST;
    volatile public Setting<String> HELLO_TEXT;
    volatile public Setting<String> DONT_UNDERSTAND;
    volatile public Setting[] settings;
    volatile public Map<String, Object> settingsMap = new HashMap<>();
    volatile public Map<String, String> keyRusEnMap = new HashMap<>();

    public Settings() throws SQLException {
        keyRusEnMap.put("стоимость пранка", "stoimost_pranka");
        keyRusEnMap.put("макс. кол-во пранков в сообщении", "max_kolichestvo_prankov_v_soobchenii");
        keyRusEnMap.put("включить подарок за подписку?", "podarok_za_podpisku");
        keyRusEnMap.put("стоимость пранка для избранных", "skidka_dlya_polzovateley");
        keyRusEnMap.put("текст приветствия", "text_privetstviya");
        keyRusEnMap.put("текст для нераспознанной команды", "dont_understand");
        update();
    }

    public void update() throws SQLException {
        PRANK_COST = getIntProperty("stoimost_pranka");
        MAX_PRANKS_TO_SEND = getIntProperty("max_kolichestvo_prankov_v_soobchenii");
        GIFTS_ON = getBooleanProperty("podarok_za_podpisku?");
        DISCOUNT_PRANK_COST = getIntProperty("skidka_dlya_polzovateley");
        HELLO_TEXT = getProperty("text_privetstviya");
        DONT_UNDERSTAND = getProperty("dont_understand");
        settings = new Setting[]{
                PRANK_COST,
                MAX_PRANKS_TO_SEND,
                GIFTS_ON,
                DISCOUNT_PRANK_COST,
                HELLO_TEXT,
                DONT_UNDERSTAND
        };
        for (Setting setting : settings) {
            settingsMap.put(setting.getKey(), setting.getDATA());
        }
    }

    private Setting<Boolean> getBooleanProperty(String name, String prop) {
        if (prop == null) return new Setting<>(name, false);
        return new Setting<>(name, anyEquals(prop,
                "yes", "da", "aga", "yeah", "+", "вкл", "да", "включить", "true", "включи", "ок", "го"));
    }

    private Setting<Boolean> getBooleanProperty(String name) throws SQLException {
        String prop = getProperty(name).getDATA();
        return getBooleanProperty(name, prop);
    }

    private Setting<Integer> getIntProperty(String name, String prop) {
        if (prop == null) return new Setting<>(name, 0);
        return new Setting<>(name, Integer.parseInt(prop));
    }

    private Setting<Integer> getIntProperty(String name) throws SQLException {
        String prop = getProperty(name).getDATA();
        return getIntProperty(name, prop);
    }

    private Setting<String> getProperty(String name) throws SQLException {
        return new Setting<>(name, new Database().getSetting(name));
    }

    private Setting<String[]> getArrayProperty(String name, String prop) {
        if (prop == null) return new Setting<>(name, new String[0]);
        return new Setting<>(name, prop.replaceAll("[\\s\\[\\]{}]", "").split(","));
    }

    private Setting<String[]> getArrayProperty(String name) throws SQLException {
        String prop = getProperty(name).getDATA();
        return getArrayProperty(name, prop);
    }

    private Setting<List<Integer>> getIntListProperty(String name, String prop) {
        Setting<String[]> setting = getArrayProperty(name, prop);
        String[] strArr = setting.getDATA();
        List<Integer> intList = new ArrayList<>(strArr.length);
        Arrays.stream(strArr).forEach(e -> intList.add(Integer.valueOf(e)));
        return new Setting<>(name, intList);
    }

    private Setting<List<Integer>> getIntListProperty(String name) throws SQLException {
        String prop = getProperty(name).getDATA();
        return getIntListProperty(name, prop);
    }

    private boolean anyEquals(String o1, Object... objects) {
        for (Object o : objects) {
            if (o.equals(o1)) return true;
        }
        return false;
    }

    public static class Setting<T> {
        private final String key;
        private T DATA;

        public Setting(String key, T DATA) {
            this.key = key;
            this.DATA = DATA;
        }

        public T getDATA() {
            return DATA;
        }

        public void setDataBySetting(Setting setting) {
            setDATA(setting.getDATA());
        }

        @SuppressWarnings("Un")
        public void setDATA(Object DATA) {
            this.DATA = (T) DATA;
            try {
                new Database().updateSetting(key, DATA.toString());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        public String getKey() {
            return key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Setting<?> setting = (Setting<?>) o;
            return Objects.equals(key, setting.key) &&
                    Objects.equals(DATA, setting.DATA);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, DATA);
        }

        @Override
        public String toString() {
            return "{" +
                    key +
                    " = "
                    + DATA +
                    '}';
        }
    }

}
