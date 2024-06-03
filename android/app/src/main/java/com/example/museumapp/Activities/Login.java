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
    private String contra ;
    private String username ;
    private UserService usersService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usersService = new UserService(this);

        // Configurar Google Sign-In para solicitar el ID del usuario, dirección de correo electrónico y perfil básico.
        // ID y perfil básico están incluidos en DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Crear un GoogleSignInClient con las opciones especificadas por gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            // Sign in failed, update UI appropriately.
            Log.w("SignInActivity", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {
            // Usuario está logueado
            usersService.loginGoogle(account.getEmail(), account.getDisplayName(), account.getPhotoUrl().toString(), new UserService.UserCallback() {
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
            toast("Error al iniciar sesion con google");
        }
    }

    public void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void Register(View view){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    @SuppressLint("NotConstructor")
    public void Login(View view) {
        TextView c = findViewById(R.id.contra);
        contra = c.getText().toString();
        TextView u = findViewById(R.id.user);
        username = u.getText().toString();

        if (username.isEmpty() || contra.isEmpty()) {
            Toast.makeText(this, "Debe escribir un usuario y una contraseña", Toast.LENGTH_SHORT).show();
        }

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
        Log.e("UserData", sharedData.toString());
        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);
    }

}