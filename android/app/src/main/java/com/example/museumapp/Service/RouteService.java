package com.example.museumapp.Service;

import android.content.Context;
import android.util.Log;

import com.example.museumapp.Adapters.RutasAdapter;
import com.example.museumapp.Api.ApiClient;
import com.example.museumapp.Api.ApiService;
import com.example.museumapp.Models.Route;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RouteService {
    private ApiService apiService;

    public RouteService(Context context) {
        Retrofit retrofit = ApiClient.addHeader(context);
        apiService = retrofit.create(ApiService.class);
    }

    public interface RouteCallback {
        void onSuccess(List<Route> rutas);
    }

    public void getAllRoutes (RouteCallback callback) {
        Call<List<Route>> call = apiService.getAllRoutes();
        call.enqueue(new Callback<List<Route>>() {
            @Override
            public void onResponse(Call<List<Route>> call, retrofit2.Response<List<Route>> response) {
                if (response.isSuccessful()) {
                    List<Route> rutas = response.body();
                    Log.d("GetRutas", rutas.toString());
                    callback.onSuccess(rutas);
                } else {
                    Log.d("GetRutas", response.message());
                }
            }
            @Override
            public void onFailure(Call<List<Route>> call, Throwable t) {
                Log.e("GetRutasFail", "Error en la solicitud: " + t.getMessage());
            }
        });
    }

    public void getRoutesFromUser (String id , RouteCallback callback) {
        Call<List<Route>> call = apiService.getRouteFromUser(id);
        call.enqueue(new Callback<List<Route>>() {
            @Override
            public void onResponse(Call<List<Route>> call, retrofit2.Response<List<Route>> response) {
                if (response.isSuccessful()) {
                    List<Route> rutas = response.body();
                    Log.d("GetRutasFromUserSuccess", rutas.toString());
                    callback.onSuccess(rutas);
                } else {
                    Log.d("GetRutasFromUser", response.message());
                }
            }
            @Override
            public void onFailure(Call<List<Route>> call, Throwable t) {
                Log.e("GetRutasFromUserFail", "Error en la solicitud: " + t.getMessage());
            }
        });
    }

    public void getRouteFromUserAndMuseum (String id, String idMuseum, RouteCallback callback) {
        Call<List<Route>> call = apiService.getRouteFromUserAndMuseum(id,idMuseum);

        call.enqueue(new Callback<List<Route>>() {
            @Override
            public void onResponse(Call<List<Route>> call, Response<List<Route>> response) {
                if (response.isSuccessful()) {
                    List<Route> rutas = response.body();
                    Log.d("GetRutasFromUserAndMuseumSuccess", rutas.toString());
                    callback.onSuccess(rutas);
                } else {
                    Log.d("GetRutasFromUserAndMuseum", response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Route>> call, Throwable t) {
                Log.e("GetRutasFromUserFail", "Error en la solicitud: " + t.getMessage());
            }
        });
    }


}
