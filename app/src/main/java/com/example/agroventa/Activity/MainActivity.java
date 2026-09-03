package com.example.agroventa.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.agroventa.R;
import com.example.agroventa.interfaces.SessionListener;
import com.example.agroventa.repository.BackendRepository;
import com.example.agroventa.singleton.SessionManager;

public class MainActivity extends AppCompatActivity {
    private static final long SESSION_DURATION = 15 * 60 * 1000;
    private EditText userEditText;
    private EditText passwordEditText;
    private String redirectTarget;
    AppCompatButton btnInit;
    private ProgressBar progressBarinit;
    private BackendRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = BackendRepository.getInstance(this);
        userEditText = findViewById(R.id.userEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        TextView txt_password = findViewById(R.id.txt_password);
        btnInit = findViewById(R.id.btnInit);
        progressBarinit = findViewById(R.id.progressBarInit);

        Intent intent = getIntent();
        redirectTarget = intent.getStringExtra("redirectTarget");

        btnInit.setOnClickListener(view -> loginUser());

        txt_password.setOnClickListener(view -> {
            Intent intentPass = new Intent(this, RegisterActivity.class);
            startActivity(intentPass);
        });
    }

    private void loginUser() {
        String email = userEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese correo electrónico y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        btnInit.setEnabled(false);
        progressBarinit.setVisibility(View.VISIBLE);

        repository.login(email, password, new BackendRepository.RepositoryCallback<String>() {
                    @Override
                    public void onSuccess(String token) {
                        btnInit.setEnabled(true);
                        SessionManager.getInstance().setLogin(true);
                        SessionManager.getInstance().setAuthToken(token);
                        SessionManager.getInstance().setUserSave(email);
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

                        progressBarinit.setVisibility(View.GONE);
                        Toast.makeText(this, "Iniciaste sesión", Toast.LENGTH_SHORT).show();
                        Intent intent1 = resolveTargetIntent();
                        startActivity(intent1);
                        finish();
                    }

                    @Override
                    public void onError(String message) {
                        btnInit.setEnabled(true);
                        SessionManager.getInstance().setLogin(false);
                        Toast.makeText(MainActivity.this, "Revisa tus credenciales. " + message, Toast.LENGTH_SHORT).show();
                        progressBarinit.setVisibility(View.GONE);
                    }
                });
    }

    private Intent resolveTargetIntent() {
        if ("buy".equals(redirectTarget)) {
            return new Intent(MainActivity.this, MakePurchase.class).putExtras(getIntent());
        }
        if ("sell".equals(redirectTarget)) {
            return new Intent(MainActivity.this, SellProducto.class);
        }
        if ("profile".equals(redirectTarget)) {
            return new Intent(MainActivity.this, DetailUser.class);
        }
        return new Intent(MainActivity.this, Menu.class);
    }

}