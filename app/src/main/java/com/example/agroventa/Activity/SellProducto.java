package com.example.agroventa.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agroventa.data.Product;
import com.example.agroventa.R;
import com.example.agroventa.adapters.ImageProductsAdapter;
import com.example.agroventa.repository.BackendRepository;
import com.example.agroventa.singleton.SessionManager;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import android.widget.ArrayAdapter;

public class SellProducto extends AppCompatActivity {
    private EditText edtNameSeller, edtTitle, edtDescription, edtPrice, edtPhoneSeller, edtUbicationProduct, edtQuantity;
    private Button btnPublicar, btnAddImage;
    private Spinner spinnerOptions, spinnerMeasureType;
    private String selectedItem;
    private String selectedMedida;
    private RecyclerView recyclerViewImages;
    private List<Uri> selectedImagesList = new ArrayList<>();
    private ImageProductsAdapter imagesAdapter;
    private LinearLayout lyProgress;
    private BackendRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_producto);
        repository = BackendRepository.getInstance(this);

        if (!SessionManager.getInstance().isLogin()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("redirectTarget", "sell");
            startActivity(intent);
            finish();
            return;
        }


        edtNameSeller = findViewById(R.id.edtNameSeller);
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);
        edtPhoneSeller = findViewById(R.id.edtPhoneSeller);
        edtUbicationProduct = findViewById(R.id.edtUbicationProduct);
        btnPublicar = findViewById(R.id.btnPublicar);
        spinnerOptions = findViewById(R.id.spinnerOptionsSeller);
        spinnerMeasureType = findViewById(R.id.spinnerMeasureType);
        btnAddImage = findViewById(R.id.btnAddImage);
        recyclerViewImages = findViewById(R.id.imageViewProduct);
        edtQuantity = findViewById(R.id.edtQuantity);
        lyProgress = findViewById(R.id.lyProgress);

        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesAdapter = new ImageProductsAdapter(getApplicationContext(), selectedImagesList);
        recyclerViewImages.setAdapter(imagesAdapter);

        spinnerMeasureType.setAdapter(adapter(R.array.measureTypes, R.layout.spinner2));
        spinnerOptions.setAdapter(adapter(R.array.optionsSell, R.layout.spinner2));

        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {/**None*/}
        });

        spinnerMeasureType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMedida = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {/**None*/}
        });

        btnAddImage.setOnClickListener(view -> openGalleryForImages());
        btnPublicar.setOnClickListener(view -> uploadProduct());
    }

    private void openGalleryForImages() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            if (result.getData().getClipData() != null) {
                int count = result.getData().getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                    selectedImagesList.add(imageUri);
                }
                recyclerViewImages.setVisibility(View.VISIBLE);
            } else if (result.getData().getData() != null) {
                Uri imageUri = result.getData().getData();
                selectedImagesList.add(imageUri);
            }
            imagesAdapter.notifyDataSetChanged();
        }
    });

    private void uploadProduct() {
        btnPublicar.setEnabled(false);
        if (selectedImagesList.isEmpty()) {
            Toast.makeText(this, "Por favor seleccione al menos una imagen", Toast.LENGTH_SHORT).show();
            btnPublicar.setEnabled(true);
            return;
        }

        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        String ubication = edtUbicationProduct.getText().toString().trim();
        String priceText = convertPrice(edtPrice.getText().toString().trim());
        String phone = edtPhoneSeller.getText().toString().trim();
        String name = edtNameSeller.getText().toString().trim();
        String cantidad = edtQuantity.getText().toString().trim();

        if (!validateInputs(title, description, ubication, priceText, phone, name, cantidad)) {
            return;
        }

        List<String> imageUrlStrings = new ArrayList<>();
        for (Uri image : selectedImagesList) {
            imageUrlStrings.add(image.toString());
        }

        Product product = new Product(
                title,
                description,
                imageUrlStrings,
                ubication,
                priceText,
                phone,
                name,
                selectedItem,
                selectedMedida,
                Integer.parseInt(cantidad)
        );

        lyProgress.setVisibility(View.VISIBLE);
        repository.publishProduct(SessionManager.getInstance().getAuthToken(), product, new BackendRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void value) {
                lyProgress.setVisibility(View.GONE);
                showToast("Producto publicado exitosamente");
                btnPublicar.setEnabled(true);
                finish();
            }

            @Override
            public void onError(String message) {
                lyProgress.setVisibility(View.GONE);
                showToast("Error al publicar: " + message);
                btnPublicar.setEnabled(true);
            }
        });
    }

    private boolean validateInputs(String title, String description, String ubication, String priceText, String phone, String name, String cantidad) {
        lyProgress.setVisibility(View.GONE);
        btnPublicar.setEnabled(true);
        if (title.isEmpty()) {
            showToast("Ingresa un título.");
            return false;
        }
        if (description.isEmpty()) {
            showToast("Ingresa una descripción.");
            return false;
        }
        if (ubication.isEmpty()) {
            showToast("Ingresa la ubicación del producto.");
            return false;
        }
        if (priceText.isEmpty()) {
            showToast("Ingresa el precio del producto.");
            return false;
        }
        if (phone.isEmpty()) {
            showToast("Ingresa un número de teléfono.");
            return false;
        }
        if (name.isEmpty()) {
            showToast("Ingresa el nombre del vendedor.");
            return false;
        }

        if (cantidad.isEmpty()) {
            showToast("Ingrese la cantidad a vender.");
            return false;
        }

        if (selectedMedida == null || selectedMedida.isEmpty() || "Seleccione...".equals(selectedMedida)) {
            showToast("Seleccione el tipo de medida.");
            return false;
        }

        if (selectedItem == null || selectedItem.isEmpty() || "Seleccione...".equals(selectedItem)) {
            showToast("Seleccione el tipo de producto.");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static String convertPrice(String price) {
        try {
            double amount = Double.parseDouble(price.replace("[,]", "").replace(".", ""));

            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            symbols.setDecimalSeparator(',');

            // Creamos el formato con el símbolo configurado
            DecimalFormat formatter = new DecimalFormat("#,###.##", symbols);
            return formatter.format(amount);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return price;
        }
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
}