package com.shibashortener.models;

import com.shibashortener.models.embedded.DailyStats;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "url_stats")
public class Stats implements Serializable {

    @Id
    String id_url;
    List<DailyStats> dailyStats = new ArrayList<>();
    String browser;
    String os;
    String createdAt;


    public Stats(String id_url, String browser, String os, String createdAt) {
        this.id_url = id_url;
        this.browser = browser;
        this.os = os;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id_url;
    }

    public void setId(String id) {
        this.id_url = id;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public List<DailyStats> getDailyStats() {
        return dailyStats;
    }

    public void setDailyStats(List<DailyStats> dailyStats) {
        this.dailyStats = dailyStats;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void initCounter(String date){
        this.dailyStats.add(new DailyStats(date));
    }

    public void addDailyReport(DailyStats dailyReport) {
        this.dailyStats.add(dailyReport);
    }
}
