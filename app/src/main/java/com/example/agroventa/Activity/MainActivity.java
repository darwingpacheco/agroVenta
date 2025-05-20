package com.example.agroventa.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.agroventa.R;
import com.example.agroventa.interfaces.SessionListener;
import com.example.agroventa.singleton.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

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
    private int cantidadMove;
    private SharedPreferences preferences;
    AppCompatButton btnInit;
    private ProgressBar progressBarinit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        userEditText = findViewById(R.id.userEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        TextView txt_password = findViewById(R.id.txt_password);
        btnInit = findViewById(R.id.btnInit);
        progressBarinit = findViewById(R.id.progressBarInit);

        preferences = getSharedPreferences("SessionPrefs", MODE_PRIVATE);
        remainingTime = preferences.getLong("remainingTime", SESSION_DURATION);

        Intent intent = getIntent();
        productId = intent.getStringExtra("idProductDetail");
        titleMove = intent.getStringExtra("productTitle");
        priceMove = intent.getStringExtra("productPrice");
        cantidadMove = intent.getIntExtra("cantidadDetail", -1);

        btnInit.setOnClickListener(view -> loginUser());

        txt_password.setOnClickListener(view -> {
            Intent intentPass = new Intent(this, RecuperarContraseña.class);
            startActivity(intentPass);
            finish();
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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        btnInit.setEnabled(true);
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

                        progressBarinit.setVisibility(View.GONE);
                        Toast.makeText(this, "Iniciaste sesión", Toast.LENGTH_SHORT).show();
                        SessionManager.getInstance().setClickNoLogin(false);
                        Intent intent1 = new Intent(MainActivity.this, Menu.class);
                        startActivity(intent1);
                        finish();
                        SessionManager.getInstance().setUserSave(email);

                    } else {
                        btnInit.setEnabled(true);
                        SessionManager.getInstance().setLogin(false);
                        Toast.makeText(this, "Revisa tus credenciales.", Toast.LENGTH_SHORT).show();
                        progressBarinit.setVisibility(View.GONE);
                    }
                });
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}