package com.example.museumapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
    }

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    protected void register (View view) {
        //Comprobar que el email o usuario exista

        TextView mail = findViewById(R.id.mailRegister);
        TextView user = findViewById(R.id.userRegister);
        TextView contra = findViewById(R.id.contra);
        TextView contraRepeat = findViewById(R.id.contraRegister);

        String password = contra.getText().toString();
        String repeatedPassword = contraRepeat.getText().toString();

        if (password.equals("")) {
            toast("Escriba una contraseña");
            return;
        }

        if (!password.equals(repeatedPassword)) {
            toast("Las contraseñas no coinciden");
            return;
        }


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.17:5001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        String correo = mail.getText().toString();
        String username = user.getText().toString();
        Call<Response> call = apiService.checkExists(correo,username);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful()) {
                    Response responseb = response.body();
                    String message = responseb.getMessage();
                    if (message.contains("1")) {
                        toast("Todo perfe");
                    } else {
                        toast("No");
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
