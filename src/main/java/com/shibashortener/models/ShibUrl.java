package com.shibashortener.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "urls")
public class ShibUrl {

    @Id
    private String id;
    private String shortenedUrl;
    private String longUrl;
    private String expirationTime;

    public ShibUrl(String id, String shortenedUrl, String longUrl, String expirationTime) {
        this.id = id;
        this.shortenedUrl = shortenedUrl;
        this.longUrl = longUrl;
        this.expirationTime = expirationTime;
    }

    public ShibUrl(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortenedUrl() {
        return shortenedUrl;
    }

    public void setShortenedUrl(String shortenedUrl) {
        this.shortenedUrl = shortenedUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }
}
