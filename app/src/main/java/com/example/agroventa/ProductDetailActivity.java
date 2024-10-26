package com.example.agroventa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {

    private RecyclerView productImage;
    private TextView productTitle, productDescription, productPrice, sellerName, sellerContact, productLocation;
    private Button btnBuy;
    private ArrayList<Uri> imageResourceId;
    private ImageProductsAdapter imagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productImage = findViewById(R.id.productImage);
        productTitle = findViewById(R.id.productTitle);
        productDescription = findViewById(R.id.productDescription);
        productPrice = findViewById(R.id.productPrice);
        sellerName = findViewById(R.id.sellerName);
        sellerContact = findViewById(R.id.sellerContact);
        productLocation = findViewById(R.id.ciudad);
        btnBuy = findViewById(R.id.btnBuy);

        Intent intent = getIntent();
        String title = intent.getStringExtra("productTitle");
        String description = intent.getStringExtra("productDescription");
        imageResourceId = getIntent().getParcelableArrayListExtra("productImage");
        String ubication = intent.getStringExtra("productUbication");
        String phoneContact = intent.getStringExtra("productContactPhone");
        String nameSeller = intent.getStringExtra("productNameSeller");
        double price = intent.getDoubleExtra("productPrice", -1);

        productImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesAdapter = new ImageProductsAdapter(imageResourceId);
        productImage.setAdapter(imagesAdapter);
        productTitle.setText(title);
        productDescription.setText(description);
        productPrice.setText("Precio: " + String.valueOf(price));
        sellerName.setText(nameSeller);
        sellerContact.setText(phoneContact);
        productLocation.setText(ubication);

        btnBuy.setOnClickListener(view -> {
            Intent intent1 = new Intent(this, MainActivity.class);
            startActivity(intent1);
        });

    }
}