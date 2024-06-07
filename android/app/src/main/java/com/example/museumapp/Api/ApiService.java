package com.example.museumapp.Api;

import com.example.museumapp.Models.Museum;
import com.example.museumapp.Models.Obra;
import com.example.museumapp.Models.Route;
import com.example.museumapp.Models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {


    //------------ Usuarios

    @POST("users/login/")
    Call<Response> loginUser(@Body LoginRequest loginRequest);

    @POST("users/google/")
    Call<Response> loginGoogle(@Body GoogleLoginRequest loginRequest);

    @POST("users")
    Call<Response> createUser(@Body UserBody userBody);

    //------------ Obras
    @GET("art/")
    Call<List<Obra>> getAllArt();

    @GET("art/obrasMuseo/{id}")
    Call<List<Obra>> getObrasFromMuseum(@Path("id") String id);

    @GET("art/{id}")
    Call<Obra> getObrasFromId(@Path("id") String id);

    //------------Museos
    @GET("museum/")
    Call<List<Museum>> getAllMuseum();

    @GET("museum/{id}")
    Call<Museum> getMuseumFromId(@Path("id") String id);

    @GET("museum/fromCoords/{lat}/{lon}")
    Call<Museum> getMuseumFromCoords(@Path("lat") double lat, @Path("lon") double lon);

    //------------Recorridos
    @GET("route/")
    Call<List<Route>> getAllRoutes(); // Deberia ser la ruta del usuario solo

    @GET("route/user/{id}")
    Call<List<Route>> getRouteFromUser(@Path("id") String id);

    @GET("route/user/{id}/museum/{museum}")
    Call<List<Route>> getRouteFromUserAndMuseum(@Path("id") String id, @Path("museum") String idMuseum);


}