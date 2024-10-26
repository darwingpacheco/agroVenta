package com.example.agroventa;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class sellProducto extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference productsRef;
    private StorageReference storageRef;
    private EditText edtNameSeller, edtTitle, edtDescription, edtPrice, edtPhoneSeller, edtUbicationProduct;
    private Button btnPublicar, btnAddImage;
    private Spinner spinnerOptions;
    private String selectedItem;
    private RecyclerView recyclerViewImages;
    private List<Uri> selectedImagesList = new ArrayList<>();
    private ImageProductsAdapter imagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_producto);

        db = FirebaseFirestore.getInstance();
        productsRef = db.collection("products");
        storageRef = FirebaseStorage.getInstance().getReference().child("product_images");

        edtNameSeller = findViewById(R.id.edtNameSeller);
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtPrice = findViewById(R.id.edtPrice);
        edtPhoneSeller = findViewById(R.id.edtPhoneSeller);
        edtUbicationProduct = findViewById(R.id.edtUbicationProduct);
        btnPublicar = findViewById(R.id.btnPublicar);
        spinnerOptions = findViewById(R.id.spinnerOptionsSeller);
        btnAddImage = findViewById(R.id.btnAddImage);
        recyclerViewImages = findViewById(R.id.imageViewProduct);

        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesAdapter = new ImageProductsAdapter(selectedImagesList);
        recyclerViewImages.setAdapter(imagesAdapter);

        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
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
            } else if (result.getData().getData() != null) {
                Uri imageUri = result.getData().getData();
                selectedImagesList.add(imageUri);
            }
            imagesAdapter.notifyDataSetChanged();
        }
    });

    private void uploadProduct() {
        if (selectedImagesList.isEmpty()) {
            Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Uri> imageUrls = new ArrayList<>();
        for (Uri imageUri : selectedImagesList) {
            String fileName = "image_" + System.currentTimeMillis();
            StorageReference imageRef = storageRef.child(fileName);

            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                imageUrls.add(Uri.parse(uri.toString()));
                if (imageUrls.size() == selectedImagesList.size()) {
                    saveProductToFirestore(imageUrls);
                }
            })).addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveProductToFirestore(List<Uri> imageUrls) {
        List<String> imageUrlStrings = new ArrayList<>();
        for (Uri uri : imageUrls) {
            imageUrlStrings.add(uri.toString());
        }

        Product product = new Product(
                edtTitle.getText().toString(),
                edtDescription.getText().toString(),
                imageUrlStrings, // Usar la lista de Strings
                edtUbicationProduct.getText().toString(),
                Double.valueOf(edtPrice.getText().toString()),
                edtPhoneSeller.getText().toString(),
                edtNameSeller.getText().toString(),
                selectedItem
        );

        productsRef.add(product)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Producto publicado exitosamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al publicar el producto", Toast.LENGTH_SHORT).show();
                });
    }

}