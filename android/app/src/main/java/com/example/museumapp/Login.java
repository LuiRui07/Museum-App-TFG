package com.example.museumapp;


import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    String contra ;
    String user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NotConstructor")
    public void Login(View view) {
        TextView c = findViewById(R.id.contra);
        contra = c.getText().toString();
        TextView u = findViewById(R.id.user);
        user = u.getText().toString();

        if (user.isEmpty() || contra.isEmpty()) {
            Toast.makeText(this, "Debe escribir un usuario y una contraseña", Toast.LENGTH_SHORT).show();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.17:5001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        LoginRequest loginRequest = new LoginRequest(user, contra);
        Call<Response> call = apiService.loginUser(loginRequest);


        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Response loginResponse = response.body();
                    String message = loginResponse.getMessage();
                    if (message.contains("1")) {
                        Intent intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                    } else {
                        toast("Correo o Contraseña Incorrectos");
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