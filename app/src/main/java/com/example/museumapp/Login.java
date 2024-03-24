package com.example.museumapp;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;


import android.media.MediaTimestamp;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    String contra ;
    String user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void login() {
        TextView c = findViewById(R.id.contra);
        contra = c.getText().toString();
        TextView u = findViewById(R.id.user);
        user = u.getText().toString();
        if (user != "" && contra != "") {
            Toast.makeText(this, "Debe escribir un usuario y una contraseña", Toast.LENGTH_SHORT).show();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonBody = new JSONObject();
        try{
            jsonBody.put("username",user);
            jsonBody.put("password",contra);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, jsonBody. toString();
        String url = "http://192.168.18.24:3000/users/login";
        Request request = new Request.Builder()
                .url(url)
                .post (requestBody)
                .build();
        new AsyncTask().execute(request);
    }

    private class AsyncTask {
        protected void execute(Request req) {
            
        }
    }
}