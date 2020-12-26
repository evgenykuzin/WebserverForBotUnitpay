package org.jekajops.core.entities;

import org.jekajops.vk.buttons.Button;
import org.jekajops.vk.buttons.KeyboardBuilder;

import java.io.File;
import java.util.Objects;

public class Prank implements Comparable<Prank> {
    private final int id;
    private String category;
    private String subcategory;
    private String text;
    private File audioFile;
    private String vkAudioId;
    private String audioUrl;
    private int rating;
    private KeyboardBuilder keyboardBuilder;

    public Prank(int id, String category, String subcategory, String text, String vkAudioId, String audioUrl, int rating) {
        this.id = id;
        this.category = category;
        this.subcategory = subcategory;
        this.text = text;
        this.vkAudioId = vkAudioId;
        this.audioUrl = audioUrl;
        this.rating = rating;
        keyboardBuilder = buildButton("отправить id:" + id);
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public File getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    public String getVkAudioId() {
        return vkAudioId;
    }

    public void setVkAudioId(String vkAudioId) {
        this.vkAudioId = vkAudioId;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public KeyboardBuilder getKeyboardBuilder() {
        return keyboardBuilder;
    }

    public void setKeyboardBuilder(KeyboardBuilder keyboardBuilder) {
        this.keyboardBuilder = keyboardBuilder;
    }

    private static KeyboardBuilder buildButton(String name) {
        KeyboardBuilder keyboardBuilder = new KeyboardBuilder(false, true);
        keyboardBuilder.addTextButton(new Button(name, Button.POSITIVE), 0);
        return keyboardBuilder;
    }

    @Override
    public String toString() {
        return "Prank{" +
                "category='" + category + '\'' +
                ", subcategory='" + subcategory + '\'' +
                ", text='" + text + '\'' +
                ", audioFile=" + audioFile +
                ", vkAudioId='" + vkAudioId + '\'' +
                ", callAudioId=" + audioUrl +
                ", rating=" + rating +
                ", keyboardBuilder=" + keyboardBuilder +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prank prank = (Prank) o;
        return audioUrl.equals(prank.audioUrl) &&
                rating == prank.rating &&
                Objects.equals(category, prank.category) &&
                Objects.equals(subcategory, prank.subcategory) &&
                Objects.equals(text, prank.text) &&
                Objects.equals(audioFile, prank.audioFile) &&
                Objects.equals(vkAudioId, prank.vkAudioId) &&
                Objects.equals(keyboardBuilder, prank.keyboardBuilder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, subcategory, text, audioFile, vkAudioId, audioUrl, rating, keyboardBuilder);
    }

    @Override
    public int compareTo(Prank o) {
        return Integer.compare(rating, o.getRating());
    }
}
