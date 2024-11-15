package com.example.agroventa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {
    private RecyclerView productImage;
    private TextView productTitle, productDescription, productPrice, sellerName, sellerContact, productLocation, cantidadDetail, medidaDetail;
    private Button btnBuy;
    private ImageProductsAdapter imagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        designView();

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
        String title = intent.getStringExtra("productTitle");
        String description = intent.getStringExtra("productDescription");

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
        String price = intent.getStringExtra("productPrice");
        int cantidadReceived = intent.getIntExtra("cantidadMenu", -1);
        String medidaReceived = intent.getStringExtra("medidaMenu");
        String productId = intent.getStringExtra("idProduct");

        productImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesAdapter = new ImageProductsAdapter(getApplicationContext(), imageUris);
        productImage.setAdapter(imagesAdapter);
        productTitle.setText(title);
        productDescription.setText(description);
        productPrice.setText("Precio: " + price);
        sellerName.setText(nameSeller);
        sellerContact.setText(phoneContact);
        productLocation.setText(ubication);
        cantidadDetail.setText(cantidadReceived == 0 ? "No hay productos en STOCK" : cantidadReceived + " ");
        medidaDetail.setText(cantidadReceived == 0 ? "" : medidaReceived);

        btnBuy.setOnClickListener(view -> {
            Intent intent1 = new Intent(this, MainActivity.class);
            intent1.putExtra("productTitle", title);
            intent1.putExtra("productPrice", price);
            intent1.putExtra("cantidadDetail", cantidadReceived);
            intent1.putExtra("idProductDetail", productId);
            startActivity(intent1);
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