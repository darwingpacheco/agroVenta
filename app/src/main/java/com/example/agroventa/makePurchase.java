package com.example.agroventa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;

public class makePurchase extends AppCompatActivity {

    private TextView productName;
    private TextView priceCompra;
    private TextView countTotal;
    private TextView priceCalculate;
    private EditText edtCantdadToBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_purchase);

        productName = findViewById(R.id.productName);
        priceCompra = findViewById(R.id.priceCompra);
        countTotal = findViewById(R.id.countTotal);
        priceCalculate = findViewById(R.id.priceCalculate);
        edtCantdadToBuy = findViewById(R.id.cantdadToBuy);

        Intent intent = getIntent();
        String titleBuy = intent.getStringExtra("titleMain");
        String priceBuy = intent.getStringExtra("priceMain");
        String priceBuyParse = String.valueOf(priceBuy);
        int cantidadBuy = intent.getIntExtra("cantidadMain", -1);

        String idBuy = intent.getStringExtra("idMain");

        productName.setText(titleBuy);
        priceCompra.setText(priceBuyParse);
        countTotal.setText(String.valueOf(cantidadBuy));

        edtCantdadToBuy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cantidadIngresada = edtCantdadToBuy.getText().toString();
                if (!cantidadIngresada.isEmpty()){
                    int cantidadParse = Integer.parseInt(cantidadIngresada);
                    BigDecimal priceFinal = calculateTotal(priceBuy, cantidadParse);
                    String priceFinalParse = String.valueOf(priceFinal);

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

    public static BigDecimal calculateTotal(String price, int quantity) {
        try {
            // Eliminar separadores (si los tiene) y convertir el precio a BigDecimal
            BigDecimal priceDecimal = new BigDecimal(price.replace(".", "").replace(",", "."));

            // Multiplicar el precio por la cantidad
            BigDecimal total = priceDecimal.multiply(BigDecimal.valueOf(quantity));

            return total;

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return BigDecimal.ZERO; // Retorna 0 si hay error
        }
    }
}