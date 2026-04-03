package com.samp.online.launcher.network;

import com.google.gson.annotations.SerializedName;
import com.samp.online.BuildConfig;

public class Links {
    @SerializedName("URL_CLIENT")
    private String URL_CLIENT;
    @SerializedName("URL_GAME_FILES")
    private String URL_GAME_FILES;
    @SerializedName("URL_GAME_FILES_UPD")
    private String URL_GAME_FILES_UPDATE;
    @SerializedName("clientVersionCode")
    private Integer targetClientVersion = BuildConfig.VERSION_CODE;
    @SerializedName("gameFilesVersionCode")
    private Integer targetGameFilesVersion;

    public Links() { }

    public final Integer getTargetClientVersion() {
        return targetClientVersion;
    }

    public final Integer getTargetGameFilesVersion() {
        return targetGameFilesVersion;
    }

    public final String getUrlClient() {
        return URL_CLIENT;
    }
    public final String getUrlFiles() {
        return URL_GAME_FILES;
    }
    public final String getUrlFilesUpdate() {
        return URL_GAME_FILES_UPDATE;
    }
}
