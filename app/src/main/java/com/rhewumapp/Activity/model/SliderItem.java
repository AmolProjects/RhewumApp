package com.rhewumapp.Activity.model;

public class SliderItem {
    private String imageUrl;
    private String title;

    private String content;
    private Boolean isRead;


    public SliderItem() {
    }

    public SliderItem(String imageUrl, String title, String content, Boolean isRead) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
        this.isRead = isRead;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
