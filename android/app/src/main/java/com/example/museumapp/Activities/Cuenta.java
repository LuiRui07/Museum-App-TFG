package com.example.museumapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.museumapp.R;
import com.example.museumapp.SharedData;
import com.squareup.picasso.Picasso;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class Cuenta extends AppCompatActivity {

    Button leave;

    SharedData data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuenta);

        data = SharedData.getInstance();

        TextView userName = findViewById(R.id.account_name);

        TextView mail = findViewById(R.id.account_email);

        ImageView photo = findViewById(R.id.user_photo);

        leave = findViewById(R.id.logOut);


        mail.setText(data.user.getMail());

        userName.setText(data.user.getUser());


        if (data.user.getPhoto() != null){
            Picasso.get()
                    .load(data.user.getPhoto())
                    .transform(new CropCircleTransformation())
                    .into(photo);
        }
    }

    public void LogOut(View view){
        data.destroy();

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}
