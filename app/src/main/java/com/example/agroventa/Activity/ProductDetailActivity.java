package com.example.agroventa.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agroventa.R;
import com.example.agroventa.singleton.SessionManager;
import com.example.agroventa.adapters.ImageProductsAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {
    private RecyclerView productImage;
    private TextView productTitle, productDescription, productPrice, sellerName, sellerContact, productLocation, cantidadDetail, medidaDetail;
    private Button btnBuy;
    private ImageProductsAdapter imagesAdapter;
    private String title, description, price, productId;
    private int cantidadReceived;

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
        medidaDetail = findViewById(R.id.medidaDetail);
        cantidadDetail = findViewById(R.id.cantidadDetail);

        Intent intent = getIntent();
        title = intent.getStringExtra("productTitle");
        description = intent.getStringExtra("productDescription");

        ArrayList<String> imageStrings = getIntent().getStringArrayListExtra("productImages");

        List<Uri> imageUris = new ArrayList<>();
        if (imageStrings != null) {
            for (String imageString : imageStrings) {
                Uri uri = Uri.parse(imageString);
                imageUris.add(uri);
            }
        }

        String ubication = intent.getStringExtra("productUbication");
        String phoneContact = intent.getStringExtra("productContactPhone");
        String nameSeller = intent.getStringExtra("productNameSeller");
        price = intent.getStringExtra("productPrice");
        cantidadReceived = intent.getIntExtra("cantidadMenu", -1);
        String medidaReceived = intent.getStringExtra("medidaMenu");
        productId = intent.getStringExtra("idProduct");

        productImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesAdapter = new ImageProductsAdapter(getApplicationContext(), imageUris);
        productImage.setAdapter(imagesAdapter);
        productTitle.setText(title);
        productDescription.setText(description);
        productPrice.setText("$" + price);
        sellerName.setText(nameSeller);
        sellerContact.setText(phoneContact);
        productLocation.setText(ubication);
        cantidadDetail.setText(cantidadReceived == 0 ? "No hay productos en STOCK" : cantidadReceived + " ");
        medidaDetail.setText(cantidadReceived == 0 ? "" : medidaReceived);

        btnBuy.setOnClickListener(view -> {
            Intent intent2 = new Intent(ProductDetailActivity.this, MakePurchase.class);
            intent2.putExtra("titleMain", title);
            intent2.putExtra("priceMain", price);
            intent2.putExtra("cantidadMain", cantidadReceived);
            intent2.putExtra("idMain", productId);
            startActivity(intent2);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}