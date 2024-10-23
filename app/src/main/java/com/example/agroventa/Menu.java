package com.example.agroventa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private List<Product> filteredProductList; // Lista filtrada
    private Spinner spinner;
    private List<Product> spinnerFilteredProductList; // Productos filtrados por el Spinner


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        recyclerView = findViewById(R.id.recyclerView);
        spinner = findViewById(R.id.spinnerOptions);
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        productList = cargarProductos();
        filteredProductList = new ArrayList<>(productList);

        productAdapter = new ProductAdapter(Menu.this, filteredProductList);
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                searchView.setQuery("", false);
                searchView.clearFocus();

                switch (selectedItem) {
                    case "General":
                        spinnerFilteredProductList = new ArrayList<>(productList); // Restaurar la lista completa
                        break;
                    case "Cosechas":
                        spinnerFilteredProductList = filtrarPorTipo("Cosechas");
                        break;
                    case "Derivados":
                        spinnerFilteredProductList = filtrarPorTipo("Derivados");
                        break;
                    default:
                        spinnerFilteredProductList = new ArrayList<>();
                        Toast.makeText(Menu.this, "Opción no disponible", Toast.LENGTH_SHORT).show();
                        break;
                }

                productAdapter.updateData(spinnerFilteredProductList);
                recyclerView.setAdapter(productAdapter);

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

    private List<Product> cargarProductos() {
        List<Product> productos = new ArrayList<>();
        productos.add(new Product("Cebolla Roja", "Se venden 10 cargas de cebolla roja de muy buena calidad", R.drawable.cebolla, "Abrego", 200.000, "3224291874", "Darwin Gómez", "Cosechas"));
        productos.add(new Product("Cebolla Roja", "Se venden 32 cargas de cebolla roja", R.drawable.cebolla, "Ocaña", 210.000, "3158746328", "Juan Martinez", "Cosechas"));
        productos.add(new Product("Shampoo con cebolla", "Shampoo derivado de la cebolla especial para la reparación del cabello", R.drawable.shampo, "Ocaña", 20.990, "3048751354", "Darwin Gómez", "Derivados"));
        productos.add(new Product("Cebollas fritas", "Cebollas fritas encurtidas en envase de plástico", R.drawable.cebollas_fritas, "Abrego", 10.000, "3157894125", "Juan Martinez", "Derivados"));

        return productos;
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
}