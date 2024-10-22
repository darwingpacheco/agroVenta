package com.example.agroventa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_menu);

        recyclerView = findViewById(R.id.recyclerView);
        spinner = findViewById(R.id.spinnerOptions);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        productList = new ArrayList<>();
        productList.add(new Product("Cebolla Roja", "Se venden 10 cargas de cebolla roja de muy buena calidad", R.drawable.cebolla, "Abrego", 200.000, "3224291874", "Darwin Gómez"));
        productList.add(new Product("Cebolla Roja", "Se venden 32 cargas de cebolla roja ", R.drawable.cebolla, "Ocaña", 210.000, "3158746328", "Juan Martinez"));
        productList.add(new Product("Cebolla Roja", "Se venden 12 cargas de cebolla roja puesta en la Plata de Belen", R.drawable.cebolla, "La Playa", 190.000, "3048751462", "Santiago"));

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
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
