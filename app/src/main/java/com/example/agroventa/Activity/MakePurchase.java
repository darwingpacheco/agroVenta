package com.example.agroventa.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agroventa.data.DispatchPlan;
import com.example.agroventa.data.PurchaseRequest;
import com.example.agroventa.R;
import com.example.agroventa.repository.BackendRepository;
import com.example.agroventa.singleton.SessionManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MakePurchase extends AppCompatActivity {

    private TextView productName;
    private TextView priceCompra;
    private TextView countTotal;
    private TextView priceCalculate;
    private EditText edtCantdadToBuy, buyerName, contactPhone;
    private Spinner citySpinner, paymentSpinner;
    private AppCompatButton confirmPurchase, cancelPurchase;
    private String countFinal, contactPhoneFinal, buyerNameFinal;
    private String idBuy;
    private String titleBuy;
    private String priceBuy;
    private int cantidadBuy, calculateCount;
    private String priceFinalParse;
    private BackendRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_purchase);
        repository = BackendRepository.getInstance(this);

        productName = findViewById(R.id.productName);
        priceCompra = findViewById(R.id.priceCompra);
        countTotal = findViewById(R.id.countTotal);
        priceCalculate = findViewById(R.id.priceCalculate);
        edtCantdadToBuy = findViewById(R.id.cantdadToBuy);
        confirmPurchase = findViewById(R.id.confirmPurchase);
        cancelPurchase = findViewById(R.id.cancelPurchase);
        buyerName = findViewById(R.id.buyerName);
        contactPhone = findViewById(R.id.contactPhone);
        citySpinner = findViewById(R.id.dispatchCitySpinner);
        paymentSpinner = findViewById(R.id.paymentMethodSpinner);

        citySpinner.setAdapter(adapter(R.array.dispatchCities, R.layout.spinner2));
        paymentSpinner.setAdapter(adapter(R.array.paymentMethods, R.layout.spinner2));

        Intent intent = getIntent();
        titleBuy = intent.getStringExtra("titleMain");
        priceBuy = intent.getStringExtra("priceMain");
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
            buyerNameFinal = buyerName.getText().toString();
            String dispatchCity = citySpinner.getSelectedItem().toString();
            String paymentMethod = paymentSpinner.getSelectedItem().toString();

            if (validations(countFinal, contactPhoneFinal, buyerNameFinal, dispatchCity, paymentMethod) != 0) {
                return;
            }

            if (!SessionManager.getInstance().isLogin()) {
                Intent loginIntent = new Intent(MakePurchase.this, MainActivity.class);
                loginIntent.putExtra("redirectTarget", "buy");
                loginIntent.putExtra("titleMain", titleBuy);
                loginIntent.putExtra("priceMain", priceBuy);
                loginIntent.putExtra("cantidadMain", cantidadBuy);
                loginIntent.putExtra("idMain", idBuy);
                startActivity(loginIntent);
                return;
            }

            PurchaseRequest request = new PurchaseRequest(
                    idBuy,
                    titleBuy,
                    calculateCount,
                    priceBuy,
                    priceFinalParse,
                    buyerNameFinal,
                    contactPhoneFinal,
                    dispatchCity,
                    paymentMethod
            );

            DispatchPlan plan = repository.evaluateDispatch(dispatchCity, calculateCount);
            processPaymentAndPurchase(request, plan);
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

    private void processPaymentAndPurchase(PurchaseRequest request, DispatchPlan plan) {
        repository.createPaymentIntent(
                SessionManager.getInstance().getAuthToken(),
                request.getTotalPrice(),
                request.getPaymentMethod(),
                new BackendRepository.RepositoryCallback<String>() {
                    @Override
                    public void onSuccess(String paymentIntentId) {
                        repository.createPurchase(
                                SessionManager.getInstance().getAuthToken(),
                                SessionManager.getInstance().getUserSave(),
                                request,
                                new BackendRepository.RepositoryCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void value) {
                                        showSuccessDialog(plan, paymentIntentId);
                                    }

                                    @Override
                                    public void onError(String message) {
                                        showToast("No se pudo completar la compra: " + message);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError(String message) {
                        showToast("No se pudo iniciar el pago: " + message);
                    }
                }
        );
    }

    private void showSuccessDialog(DispatchPlan plan, String paymentIntentId) {
        Dialog thankYouDialog = new Dialog(this);
        thankYouDialog.setContentView(R.layout.thank_you_activity);
        thankYouDialog.setCancelable(true);
        thankYouDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        thankYouDialog.show();

        String message = "Pago confirmado: " + paymentIntentId + "\n" +
                "Ciudad: " + plan.getCity() + "\n" +
                "Modo despacho: " + plan.getDispatchMode() + "\n" +
                plan.getStatusMessage();
        showToast(message);
        finish();
    }
    private int validations(String countFinal, String contactPhoneFinal, String buyerNameFinal,
                            String dispatchCity, String paymentMethod) {

        if (countFinal.isEmpty() || contactPhoneFinal.isEmpty() || buyerNameFinal.isEmpty()) {
            showToast("Revisa todos los campos");
            return -1;
        }

        if ("Seleccione ciudad...".equals(dispatchCity)) {
            showToast("Selecciona una ciudad de despacho");
            return -1;
        }

        if ("Seleccione metodo...".equals(paymentMethod)) {
            showToast("Selecciona un metodo de pago");
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

    public ArrayAdapter adapter(int lista, int layoutId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, lista, layoutId);
        adapter.setDropDownViewResource(R.layout.textspinner);
        return adapter;
    }
}