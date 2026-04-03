package com.samp.online.gui.twitter;

public class TwitterData {
    String mName;
    String mMsg;
    String mUrl;
    int mduration;

    TwitterData(String msg, int duration, String url, String playername){
        this.mName = playername;
        this.mMsg = msg;
        this.mUrl = url;
        this.mduration = duration;
    }

    public String getName() {
        return this.mName;
    }
    public void setName(String name) {
        this.mName = name;
    }
    public int getDuration() {
        return this.mduration;
    }
    public void setDuration(int duration) {
        this.mduration = duration;
    }
    public String getMsg() {
        return this.mMsg;
    }
    public void setMsg(String msg) {
        this.mMsg = msg;
    }

    public String getUrl(){return this.mUrl;}
    public void setUrl(String url) {
        this.mUrl = url;
    }
}

