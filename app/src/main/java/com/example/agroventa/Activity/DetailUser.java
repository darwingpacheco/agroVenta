package com.example.agroventa.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agroventa.R;
import com.example.agroventa.singleton.SessionManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DetailUser extends AppCompatActivity {

    private ImageView userImage;
    private TextView userName;
    private TextView userEmail;
    private TextView userPhone;
    private TextView userRole;
    private TextView purchasesTitle;
    private TextView purchaseItem1;
    private TextView purchaseDate1;
    private TextView purchaseItem2;
    private TextView purchaseDate2;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);

        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        userRole = findViewById(R.id.userRole);
        purchasesTitle = findViewById(R.id.purchasesTitle);
        purchaseItem1 = findViewById(R.id.purchaseItem1);
        purchaseDate1 = findViewById(R.id.purchaseDate1);
        purchaseItem2 = findViewById(R.id.purchaseItem2);
        purchaseDate2 = findViewById(R.id.purchaseDate2);

        db = FirebaseFirestore.getInstance();

        String email = SessionManager.getInstance().getUserSave();
        if (email != null)
            fetchUserData(email);
        else {
            Toast.makeText(this, "Correo no proporcionado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fetchUserData(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        userName.setText(document.getString("name"));
                        userEmail.setText(document.getString("email"));
                        userPhone.setText(document.getString("phone"));
                        // userRole.setText(document.getString("role"));

                    } else {
                        Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al realizar la consulta", e);
                    Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                });
    }
}