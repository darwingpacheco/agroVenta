package com.example.agroventa.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agroventa.R;
import com.example.agroventa.singleton.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MakePurchase extends AppCompatActivity {

    private TextView productName;
    private TextView priceCompra;
    private TextView countTotal;
    private TextView priceCalculate;
    private EditText edtCantdadToBuy, buyerName, shippingAddress, contactPhone;
    private AppCompatButton confirmPurchase, cancelPurchase;
    private String countFinal, contactPhoneFinal, shippingAddressFinal, buyerNameFinal;
    private String idBuy;
    private int cantidadBuy, calculateCount;
    private String email;
    private String priceFinalParse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_purchase);

        productName = findViewById(R.id.productName);
        priceCompra = findViewById(R.id.priceCompra);
        countTotal = findViewById(R.id.countTotal);
        priceCalculate = findViewById(R.id.priceCalculate);
        edtCantdadToBuy = findViewById(R.id.cantdadToBuy);
        confirmPurchase = findViewById(R.id.confirmPurchase);
        cancelPurchase = findViewById(R.id.cancelPurchase);
        buyerName = findViewById(R.id.buyerName);
        shippingAddress = findViewById(R.id.shippingAddress);
        contactPhone = findViewById(R.id.contactPhone);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail();

        Intent intent = getIntent();
        String titleBuy = intent.getStringExtra("titleMain");
        String priceBuy = intent.getStringExtra("priceMain");
        String priceBuyParse = String.valueOf(priceBuy);
        cantidadBuy = intent.getIntExtra("cantidadMain", -1);
        idBuy = intent.getStringExtra("idMain");

        productName.setText(titleBuy);
        priceCompra.setText(priceBuyParse);
        countTotal.setText(String.valueOf(cantidadBuy));

        cancelPurchase.setOnClickListener(view -> {
            finish();
        });

        confirmPurchase.setOnClickListener(view -> {
            countFinal = edtCantdadToBuy.getText().toString();
            contactPhoneFinal = contactPhone.getText().toString();
            shippingAddressFinal = shippingAddress.getText().toString();
            buyerNameFinal = buyerName.getText().toString();

            if (validations(countFinal, contactPhoneFinal, shippingAddressFinal, buyerNameFinal) != 0)
                return;

            Dialog thankYouDialog = new Dialog(this);
            thankYouDialog.setContentView(R.layout.thank_you_activity);
            thankYouDialog.setCancelable(false);

            thankYouDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            thankYouDialog.show();

            int newCount = cantidadBuy - calculateCount;

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            // Obtener la fecha actual como String con el formato deseado
            String fechaFormatted = format.format(new Date());

            db.collection("products")
                    .document(idBuy)
                    .update("cantidad", newCount)
                    .addOnSuccessListener(aVoid -> {

                        db.collection("users")
                                .whereEqualTo("email", email)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {

                                        String userId = queryDocumentSnapshots.getDocuments().get(0).getId();

                                        // Crea un mapa con los datos del producto comprado
                                        Map<String, Object> purchaseData = new HashMap<>();
                                        purchaseData.put("id", idBuy);
                                        purchaseData.put("cantidad", calculateCount);
                                        purchaseData.put("productComprado", titleBuy);
                                        purchaseData.put("fecha", fechaFormatted); // Agrega una fecha opcional
                                        purchaseData.put("priceComprado", priceFinalParse);

                                        // Agrega el arreglo de compras al documento del usuario
                                        db.collection("users")
                                                .document(userId)
                                                .update("purchasedProducts", FieldValue.arrayUnion(purchaseData))
                                                .addOnSuccessListener(aVoid1 -> {
                                                    thankYouDialog.dismiss();
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    showToast("Error al agregar producto");
                                                });
                                    } else
                                        showToast("No se encontro el usuario");

                                })
                                .addOnFailureListener(e -> {
                                    // Error al agregar productos al array
                                    Dialog errorDialog = new Dialog(this);
                                    errorDialog.setContentView(R.layout.thank_you_activity);
                                    errorDialog.setCancelable(false);
                                    errorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                                    errorDialog.show();

                                    new Handler().postDelayed(() -> {
                                        errorDialog.dismiss();
                                    }, 2000);
                                });
                    })
                    .addOnFailureListener(e -> {
                        Dialog errorDialog = new Dialog(this);
                        errorDialog.setContentView(R.layout.thank_you_activity);
                        errorDialog.setCancelable(false);
                        errorDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                        errorDialog.show();

                        new Handler().postDelayed(() -> {
                            errorDialog.dismiss();
                        }, 2000);
                    });
        });


        edtCantdadToBuy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cantidadIngresada = edtCantdadToBuy.getText().toString();
                if (!cantidadIngresada.isEmpty()) {
                    BigDecimal cantidadParse = new BigDecimal(cantidadIngresada);
                    BigDecimal priceFinal = calculateTotal(priceBuy, cantidadParse);
                    priceFinalParse = convertPriceToTotal(String.valueOf(priceFinal));

                    priceCalculate.setText(priceFinalParse);
                } else {
                    priceCalculate.setText("0.00");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private int validations(String countFinal, String contactPhoneFinal, String shippingAddressFinal, String buyerNameFinal) {

        if (countFinal.isEmpty() || contactPhoneFinal.isEmpty() || shippingAddressFinal.isEmpty() || buyerNameFinal.isEmpty()) {
            showToast("Revisa todos los campos");
            return -1;
        }

        calculateCount = Integer.parseInt(countFinal);

        if (calculateCount > cantidadBuy) {
            showToast("Cantidad a comprar insuficiente");
            return -1;
        }

        if (calculateCount == 0) {
            showToast("Ingresa una cantidad mayor.");
            return -1;
        }

        return 0;

    }

    public static BigDecimal calculateTotal(String price, BigDecimal quantity) {
        try {
            BigDecimal priceDecimal = new BigDecimal(price.replace(".", "").replace(",", "."));

            BigDecimal total = priceDecimal.multiply(quantity);

            return total;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    public static String convertPriceToTotal(String price) {
        try {
            double amount = Double.parseDouble(price.replace("[,]", "").replace(".", ""));

            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            symbols.setDecimalSeparator(',');

            DecimalFormat formatter = new DecimalFormat("#,###.##", symbols);
            return formatter.format(amount);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return price;
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}