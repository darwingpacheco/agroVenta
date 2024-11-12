package com.example.agroventa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class MakePurchase extends AppCompatActivity {

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
                    BigDecimal cantidadParse = new BigDecimal(cantidadIngresada);
                    BigDecimal priceFinal = calculateTotal(priceBuy, cantidadParse);
                    String priceFinalParse = convertPriceToTotal(String.valueOf(priceFinal));

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

    public static BigDecimal calculateTotal(String price, BigDecimal quantity) {
        try {
            // Eliminar separadores (si los tiene) y convertir el precio a BigDecimal
            BigDecimal priceDecimal = new BigDecimal(price.replace(".", "").replace(",", "."));

            // Multiplicar el precio por la cantidad
            BigDecimal total = priceDecimal.multiply(quantity);

            return total;

        } catch (NumberFormatException e) {
            e.printStackTrace();
            return BigDecimal.ZERO; // Retorna 0 si hay error
        }
    }

    public static String convertPriceToTotal(String price) {
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
}