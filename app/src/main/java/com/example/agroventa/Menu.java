package com.example.agroventa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private ImageView btnBuy;
    private FirebaseFirestore firestore;
    private CollectionReference productsRef;
    private String selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        FirebaseApp.initializeApp(this);

        recyclerView = findViewById(R.id.recyclerView);
        spinner = findViewById(R.id.spinnerOptions);
        searchView = findViewById(R.id.searchView);
        btnBuy = findViewById(R.id.btnBuy);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();

        productAdapter = new ProductAdapter(Menu.this, filteredProductList);
        recyclerView.setAdapter(productAdapter);

        firestore = FirebaseFirestore.getInstance();
        productsRef = firestore.collection("products");

        cargarProductosDesdeFirestore("General");

        btnBuy.setOnClickListener(view -> {
            Intent intent = new Intent(Menu.this, sellProducto.class);
            startActivity(intent);
        });

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

                cargarProductosDesdeFirestore(selectedItem);
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
        return String.format("{\"title\": \"%s\", \"description\": \"%s\", \"price\": %.2f, \"tipo\": \"%s\", \"ubication\": \"%s\", \"nameSeller\": \"%s\", \"phoneContact\": \"%s\"}",
                product.getTitle(), product.getDescription(), product.getPrice(), product.getTipo(), product.getUbication(), product.getNameSeller(), product.getPhoneContact());
    }
}