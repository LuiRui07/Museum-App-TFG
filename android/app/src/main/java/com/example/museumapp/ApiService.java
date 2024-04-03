package com.example.museumapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("users/")
    Call<Response> getAllUsers();

    @POST("users/login/")
    Call<Response> loginUser(@Body LoginRequest loginRequest);

    @GET("users/check")
    Call<Response> checkExists(@Query("mail") String mail, @Query("user") String user);

}