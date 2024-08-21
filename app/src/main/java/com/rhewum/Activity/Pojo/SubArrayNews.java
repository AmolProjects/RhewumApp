package com.rhewum.Activity.Pojo;

import java.util.ArrayList;
import java.util.Date;

public class SubArrayNews {
    public String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMeta_description() {
        return meta_description;
    }

    public void setMeta_description(String meta_description) {
        this.meta_description = meta_description;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getTeaser_image() {
        return teaser_image;
    }

    public void setTeaser_image(String teaser_image) {
        this.teaser_image = teaser_image;
    }

    public String getTeaser_image_alt() {
        return teaser_image_alt;
    }

    public void setTeaser_image_alt(String teaser_image_alt) {
        this.teaser_image_alt = teaser_image_alt;
    }

    public Object getHeader_image() {
        return header_image;
    }

    public void setHeader_image(Object header_image) {
        this.header_image = header_image;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMain_category() {
        return main_category;
    }

    public void setMain_category(String main_category) {
        this.main_category = main_category;
    }

    public ArrayList<ArrayList<String>> getTags() {
        return tags;
    }

    public void setTags(ArrayList<ArrayList<String>> tags) {
        this.tags = tags;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String meta_description;
    public String headline;
    public String teaser_image;
    public String teaser_image_alt;
    public Object header_image;
    public Date date;
    public String city;
    public String author;
    public String main_category;
    public ArrayList<ArrayList<String>> tags;
    public String teaser;
    public String content;
    public String url;
}

