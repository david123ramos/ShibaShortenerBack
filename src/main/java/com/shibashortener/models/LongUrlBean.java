package com.shibashortener.models;

public class LongUrlBean {

    private String longUrl;

    public LongUrlBean(String longUrl) {
        this.longUrl = longUrl;
    }

    public LongUrlBean() {}

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }
}
