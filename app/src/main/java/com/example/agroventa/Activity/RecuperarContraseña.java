package com.example.agroventa.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.agroventa.R;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContraseña extends AppCompatActivity {

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.view_recuperar_password);

        EditText emailEditText = findViewById(R.id.emailEditText);
        TextView txt_init = findViewById(R.id.txt_init);
        AppCompatButton btnReset = findViewById(R.id.btnReset);

        btnReset.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (!email.isEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Correo enviado", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(this, "Error al enviar correo" , Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Por favor ingresa un correo", Toast.LENGTH_SHORT).show();
            }
        });

        txt_init.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

    }

}