package com.rhewum.Apis;

import com.rhewum.Activity.Pojo.MainJsonNews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiServices {
    @GET("password=")
    Call<MainJsonNews> getMainNews(@Path("password")String password);
}
