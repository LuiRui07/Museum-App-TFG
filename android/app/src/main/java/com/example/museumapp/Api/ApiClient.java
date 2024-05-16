package com.example.museumapp.Api;
import android.content.Context;
import android.util.Log;

import com.example.museumapp.R;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;

    private static String JWTToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NTY3ODkiLCJ1c2VybmFtZSI6ImVqZW1wbG9fdXN1YXJpbyIsInJvbGUiOiJhZG1pbiIsImlhdCI6MTcxNTg1NDk4M30.VREIXyTuhEGiP5Zs3YKwjqPBFC41vwr6gkr1D2ogWUI";
    public static Retrofit addHeader(Context context) {
        String JWTToken = context.getString(R.string.JWTToken);
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Log.d("AA",JWTToken);
                        Request request = original.newBuilder()
                                .header("Authorization", JWTToken)
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://tfg-tkck.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit;
    }
}
