package com.shibashortener.models.embedded;

import java.io.Serializable;

public class DailyStats implements Serializable {

    private String date;
    private long visits = 0;

    public void incrementVisits() {this.visits++;}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getVisits() {
        return visits;
    }

    public void setVisits(long visits) {
        this.visits = visits;
    }
}
