package com.example.agroventa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText userEditText;
    private TextInputEditText passwordEditText;
    private FirebaseAuth mAuth;
    private String productId;
    private String titleMove;
    private String priceMove;
    private int cantidadMove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        designView();

        mAuth = FirebaseAuth.getInstance();
        userEditText = findViewById(R.id.userEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        Intent intent = getIntent();
        productId = intent.getStringExtra("idProductDetail");
        titleMove = intent.getStringExtra("productTitle");
        priceMove = intent.getStringExtra("productPrice");
        cantidadMove = intent.getIntExtra("cantidadDetail",-1);

        findViewById(R.id.btnLogin).setOnClickListener(view -> loginUser());

        findViewById(R.id.btnRegister).setOnClickListener(view -> {
            Intent intent2 = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent2);
        });
    }

    private void loginUser() {
        String email = userEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese correo electrónico y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Acceso correcto", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, makePurchase.class);
                        intent.putExtra("titleMain", titleMove);
                        intent.putExtra("priceMain", priceMove);
                        intent.putExtra("cantidadMain", cantidadMove);
                        intent.putExtra("idMain", productId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Revisa tus credenciales.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        designView();
    }

    public void designView() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}