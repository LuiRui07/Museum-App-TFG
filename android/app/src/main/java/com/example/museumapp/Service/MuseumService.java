package com.example.museumapp.Service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.museumapp.Activities.Home;
import com.example.museumapp.Activities.Obras;
import com.example.museumapp.Adapters.MuseosAdapter;
import com.example.museumapp.Api.ApiClient;
import com.example.museumapp.Api.ApiService;
import com.example.museumapp.Api.LoginRequest;
import com.example.museumapp.Api.Response;
import com.example.museumapp.Models.Museum;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class MuseumService {

    private ApiService apiService;

    private Context context;

    public MuseumService(Context context) {
        Retrofit retrofit = ApiClient.addHeader(context);
        apiService = retrofit.create(ApiService.class);

        this.context = context;
    }

    public interface MuseumCallback {
        void onSuccess(Museum museum, int tipo, List<Museum> museos, Context context);

        void onFailure(String errorMessage);
    }

    public void getMuseumFromCoords(double lat, double lon, int tipo, MuseumCallback callback){

        Call<Museum> call = apiService.getMuseumFromCoords(lat,lon);

        final Museum[] result = new Museum[1];
        call.enqueue(new retrofit2.Callback<Museum>() {
            @Override
            public void onResponse(Call<Museum> call, retrofit2.Response<Museum> response) {
                if (response.isSuccessful()) {
                        Log.e("Respuesta Museo", response.message());
                        result[0] = response.body();
                        Log.e("Museo Encontrado----", result[0].toString());
                        if (result[0].getId() == null){
                            callback.onFailure("DestroyButton");
                        } else {
                            callback.onSuccess(result[0],tipo,null,null);

                        }
                    } else {
                        if (tipo == 1){
                            callback.onFailure("No esta en ningun museo");
                        }
                        Log.e("Respuesta Museo NULA", response.message());
                    }
            }
            @Override
            public void onFailure(Call<Museum> call, Throwable t) {
                Log.e("Respuesta Museo", t.getMessage());
            }
        });
    }

    public void getAllMuseums(MuseumCallback callback) {

        Call<List<Museum>> call = apiService.getAllMuseum();
        call.enqueue(new Callback<List<Museum>>() {
            @Override
            public void onResponse(Call<List<Museum>> call, retrofit2.Response<List<Museum>> response) {
                if (response.isSuccessful()) {
                    List<Museum> museos = response.body();
                    Log.d("GetObras", museos.toString());
                    callback.onSuccess(null,-1,museos,context);
                } else {
                    Log.d("GetObras", response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Museum>> call, Throwable t) {
                Log.e("GetObrasFail", "Error en la solicitud: " + t.getMessage());
            }
        });
    }

    public void getMuseumFromId(String id, MuseumCallback callback) {
        Call<Museum> call = apiService.getMuseumFromId(id);
        call.enqueue(new Callback<Museum>() {
            @Override
            public void onResponse(Call<Museum> call, retrofit2.Response<Museum> response) {
                if (response.isSuccessful()) {
                    Museum museo = response.body();
                    Log.d("GetMuseoFromId", museo.toString());
                    callback.onSuccess(museo,-1,null,null);
                } else {
                    Log.d("GetObras", response.message());
                }
            }

            @Override
            public void onFailure(Call<Museum> call, Throwable t) {
                Log.e("GetObrasFail", "Error en la solicitud: " + t.getMessage());
            }
        });
    }

}
