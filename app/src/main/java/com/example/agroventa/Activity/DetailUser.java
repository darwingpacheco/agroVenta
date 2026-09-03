package com.example.agroventa.Activity;

import androidx.appcompat.app.AppCompatActivity;
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
import com.example.agroventa.data.SaleRecord;
import com.example.agroventa.data.UserProfile;
import com.example.agroventa.repository.BackendRepository;
import com.example.agroventa.singleton.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class DetailUser extends AppCompatActivity {

    private ImageView userImage;
    private ImageView logoutButton;
    private TextView userName;
    private TextView userEmail;
    private TextView userPhone;
    private TextView purchasesTitle;
    private TextView salesSummary;
    private RecyclerView recycler;
    List<Purchase> purchaseList = new ArrayList<>();
    PurchaseAdapter adapter = new PurchaseAdapter(purchaseList);
    private BackendRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);

        userImage = findViewById(R.id.userImage);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userPhone = findViewById(R.id.userPhone);
        purchasesTitle = findViewById(R.id.purchasesTitle);
        salesSummary = findViewById(R.id.salesSummary);
        recycler = findViewById(R.id.recyclerViewPurchases);
        logoutButton = findViewById(R.id.logoutButton);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        repository = BackendRepository.getInstance(this);

        String email = SessionManager.getInstance().getUserSave();
        if (SessionManager.getInstance().isLogin() && email != null) {
            fetchUserData(email);
        } else {
            Toast.makeText(this, "Inicia sesion para ver tu perfil", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DetailUser.this, MainActivity.class).putExtra("redirectTarget", "profile"));
            finish();
        }

        logoutButton.setOnClickListener(view -> {
            SessionManager.getInstance().setLogin(false);
            SessionManager.getInstance().setExpiredTime(true);
            SessionManager.getInstance().clearAuth();
            Intent intent = new Intent(DetailUser.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });


    }

    private void fetchUserData(String email) {
        repository.getProfile(
                SessionManager.getInstance().getAuthToken(),
                email,
                new BackendRepository.RepositoryCallback<UserProfile>() {
                    @Override
                    public void onSuccess(UserProfile profile) {
                        userName.setText(profile.getName());
                        userEmail.setText(profile.getEmail());
                        userPhone.setText(profile.getPhone());

                        purchaseList.clear();
                        if (profile.getPurchases() != null) {
                            purchaseList.addAll(profile.getPurchases());
                        }
                        adapter.notifyDataSetChanged();

                        salesSummary.setText(buildSalesSummary(profile.getSales()));
                    }

                    @Override
                    public void onError(String message) {
                        Log.e("Backend", "Error al cargar perfil", new RuntimeException(message));
                        Toast.makeText(DetailUser.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private String buildSalesSummary(List<SaleRecord> sales) {
        if (sales == null || sales.isEmpty()) {
            return "No tienes ventas registradas todavia.";
        }

        StringBuilder builder = new StringBuilder();
        for (SaleRecord sale : sales) {
            builder.append("- ")
                    .append(sale.getProductName())
                    .append(" | ")
                    .append(sale.getQuantity())
                    .append(" kg | ")
                    .append(sale.getCity())
                    .append(" | ")
                    .append(sale.getStatus())
                    .append(" | ")
                    .append(sale.getDate())
                    .append("\n");
        }
        return builder.toString().trim();
    }
}