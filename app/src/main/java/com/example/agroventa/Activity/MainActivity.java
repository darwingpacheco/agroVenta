package com.example.agroventa.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.agroventa.R;
import com.example.agroventa.interfaces.SessionListener;
import com.example.agroventa.singleton.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final long SESSION_DURATION = 15 * 60 * 1000; // 5 minutos en milisegundos
    private long remainingTime = SESSION_DURATION; // Tiempo restante
    private boolean isSessionActive = false;
    private CountDownTimer sessionTimer;
    private EditText userEditText;
    private EditText passwordEditText;
    private FirebaseAuth mAuth;
    private String productId;
    private String titleMove;
    private String priceMove;
    private TextView btnLogin;
    private int cantidadMove;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        userEditText = findViewById(R.id.userEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        preferences = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
        remainingTime = preferences.getLong("remainingTime", SESSION_DURATION);
        setSessionActive(preferences.getBoolean("isSessionActive", false));

        Intent intent = getIntent();
        productId = intent.getStringExtra("idProductDetail");
        titleMove = intent.getStringExtra("productTitle");
        priceMove = intent.getStringExtra("productPrice");
        cantidadMove = intent.getIntExtra("cantidadDetail", -1);

        //btnLogin = findViewById(R.id.btnLogin);

        if (SessionManager.getInstance().isLogin() && !SessionManager.getInstance().isClickNoLogin())
            intentToDetail();

        //btnLogin.setOnClickListener(view -> loginUser());

        //findViewById(R.id.btnRegister).setOnClickListener(view -> {
            //Intent intent2 = new Intent(MainActivity.this, RegisterActivity.class);
            //startActivity(intent2);
        //});
    }

    private void loginUser() {
        String email = userEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese correo electrónico y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        btnLogin.setEnabled(true);
                        SessionManager.getInstance().setLogin(true);
                        SessionManager.getInstance().setSessionActive(true);
                        SessionManager.getInstance().setExpiredTime(false);
                        SessionManager.getInstance().setRemainingTime(SESSION_DURATION);


                        SessionManager.getInstance().startSession(new SessionListener() {
                            @Override
                            public void onSessionTick(long remainingTime) {

                            }

                            @Override
                            public void onSessionExpired() {
                                SessionManager.getInstance().setExpiredTime(false);
                            }
                        });

                        if (SessionManager.getInstance().isClickNoLogin()) {
                            Toast.makeText(this, "Iniciaste sesión", Toast.LENGTH_SHORT).show();
                            SessionManager.getInstance().setClickNoLogin(false);
                            Intent intent1 = new Intent(MainActivity.this, Menu.class);
                            startActivity(intent1);
                            finish();
                        } else {
                            Toast.makeText(this, "Acceso correcto", Toast.LENGTH_SHORT).show();
                            intentToDetail();
                        }

                        SessionManager.getInstance().setUserSave(email);

                    } else {
                        btnLogin.setEnabled(true);
                        SessionManager.getInstance().setLogin(false);
                        Toast.makeText(this, "Revisa tus credenciales.", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void startSessionTimer() {
        if (sessionTimer != null) {
            sessionTimer.cancel();
        }

        sessionTimer = new CountDownTimer(remainingTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                setSessionActive(false);
                remainingTime = SESSION_DURATION;
                saveSessionState();
                Toast.makeText(MainActivity.this, "La sesión ha expirado. Por favor, vuelve a iniciar sesión.", Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        sessionTimer.start();
    }

    private void saveSessionState() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong("remainingTime", remainingTime);
        editor.putBoolean("isSessionActive", isSessionActive());
        editor.apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSessionState();
        if (sessionTimer != null) {
            sessionTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isSessionActive) {
            startSessionTimer();
        }
    }


    public void intentToDetail() {
        Intent intent = new Intent(MainActivity.this, MakePurchase.class);
        intent.putExtra("titleMain", titleMove);
        intent.putExtra("priceMain", priceMove);
        intent.putExtra("cantidadMain", cantidadMove);
        intent.putExtra("idMain", productId);
        startActivity(intent);
        finish();
    }

    public boolean isSessionActive() {
        return isSessionActive;
    }

    public void setSessionActive(boolean sessionActive) {
        isSessionActive = sessionActive;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isSessionActive", sessionActive);
        editor.apply();
    }
}