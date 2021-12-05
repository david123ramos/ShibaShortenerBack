package com.shibashortener.models.embedded;

import java.io.Serializable;

public class Visitor implements Serializable {

    String visitorIp;
    String visitorBrowser;
    String visitorOs;
    String datetime;

    public Visitor(){}

    public Visitor(String visitorIp, String visitorBrowser, String visitorOs, String datetime) {
        this.visitorIp = visitorIp;
        this.visitorBrowser = visitorBrowser;
        this.visitorOs = visitorOs;
        this.datetime = datetime;
    }

    public String getVisitorIp() {
        return visitorIp;
    }

    public void setVisitorIp(String visitorIp) {
        this.visitorIp = visitorIp;
    }

    public String getVisitorBrowser() {
        return visitorBrowser;
    }

    public void setVisitorBrowser(String visitorBrowser) {
        this.visitorBrowser = visitorBrowser;
    }

    public String getVisitorOs() {
        return visitorOs;
    }

    public void setVisitorOs(String visitorOs) {
        this.visitorOs = visitorOs;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
