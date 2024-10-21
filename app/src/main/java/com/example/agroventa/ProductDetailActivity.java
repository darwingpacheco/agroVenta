package com.example.agroventa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView productImage;
    private TextView productTitle, productDescription, productPrice, sellerName, sellerContact, productLocation;
    private Button btnBuy;

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
        productLocation = findViewById(R.id.productLocation);
        btnBuy = findViewById(R.id.btnBuy);

        Intent intent = getIntent();
        String title = intent.getStringExtra("productTitle");
        String description = intent.getStringExtra("productDescription");
        int imageResourceId = intent.getIntExtra("productImage", -1);
        String ubication = intent.getStringExtra("productUbication");
        String phoneContact = intent.getStringExtra("productContactPhone");
        String nameSeller = intent.getStringExtra("productNameSeller");
        double price = intent.getDoubleExtra("productPrice", -1);

        productImage.setImageResource(imageResourceId);
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

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}