package com.example.agroventa.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agroventa.R;
import com.example.agroventa.adapters.ProductAdapter;
import com.example.agroventa.adapters.TutorialAdapter;
import com.example.agroventa.data.Product;
import com.example.agroventa.data.Tutorial;
import com.example.agroventa.interfaces.SessionListener;
import com.example.agroventa.singleton.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private androidx.appcompat.widget.SearchView searchView;
    private List<Product> productList;
    private List<Product> filteredProductList;
    private Spinner spinner;
    private ImageView btnBuy, btnUser, btnUser2;
    private FirebaseFirestore firestore;
    private CollectionReference productsRef;
    private String selectedItem;
    private Handler handler = new Handler();
    private Runnable sessionCheckRunnable;
    private boolean isSessionExpired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        FirebaseApp.initializeApp(this);

        recyclerView = findViewById(R.id.recyclerView);
        spinner = findViewById(R.id.spinnerOptions);
        searchView = findViewById(R.id.searchView);
        btnBuy = findViewById(R.id.btnBuy);
        btnUser = findViewById(R.id.btnUser);
        btnUser2 = findViewById(R.id.btnUser2);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();

        productAdapter = new ProductAdapter(Menu.this, filteredProductList);
        recyclerView.setAdapter(productAdapter);

        firestore = FirebaseFirestore.getInstance();
        productsRef = firestore.collection("products");

        cargarProductosDesdeFirestore("General");
        startSessionCheck();

        btnBuy.setOnClickListener(view -> {
            Intent intent = new Intent(Menu.this, SellProducto.class);
            startActivity(intent);
        });

        btnUser2.setOnClickListener(view -> {
            SessionManager.getInstance().setClickNoLogin(true);
            Intent intent = new Intent(Menu.this, MainActivity.class);
            startActivity(intent);
        });

        if (SessionManager.getInstance().isLogin() && !SessionManager.getInstance().isExpiredTime()) {
            btnUser.setVisibility(View.VISIBLE);
            btnUser2.setVisibility(View.GONE);
        } else {
            btnUser.setVisibility(View.GONE);
            btnUser2.setVisibility(View.VISIBLE);
        }

        productAdapter.setOnClickListener((view, obj, position) -> {
            Intent intent = new Intent(Menu.this, ProductDetailActivity.class);
            intent.putExtra("productTitle", obj.getTitle());
            intent.putExtra("productDescription", obj.getDescription());
            ArrayList<String> imageStrings = new ArrayList<>(obj.getImageResourceId());
            intent.putStringArrayListExtra("productImages", imageStrings);
            intent.putExtra("productPrice", obj.getPrice());
            intent.putExtra("productUbication", obj.getUbication());
            intent.putExtra("productNameSeller", obj.getNameSeller());
            intent.putExtra("productContactPhone", obj.getPhoneContact());
            intent.putExtra("cantidadMenu", obj.getCantidad());
            intent.putExtra("medidaMenu", obj.getMedida());
            intent.putExtra("idProduct", obj.getProductId());
            startActivity(intent);
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();

                searchView.setQuery("", false);
                searchView.clearFocus();

                if (selectedItem.equals("Tutoriales")) {

                    new Handler().postDelayed(() -> {
                        recyclerView.setLayoutManager(new LinearLayoutManager(Menu.this));

                        List<Tutorial> tutorialList = new ArrayList<>();
                        tutorialList.add(new Tutorial("Cebolla deshidratada", "https://www.youtube.com/watch?v=IjPqYSliXEU"));
                        tutorialList.add(new Tutorial("Shampoo de cebolla", "https://www.youtube.com/watch?v=HncVAITs4rs"));
                        tutorialList.add(new Tutorial("Salsa de tomate", "https://www.youtube.com/watch?v=4gns1ixgZ48"));
                        tutorialList.add(new Tutorial("Aceite de cebolla", "https://www.youtube.com/watch?v=mbWAWgfGP-8"));

                        TutorialAdapter adapter = new TutorialAdapter(tutorialList, Menu.this);
                        recyclerView.setAdapter(adapter);

                    }, 1000);
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(Menu.this, 2));
                    cargarProductosDesdeFirestore(selectedItem);
                    recyclerView.setAdapter(productAdapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarProductosPorNombre(newText);
                return true;
            }
        });
    }

    private void cargarProductosDesdeFirestore(String tipoFiltro) {
        productsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    productList.clear();
                    StringBuilder jsonBuilder = new StringBuilder();
                    jsonBuilder.append("{\n  \"products\": [\n");

                    boolean first = true;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Product product = document.toObject(Product.class);
                        if (product != null) {
                            productList.add(product.setProductId(document.getId()));

                            // Agregar al JSON
                            if (!first) {
                                jsonBuilder.append(",\n");
                            }
                            jsonBuilder.append("    ").append(productToJson(product));
                            first = false;
                        }
                    }

                    jsonBuilder.append("\n  ]\n}");
                    Log.d("FirestoreData", jsonBuilder.toString());

                    // Filtrar la lista según el tipo seleccionado
                    if (!tipoFiltro.equals("General")) {
                        filteredProductList.clear();
                        filteredProductList.addAll(filtrarPorTipo(tipoFiltro));
                    } else {
                        filteredProductList.clear();
                        filteredProductList.addAll(productList);
                    }
                    productAdapter.updateData(filteredProductList);
                } else {
                    Log.e("FirestoreError", "Error al cargar productos: " + task.getException().getMessage());
                    Toast.makeText(Menu.this, "Error al cargar productos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startSessionCheck() {
        sessionCheckRunnable = new Runnable() {
            @Override
            public void run() {

                runOnUiThread(() -> updateIconsBasedOnSession());

                handler.postDelayed(this, 1000);
            }
        };

        handler.post(sessionCheckRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startSessionCheck();

        SessionManager.getInstance().startSession(new SessionListener() {
            @Override
            public void onSessionTick(long remainingTime) {

            }

            @Override
            public void onSessionExpired() {
                SessionManager.getInstance().setExpiredTime(true);
            }
        });

        if (selectedItem != null && !selectedItem.equals("Tutoriales"))
            cargarProductosDesdeFirestore(selectedItem);
    }

    private void updateIconsBasedOnSession() {
        if (SessionManager.getInstance().isLogin() && !SessionManager.getInstance().isExpiredTime()) {
            btnUser.setVisibility(View.VISIBLE);
            btnUser2.setVisibility(View.GONE);
        } else {
            btnUser.setVisibility(View.GONE);
            btnUser2.setVisibility(View.VISIBLE);
        }
    }

    private List<Product> filtrarPorTipo(String tipo) {
        List<Product> productosFiltrados = new ArrayList<>();
        for (Product producto : productList) {
            if (producto.getTipo().equals(tipo)) {
                productosFiltrados.add(producto);
            }
        }
        return productosFiltrados;
    }

    private void filtrarProductosPorNombre(String query) {
        if (productList == null || productList.isEmpty())
            return;

        String textoFiltrado = query.toLowerCase();
        List<Product> productosFiltrados = new ArrayList<>();

        for (Product producto : productList) {
            if (producto.getTitle().toLowerCase().contains(textoFiltrado)) {
                productosFiltrados.add(producto);
            }
        }

        filteredProductList.clear();
        filteredProductList.addAll(productosFiltrados);
        productAdapter.updateData(productosFiltrados);
    }

    private String productToJson(Product product) {
        return String.format("{\"title\": \"%s\", \"description\": \"%s\", \"price\": \"%s\", \"tipo\": \"%s\", \"ubication\": \"%s\", \"nameSeller\": \"%s\", \"phoneContact\": \"%s\"}",
                product.getTitle(), product.getDescription(), product.getPrice(), product.getTipo(), product.getUbication(), product.getNameSeller(), product.getPhoneContact());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detener el Runnable para evitar fugas de memoria
        if (handler != null && sessionCheckRunnable != null) {
            handler.removeCallbacks(sessionCheckRunnable);
        }
    }
}