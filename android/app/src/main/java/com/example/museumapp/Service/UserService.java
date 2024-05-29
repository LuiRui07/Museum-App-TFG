package com.example.museumapp.Service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.museumapp.Activities.Login;
import com.example.museumapp.Api.ApiClient;
import com.example.museumapp.Api.ApiService;
import com.example.museumapp.Api.LoginRequest;
import com.example.museumapp.Api.Response;
import com.example.museumapp.Api.UserBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class UserService {

    private ApiService apiService;

    public UserService(Context context) {
        Retrofit retrofit = ApiClient.addHeader(context);
        apiService = retrofit.create(ApiService.class);
    }

    public interface UserCallback {
        void onSuccess(String[] result);
        void onFailure(String errorMessage);
    }

    public void loginUser(String user, String contra, UserCallback callback) {
        final String[] result = new String[2];
        result[0] = "0";
        LoginRequest loginRequest = new LoginRequest(user, contra);
        Call<com.example.museumapp.Api.Response> call = apiService.loginUser(loginRequest);
        call.enqueue(new Callback<com.example.museumapp.Api.Response>() {
            @Override
            public void onResponse(Call<com.example.museumapp.Api.Response> call, retrofit2.Response<com.example.museumapp.Api.Response> response) {
                if (response.isSuccessful()) {
                    Response loginResponse = response.body();
                    String message = loginResponse.getMessage();
                    Log.d("LoginResponseSuccesFul", response.toString());
                    if (message.contains("1")) {
                        result[0] = "1";
                        result[1] = loginResponse.getMail();
                        callback.onSuccess(result);
                    } else {
                        callback.onFailure("Contraseña o Usuario Incorrecto");
                    }
                } else {
                    callback.onFailure("Error en la respuesta del servidor");
                }
            }

            @Override
            public void onFailure(Call<com.example.museumapp.Api.Response> call, Throwable t) {
                Log.e("LoginResponse", "Error en la solicitud: " + t.toString());
                callback.onFailure("Error en la solicitud: " + t.toString());
            }
        });
    }

    public void registerUser(String username, String correo, String pass, UserCallback callback){

        UserBody userBody = new UserBody(username,correo,pass);
        Call<Response> call = apiService.createUser(userBody);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    Log.d("Create?", message);
                    if (message.contains("1")) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(message);
                    }
                } else {
                    Log.d("LoginResponse", response.message());
                }
            }
            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.e("LoginResponse", "Error en la solicitud: " + t.getMessage());
            }
        });


    }
}
