package com.samp.online.launcher.network;

import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;
public interface Api {

    @GET("/SAMPGAMES/api.json")
    Call<Links> getLinks();

    @GET("/SAMPGAMES/files.json")
    Call<List<FilesList>> getFiles();
}
