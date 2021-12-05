package com.shibashortener.models.embedded;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DailyStats implements Serializable {

    private String date;
    private long visits = 0;
    private List<Visitor> visitors = new ArrayList<>();

    public DailyStats(String date) {
        this.date = date;
    }

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

    public void addVisitor(Visitor v){
        this.visitors.add(v);
    }
}
