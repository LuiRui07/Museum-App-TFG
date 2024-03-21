package com.example.museumapp;

import androidx.appcompat.app.AppCompatActivity;

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

        MediaType JSON = MediaType.parse();

        JSONObject jsonbody = new JSONObject();
        try{
            jsonbody.put("username",user);
            jsonbody.put("password",contra);
        } catch (JSONException e) {
            e.printStackTrace();
        }






    }
}