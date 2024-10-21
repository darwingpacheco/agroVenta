package com.example.agroventa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        recyclerView = findViewById(R.id.recyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        productList = new ArrayList<>();
        productList.add(new Product("Cebolla Roja", "Se venden 10 cargas de cebolla roja de muy buena calidad", R.drawable.account_manager, "Abrego", 200.000, "3224291874", "Darwin Gómez"));
        productList.add(new Product("Cebolla Roja", "Se venden 32 cargas de cebolla roja ", R.drawable.account_manager, "Ocaña", 210.000, "3158746328", "Juan Martinez"));
        productList.add(new Product("Cebolla Roja", "Se venden 12 cargas de cebolla roja puesta en la Plata de Belen", R.drawable.account_manager, "La Playa", 190.000, "3048751462", "Santiago"));

        productAdapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(productAdapter);

        productAdapter.setOnClickListener((view, obj, position) -> {
            Intent intent = new Intent(Menu.this, ProductDetailActivity.class);
            intent.putExtra("productTitle", obj.getTitle());
            intent.putExtra("productDescription", obj.getDescription());
            intent.putExtra("productImage", obj.getImageResourceId());
            intent.putExtra("productPrice", obj.getPrice());
            intent.putExtra("productUbication", obj.getUbication());
            intent.putExtra("productNameSeller", obj.getNameSeller());
            intent.putExtra("productContactPhone", obj.getPhoneContact());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
