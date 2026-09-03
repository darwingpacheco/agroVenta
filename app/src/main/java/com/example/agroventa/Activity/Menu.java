package com.example.agroventa.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.example.agroventa.repository.BackendRepository;
import com.example.agroventa.singleton.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private EditText searchView;
    private List<Product> productList;
    private List<Product> filteredProductList;
    private Spinner spinner;
    private ImageView imageViewBuy, imageViewUser, btnUser2;
    private String selectedItem;
    private final Handler handler = new Handler();
    private Runnable sessionCheckRunnable;
    private ProgressBar progressBarMenu;
    private BackendRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        repository = BackendRepository.getInstance(this);

        recyclerView = findViewById(R.id.recyclerView);
        spinner = findViewById(R.id.spinnerOptions);
        searchView = findViewById(R.id.searchView);
        progressBarMenu = findViewById(R.id.progressBarMenu);
        progressBarMenu.setVisibility(View.VISIBLE);
        imageViewBuy = findViewById(R.id.imageViewBuy);
        imageViewUser = findViewById(R.id.imageViewUser);
        //btnUser2 = findViewById(R.id.btnUser2);

        spinner.setAdapter(adapter(R.array.options, R.layout.spinner));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();

        productAdapter = new ProductAdapter(Menu.this, filteredProductList);
        recyclerView.setAdapter(productAdapter);

        cargarProductosDesdeFirestore("General");

        imageViewBuy.setOnClickListener(view -> {
            if (!SessionManager.getInstance().isLogin()) {
                navigateToLogin("sell");
                return;
            }
            Intent intent = new Intent(Menu.this, SellProducto.class);
            startActivity(intent);
        });

        imageViewUser.setOnClickListener(view -> {
                if (!SessionManager.getInstance().isLogin()) {
                    navigateToLogin("profile");
                    return;
                }
                Intent intent = new Intent(Menu.this, DetailUser.class);
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

                searchView.setText("");
                searchView.clearFocus();

                if (selectedItem.equals("Tutoriales")) {
                    progressBarMenu.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(() -> {
                        recyclerView.setLayoutManager(new LinearLayoutManager(Menu.this));

                        List<Tutorial> tutorialList = new ArrayList<>();
                        tutorialList.add(new Tutorial("Cebolla deshidratada", "https://www.youtube.com/watch?v=IjPqYSliXEU"));
                        tutorialList.add(new Tutorial("Shampoo de cebolla", "https://www.youtube.com/watch?v=HncVAITs4rs"));
                        tutorialList.add(new Tutorial("Salsa de tomate", "https://www.youtube.com/watch?v=4gns1ixgZ48"));
                        tutorialList.add(new Tutorial("Aceite de cebolla", "https://www.youtube.com/watch?v=mbWAWgfGP-8"));

                        TutorialAdapter adapter = new TutorialAdapter(tutorialList, Menu.this);
                        recyclerView.setAdapter(adapter);
                        progressBarMenu.setVisibility(View.GONE);

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



        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarProductosPorNombre(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }

    private void cargarProductosDesdeFirestore(String tipoFiltro) {
        progressBarMenu.setVisibility(View.VISIBLE);
        repository.getProducts(tipoFiltro, new BackendRepository.RepositoryCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> value) {
                productList.clear();
                for (Product product : value) {
                    if (product.getCantidad() > 0) {
                        productList.add(product);
                    }
                }
                filteredProductList.clear();
                filteredProductList.addAll(productList);
                productAdapter.updateData(filteredProductList);
                progressBarMenu.setVisibility(View.GONE);
            }

            @Override
            public void onError(String message) {
                Log.e("Backend", "Error al cargar productos: " + message);
                Toast.makeText(Menu.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                progressBarMenu.setVisibility(View.GONE);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        searchView.clearFocus();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detener el Runnable para evitar fugas de memoria
        if (handler != null && sessionCheckRunnable != null) {
            handler.removeCallbacks(sessionCheckRunnable);
        }
        searchView.clearFocus();
    }

    public ArrayAdapter adapter(int lista, int layoutId){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                lista,
                layoutId
        );
        adapter.setDropDownViewResource(R.layout.textspinner);
        return adapter;
    }

    private void navigateToLogin(String target) {
        Intent intent = new Intent(Menu.this, MainActivity.class);
        intent.putExtra("redirectTarget", target);
        startActivity(intent);
    }
}