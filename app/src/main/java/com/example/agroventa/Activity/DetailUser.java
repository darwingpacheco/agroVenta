package com.example.agroventa.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agroventa.R;
import com.example.agroventa.adapters.PurchaseAdapter;
import com.example.agroventa.data.Purchase;
import com.example.agroventa.data.UserData;
import com.example.agroventa.singleton.SessionManager;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailUser extends AppCompatActivity {

    private ImageView userImage;
    private ImageView logoutButton;
    private TextView userName;
    private TextView userEmail;
    private TextView userPhone;
    private TextView purchasesTitle;
    private FirebaseFirestore db;
    private RecyclerView recycler;
    List<Purchase> purchaseList = new ArrayList<>();
    PurchaseAdapter adapter = new PurchaseAdapter(purchaseList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        purchasesTitle = findViewById(R.id.purchasesTitle);
        recycler = findViewById(R.id.recyclerViewPurchases);
        logoutButton = findViewById(R.id.logoutButton);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        logoutButton.setOnClickListener(view -> {
            SessionManager.getInstance().setLogin(false);
            SessionManager.getInstance().setExpiredTime(true);
            Intent intent = new Intent(DetailUser.this, Menu.class);
            startActivity(intent);
            finish();
        });

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

                        List<Map<String, Object>> purchases = (List<Map<String, Object>>) document.get("purchasedProducts");
                        if (purchases != null) {
                            for (Map<String, Object> purchaseData : purchases) {
                                Purchase purchase = new Purchase(
                                        (String) purchaseData.get("productComprado"),
                                        (String) purchaseData.get("priceComprado"),
                                        String.valueOf(purchaseData.get("cantidad")),
                                        (String) purchaseData.get("fecha")
                                );
                                purchaseList.add(purchase);
                            }
                            adapter.notifyDataSetChanged();
                        }

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