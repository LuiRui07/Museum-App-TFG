package com.example.museumapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.museumapp.Models.User;
import com.example.museumapp.R;
import com.example.museumapp.Service.UserService;

public class Register extends AppCompatActivity {
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        userService = new UserService(this);
    }

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


    public void registerUser (View view) {
        TextView mail = findViewById(R.id.mailRegister);
        TextView user = findViewById(R.id.userRegister);
        TextView contra = findViewById(R.id.contraRegister);
        TextView contraRepeat = findViewById(R.id.contraRegister);

        String correo = mail.getText().toString();
        String username = user.getText().toString();
        String password = contra.getText().toString();
        String repeatedPassword = contraRepeat.getText().toString();


        if (password.equals("")) {
            toast("Escriba una contraseña");
        } else if (!password.equals(repeatedPassword)) {
            toast("Las contraseñas no coinciden");
        } else if (!correo.contains("@")){
            toast("El correo no es válido");
        } else if (username == ""){
            toast("Usuario Incorrecto");
        } else {

        userService.registerUser(username, correo, password, new UserService.UserCallback() {
            @Override
            public void onSuccess(User user) {
                toast("Usuario Creado");
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(String errorMessage) {
                toast(errorMessage);
            }
        });



    }}

}
