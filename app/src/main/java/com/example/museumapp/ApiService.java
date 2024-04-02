package com.example.museumapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("users/")
    Call<LoginResponse> getAllUsers();

    @POST("users/login/")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

}