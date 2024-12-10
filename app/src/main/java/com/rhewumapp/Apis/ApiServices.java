package com.rhewumapp.Apis;

import com.rhewumapp.Activity.Pojo.MainJsonNews;
import com.rhewumapp.Activity.Pojo.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;



public interface ApiServices {
    @GET("api/articles")
    Call<MainJsonNews> getNews(@Query("page") int page, @Query("password") String password);
    @POST("api/firebase/test")
    Call<Notifications> postNotificationData(@Query("password") String password, @Body Notifications requestBody);
}
