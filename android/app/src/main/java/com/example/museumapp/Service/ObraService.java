package com.example.museumapp.Service;

import android.content.Context;
import android.util.Log;

import com.example.museumapp.Adapters.ObrasAdapter;
import com.example.museumapp.Api.ApiClient;
import com.example.museumapp.Api.ApiService;
import com.example.museumapp.Models.Obra;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ObraService {

    private ApiService apiService;

    public ObraService(Context context) {
        Retrofit retrofit = ApiClient.addHeader(context);
        apiService = retrofit.create(ApiService.class);
    }

    public interface ObraCallback {
        void onSuccess(List<Obra> obras, Obra obra);
    }

    public void getAllArt(ObraCallback callback){
        Call<List<Obra>> call = apiService.getAllArt();
        call.enqueue(new Callback<List<Obra>>() {
            @Override
            public void onResponse(Call<List<Obra>> call, retrofit2.Response<List<Obra>> response) {
                if (response.isSuccessful()) {
                    List<Obra> obras = response.body();
                    Log.d("GetObras", obras.toString());
                    callback.onSuccess(obras,null);
                } else {
                    Log.d("GetObras", response.message());
                }
            }
            @Override
            public void onFailure(Call<List<Obra>> call, Throwable t) {
                Log.e("GetObrasFail", "Error en la solicitud: " + t.getMessage());
            }
        });

    }

    public void getArtFromMuseum(String id, ObraCallback callback) {
        Call<List<Obra>> call = apiService.getObrasFromMuseum(id);
        call.enqueue(new Callback<List<Obra>>() {
            @Override
            public void onResponse(Call<List<Obra>> call, retrofit2.Response<List<Obra>> response) {
                if (response.isSuccessful()) {
                    List<Obra> obras = response.body();
                    Log.d("GetObrasDeMuseoA", obras.toString());
                    callback.onSuccess(obras,null);
                } else {
                    Log.d("GetObrasDeMuseoB", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<List<Obra>> call, Throwable t) {
                Log.e("GetObrasdeMuseoFail", "Error en la solicitud: " + t.getMessage());
            }
        });

    }

    public void getArtFromId(String id, ObraCallback callback){
        Call<Obra> call = apiService.getObrasFromId(id);

        call.enqueue(new Callback<Obra>() {
            @Override
            public void onResponse(Call<Obra> call, Response<Obra> response) {
                if (response.isSuccessful()){
                    Obra obra = response.body();
                    Log.d("GetObraFromId", obra.toString());
                    callback.onSuccess(null,obra);
                } else {
                    Log.d("GetObrasFromId", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Obra> call, Throwable t) {
                Log.d("GetObrasFromId", t.getMessage());
            }
        });
    }

}
