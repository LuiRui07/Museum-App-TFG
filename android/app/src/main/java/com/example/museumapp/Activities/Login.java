package com.example.museumapp.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.museumapp.Api.ApiClient;
import com.example.museumapp.Api.ApiService;
import com.example.museumapp.Api.LoginRequest;
import com.example.museumapp.R;
import com.example.museumapp.Api.Response;
import com.example.museumapp.SharedData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    private static final int RC_SIGN_IN = 183107755; // Constante para el código de solicitud de inicio de sesión

    private GoogleSignInClient mGoogleSignInClient;

    private ActivityResultLauncher<Intent> signInLauncher;


    private boolean signedGoogle;
    private String contra ;
    private String user ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
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
            goHome(account.getDisplayName(),account.getEmail(),account.getPhotoUrl());
        } else {
            // Usuario no está logueado
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
        user = u.getText().toString();

        if (user.isEmpty() || contra.isEmpty()) {
            Toast.makeText(this, "Debe escribir un usuario y una contraseña", Toast.LENGTH_SHORT).show();
        }
        Retrofit retrofit = ApiClient.addHeader(this);
        ApiService apiService = retrofit.create(ApiService.class);

        LoginRequest loginRequest = new LoginRequest(user, contra);
        Call<Response> call = apiService.loginUser(loginRequest);
        Log.d(call.toString(), "");
        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                Log.e("LoginResponse", response.headers().toString());
                if (response.isSuccessful()) {
                    Response loginResponse = response.body();
                    String message = loginResponse.getMessage();
                    if (message.contains("1")) {
                        goHome(loginResponse.getUser(),loginResponse.getMail(),null);  // Meter datos
                    } else {
                        toast("Correo o Contraseña Incorrectos");
                    }
                } else {
                    Log.d("LoginResponse", response.message());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Log.e("LoginResponse", "Error en la solicitud: " + t.toString());
            }
        });
    }

    public void goHome(String user, String mail, Uri photo) {
        SharedData sharedData = SharedData.getInstance();
        sharedData.setUser(user);
        sharedData.setMail(mail);
        sharedData.setPhoto(photo);
        Log.e("UserData", sharedData.toString());
        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);

    }

}