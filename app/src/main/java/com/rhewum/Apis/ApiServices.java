package com.rhewum.Apis;

import com.rhewum.Activity.Pojo.MainJsonNews;
import com.rhewum.Activity.Pojo.SubArrayNews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;



public interface ApiServices {
    @GET("api/articles")
    Call<MainJsonNews> getNews(@Query("page") int page, @Query("password") String password);
}
