package com.example.museumapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.museumapp.Models.User;
import com.example.museumapp.R;
import com.example.museumapp.Service.UserService;
import com.example.museumapp.SharedData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 183107755; // Constante para el código de solicitud de inicio de sesión
    private GoogleSignInClient mGoogleSignInClient;
    private UserService usersService;
    private TextView TextViewContra;
    private TextView TextViewUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usersService = new UserService(this);
        TextViewContra = findViewById(R.id.contra);
        TextViewUser = findViewById(R.id.user);

        // Configuración de Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Configuración del botón de inicio de sesión con Google
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                updateUI(account);
            } catch (ApiException e) {
                Log.w("SignInActivity", "signInResult:failed code=" + e.getStatusCode());
                updateUI(null);
            }
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            // Usuario está logueado con Google
            usersService.loginGoogle(account.getEmail(), account.getDisplayName(),(account.getPhotoUrl() == null ? null : account.getPhotoUrl().toString()), new UserService.UserCallback() {
                @Override
                public void onSuccess(User user) {
                    goHome(user);
                }

                @Override
                public void onFailure(String errorMessage) {
                    toast(errorMessage);
                }
            });
        } else {
            toast("Error al iniciar sesión con Google");
        }
    }

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void Register(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    @SuppressLint("NotConstructor")
    public void Login(View view) {
        String contra = TextViewContra.getText().toString();
        String username = TextViewUser.getText().toString();
        usersService.loginUser(username, contra, new UserService.UserCallback() {
            @Override
            public void onSuccess(User user) {
                user.setPhoto(null);
                goHome(user);
            }

            @Override
            public void onFailure(String errorMessage) {
                toast(errorMessage);
            }
        });
    }

    public void goHome(User user) {
        SharedData sharedData = SharedData.getInstance();
        sharedData.setUser(user);
        Log.e("user", user.toString());
        Log.e("UserData", sharedData.toString());
        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);
    }
}
