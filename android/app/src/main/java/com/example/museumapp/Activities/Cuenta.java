package com.example.museumapp.Activities;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.museumapp.R;
import com.example.museumapp.SharedData;
import com.squareup.picasso.Picasso;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import org.w3c.dom.Text;

public class Cuenta extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cuenta);

        SharedData data = SharedData.getInstance();

        TextView userName = findViewById(R.id.account_name);

        TextView mail = findViewById(R.id.account_email);

        ImageView photo = findViewById(R.id.user_photo);

        mail.setText(data.mail);

        userName.setText(data.user);

        Picasso.get()
                .load(data.photo)
                .transform(new CropCircleTransformation())
                .into(photo);

    }
}
