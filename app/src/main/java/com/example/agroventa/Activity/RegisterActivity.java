package com.example.agroventa.Activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.agroventa.R;
import com.example.agroventa.data.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText, confirmPasswordEditText, lastNameEditText, nameEditText, telefonoEditText;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private ScrollView scrollView;
    private ConstraintLayout constraintId;
    ViewGroup.MarginLayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        designView();

        auth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditTextRegister);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
        nameEditText = findViewById(R.id.nameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        telefonoEditText = findViewById(R.id.telefonoEditText);
        scrollView = findViewById(R.id.scroll);
        constraintId = findViewById(R.id.constraintId);

        registerButton.setOnClickListener(view -> registerUser());

        constraintId.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            constraintId.getWindowVisibleDisplayFrame(r);

            int screenHeight = constraintId.getRootView().getHeight();
            int heightDifference = screenHeight - r.bottom;

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) scrollView.getLayoutParams();

            if (heightDifference > 200)
                params.setMargins(0, 0, 0, 340);
            else
                params.setMargins(0, 0, 0, 0);

            scrollView.setLayoutParams(params);
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String apellido = lastNameEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String phone = telefonoEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            saveUserToFirestore(user.getUid(), name, apellido, email, phone);
                        }
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            passwordEditText.setError("Contraseña débil");
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            emailEditText.setError("Correo no válido");
                        } catch (FirebaseAuthUserCollisionException e) {
                            emailEditText.setError("El usuario ya está registrado");
                        } catch (Exception e) {
                            Toast.makeText(RegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserToFirestore(String uid, String name, String apellido, String email, String phone) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserData user = new UserData(name, apellido, email, phone);

        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Finaliza la actividad
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
