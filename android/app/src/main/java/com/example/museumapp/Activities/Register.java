package com.example.museumapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.museumapp.Api.ApiService;
import com.example.museumapp.R;
import com.example.museumapp.Api.Response;
import com.example.museumapp.Api.UserBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Register extends AppCompatActivity {

    String username;
    String correo;
    String pass;

    byte check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
    }

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


    public void registerUser (View view) {

        TextView mail = findViewById(R.id.mailRegister);
        TextView user = findViewById(R.id.userRegister);
        TextView contra = findViewById(R.id.contraRegister);
        TextView contraRepeat = findViewById(R.id.contraRegister);

        String password = contra.getText().toString();
        String repeatedPassword = contraRepeat.getText().toString();

        this.correo = mail.getText().toString();
        this.username = user.getText().toString();
        this.pass = password;

        if (password.equals("")) {
            toast("Escriba una contraseña");
        } else if (!password.equals(repeatedPassword)) {
            toast("Las contraseñas no coinciden");
        } else if (!correo.contains("@")){
            toast("El correo no es válido");
        } else {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.17:5001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        UserBody userBody = new UserBody(username,correo,pass);
        Call<Response> call = apiService.createUser(userBody);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Response responseb = response.body();
                    String message = responseb.getMessage();
                    Log.d("Create?", message);
                    Log.d("Correo", correo);
                    Log.d("Username",username);
                    Log.d("Password", password);
                    if (message.contains("1")) {
                        toast("Usuario Creado");
                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                    } else {
                        toast(message);
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
    }}

}
