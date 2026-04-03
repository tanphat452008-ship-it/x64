package com.samp.online.launcher.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FilesList {
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("size")
    @Expose
    private String size;

    @SerializedName("hash")
    @Expose
    private String hash;

    @SerializedName("path")
    @Expose
    private String path;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("ignore")
    @Expose
    private String ignore;

    // copy matrp by EDGAR DEVELOPER / by EDGAR 3.0 https://github.com/edgar-code
    // created at 13.01.2024

    // Конструктор
    public FilesList(String name, String size, String hash, String path, String url, String ignore) {
        this.name = name;
        this.size = size;
        this.hash = hash;
        this.path = path;
        this.url = url;
        this.ignore = ignore;
    }

    // Геттеры
    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public String getHash() {
        return hash;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public String getIgnore() {
        return ignore;
    }
}
