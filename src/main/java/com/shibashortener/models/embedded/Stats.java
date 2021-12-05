package com.shibashortener.models.embedded;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Stats implements Serializable {


    List<DailyStats> dailyStats = new ArrayList<>();
    String browser;
    String device;


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

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
