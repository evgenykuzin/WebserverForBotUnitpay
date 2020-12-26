package org.jekajops.core.utils.files;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.HttpUriRequest;
import org.jekajops.call_api.exceptions.LoaderException;
import org.jekajops.core.http.body_builders.JsonBodyBuilder;
import org.jekajops.core.http.headers.Header;
import org.jekajops.core.http.headers.HeadersModelImpl;
import org.jekajops.core.http.models.RequestModel;
import org.jekajops.core.http.models.ResponseModel;
import org.jekajops.core.http.services.DefaultHttpService;
import org.jekajops.core.http.services.HttpService;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.jekajops.core.utils.files.FileManager.download;

public class FileFormatConverter {

    public static List<AudioFile> convertInFormat(String format, List<AudioFile> audioFiles) throws IOException, UnsupportedAudioFileException {
        DefaultHttpService service = new DefaultHttpService();
        String host = "https://api2.online-convert.com/";

        RequestModel<HttpUriRequest> requestModel = service.constructRequest(
                host + "jobs",
                HttpService.POST,
                new HeadersModelImpl(
                        new Header("X-Oc-Api-Key", "ac26584ad39404680245904c0eaa25d6"),
                        Header.APP_JSON
                ),
                (JsonBodyBuilder) (object) -> {
                    JsonArray input = new JsonArray();
                    for (AudioFile file : audioFiles) {
                        JsonObject inObj = new JsonObject();
                        inObj.addProperty("type", "remote");
                        inObj.addProperty("source", file.getUrl());
                        input.add(inObj);
                    }
                    JsonArray conversion = new JsonArray();
                    JsonObject convObj = new JsonObject();
                    JsonObject options = new JsonObject();
                    options.addProperty("frequency", 8000);
                    options.addProperty("channels", "mono");
                    convObj.add("options", options);
                    convObj.addProperty("target", format);
                    convObj.addProperty("category", "audio");
                    for (int i = 0; i < audioFiles.size(); i++) {
                        conversion.add(convObj);
                    }
                    object.add("input", input);
                    object.add("conversion", conversion);
                    return new JsonBodyBuilder.BodyJson(object);
                }
        );
        ResponseModel responseModel = service.getResponse(requestModel);
        if (responseModel.getCode() > 201)
            throw new IOException("Error in response: " + responseModel.toString());
        System.out.println(responseModel);
        String id = new JsonParser()
                .parse(responseModel.getResponseString())
                .getAsJsonObject()
                .get("id")
                .getAsString();
        System.out.println(id);
        return getConvertedUrls(id, audioFiles, format);
    }

    private static List<AudioFile> getConvertedUrls(String id, List<AudioFile> audioFiles, String format) throws IOException {
        DefaultHttpService service = new DefaultHttpService();
        String host = "https://api2.online-convert.com/";
        RequestModel<HttpUriRequest> getReq;
        ResponseModel getResp;
        int attemts = 0;
        do {
            getReq = service.constructRequest(
                    host + "jobs/" + id,
                    HttpService.GET,
                    new HeadersModelImpl(new Header("X-Oc-Api-Key", "ac26584ad39404680245904c0eaa25d6")),
                    JsonBodyBuilder.NO_BODY
            );
            getResp = service.getResponse(getReq);
            System.out.println(getResp);
            attemts++;
            JsonArray errors = new JsonParser().parse(getResp.getResponseString())
                    .getAsJsonObject()
                    .get("errors")
                    .getAsJsonArray();
            if (attemts > 40 || errors.size() > 0) {
                throw new IOException("could not convert file. Errors: " + errors.toString());
            }
        } while (!checkConvertStatus(getResp));
        System.out.println(getResp);
        JsonArray input = new JsonParser()
                .parse(getResp.getResponseString())
                .getAsJsonObject()
                .get("output")
                .getAsJsonArray();
        int limit = Math.min(input.size(), audioFiles.size());
        for (int i = 0; i < limit; i++) {
            String url = input.get(i).getAsJsonObject().get("uri").getAsString();
            System.out.println("converted url: " + url);
            File file = download(url, "audio", "." + format);
            try {
                url = FTPManger.uploadToFTP(file);
            } catch (LoaderException e) {
                e.printStackTrace();
                audioFiles.get(i).setUrl(null);
                continue;
            }
            System.out.println("uploaded url: " + url);
            audioFiles.get(i).setUrl(url);
        }
        return audioFiles;
    }

    private static boolean checkConvertStatus(ResponseModel response) {
        return new JsonParser()
                .parse(response.getResponseString())
                .getAsJsonObject()
                .getAsJsonObject("status")
                .get("code")
                .getAsString()
                .equals("completed");
    }

    public static List<AudioFile> convertFilesListToWav(List<AudioFile> audioFiles) throws IOException, UnsupportedAudioFileException {
        for (AudioFile audioFile : audioFiles) {
            var url = audioFile.getUrl();
            File convFile = convertFileToWav(download(url, "audio", ".wav"));
            String convertedUrl;
            try {
                convertedUrl = FTPManger.uploadToFTP(convFile);
                System.out.println("convertedUrl: " + convertedUrl);
            } catch (LoaderException e) {
                e.printStackTrace();
                audioFile.setUrl(null);
                continue;
            }
            audioFile.setUrl(convertedUrl);
        }
        return audioFiles;
    }

    public static File convertFileToWav(File file) throws IOException, UnsupportedAudioFileException {
        //AudioFormat inFormat = AudioSystem.getAudioFileFormat(file).getFormat();
        long fileSize = file.length();
        int frameSize = 160;
        long numFrames = fileSize / frameSize;
        //AudioFormat audioFormat = new AudioFormat(inFormat.getEncoding(), 8000, inFormat.getSampleSizeInBits(), 1, inFormat.getFrameSize(), inFormat.getFrameRate(), inFormat.isBigEndian());
        AudioFormat audioFormat = new AudioFormat(8000, 16, 1, true, true);
        AudioInputStream audioInputStream = new AudioInputStream(new FileInputStream(file), audioFormat, numFrames);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, file);
        return file;
    }

}
