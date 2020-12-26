package org.jekajops.core.utils.files;

public class AudioFile {
    String url, vkAudioId, audioName;

    public AudioFile(String url, String vkAudioId, String audioName) {
        this.url = url;
        this.vkAudioId = vkAudioId;
        this.audioName = audioName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVkAudioId() {
        return vkAudioId;
    }

    public String getAudioName() {
        return audioName;
    }
}