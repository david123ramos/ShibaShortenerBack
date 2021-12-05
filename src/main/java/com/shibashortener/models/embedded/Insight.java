package com.shibashortener.models.embedded;

import java.io.Serializable;
import java.util.Map;

public class Insight implements Serializable {

    private String id;
    private String createdAt;
    private int existingDays;
    private int totalClicks;
    private double clicksPerDay;
    private Map<String, Integer> visitorsByOs;
    private String mostVistedPeriod;

    public Insight(String id, String createdAt, int existingDays, int totalClicks, double clicksPerDay, Map<String, Integer> visitorsByOs, String mostVistedPeriod) {
        this.id = id;
        this.createdAt = createdAt;
        this.existingDays = existingDays;
        this.totalClicks = totalClicks;
        this.clicksPerDay = clicksPerDay;
        this.visitorsByOs = visitorsByOs;
        this.mostVistedPeriod = mostVistedPeriod;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getExistingDays() {
        return existingDays;
    }

    public void setExistingDays(int existingDays) {
        this.existingDays = existingDays;
    }

    public int getTotalClicks() {
        return totalClicks;
    }

    public void setTotalClicks(int totalClicks) {
        this.totalClicks = totalClicks;
    }

    public double getClicksPerDay() {
        return clicksPerDay;
    }

    public void setClicksPerDay(double clicksPerDay) {
        this.clicksPerDay = clicksPerDay;
    }

    public Map<String, Integer> getVisitorsByOs() {
        return visitorsByOs;
    }

    public void setVisitorsByOs(Map<String, Integer> visitorsByOs) {
        this.visitorsByOs = visitorsByOs;
    }

    public String getMostVistedPeriod() {
        return mostVistedPeriod;
    }

    public void setMostVistedPeriod(String mostVistedPeriod) {
        this.mostVistedPeriod = mostVistedPeriod;
    }
}
