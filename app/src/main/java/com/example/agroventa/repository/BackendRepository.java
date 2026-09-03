package com.example.agroventa.repository;

import android.content.Context;

import com.example.agroventa.BuildConfig;
import com.example.agroventa.data.DispatchPlan;
import com.example.agroventa.data.Product;
import com.example.agroventa.data.Purchase;
import com.example.agroventa.data.PurchaseRequest;
import com.example.agroventa.data.SaleRecord;
import com.example.agroventa.data.UserProfile;
import com.example.agroventa.network.AgroVentaApi;
import com.example.agroventa.network.ApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackendRepository {

    public interface RepositoryCallback<T> {
        void onSuccess(T value);

        void onError(String message);
    }

    private static BackendRepository instance;
    private final AgroVentaApi api;
    private final boolean useMock;

    private final List<Product> mockProducts = new ArrayList<>();
    private final Map<String, UserProfile> mockProfiles = new HashMap<>();
    private final Map<String, Integer> cityLoads = new HashMap<>();

    private BackendRepository(Context context) {
        this.api = ApiClient.getRetrofit().create(AgroVentaApi.class);
        this.useMock = BuildConfig.USE_MOCK_BACKEND;
        seedMockData();
    }

    public static synchronized BackendRepository getInstance(Context context) {
        if (instance == null) {
            instance = new BackendRepository(context.getApplicationContext());
        }
        return instance;
    }

    public void login(String email, String password, RepositoryCallback<String> callback) {
        if (useMock) {
            if (email.isEmpty() || password.isEmpty()) {
                callback.onError("Credenciales invalidas");
                return;
            }
            if (!mockProfiles.containsKey(email)) {
                mockProfiles.put(email, new UserProfile("Comprador", email, "3000000000"));
            }
            callback.onSuccess("mock-token-" + UUID.randomUUID());
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);
        api.login(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().get("token"));
                } else {
                    callback.onError("No fue posible iniciar sesion");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void register(String name, String apellido, String phone, String email, String password,
                         RepositoryCallback<Void> callback) {
        if (useMock) {
            UserProfile profile = new UserProfile(name + " " + apellido, email, phone);
            mockProfiles.put(email, profile);
            callback.onSuccess(null);
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("lastName", apellido);
        body.put("phone", phone);
        body.put("email", email);
        body.put("password", password);

        api.register(body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("No fue posible registrar el usuario");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void requestPasswordReset(String email, RepositoryCallback<Void> callback) {
        if (useMock) {
            callback.onSuccess(null);
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        api.requestPasswordReset(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("No fue posible enviar el correo");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getProducts(String category, RepositoryCallback<List<Product>> callback) {
        if (useMock) {
            List<Product> filtered = new ArrayList<>();
            for (Product product : mockProducts) {
                if ("General".equals(category) || product.getTipo().equals(category)) {
                    filtered.add(product);
                }
            }
            callback.onSuccess(filtered);
            return;
        }

        api.getProducts(category).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("No fue posible cargar productos");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void publishProduct(String token, Product product, RepositoryCallback<Void> callback) {
        if (useMock) {
            product.setProductId("prd-" + System.currentTimeMillis());
            mockProducts.add(product);
            callback.onSuccess(null);
            return;
        }

        api.publishProduct(bearer(token), product).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("No fue posible publicar");
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createPaymentIntent(String token, String total, String method, RepositoryCallback<String> callback) {
        if (useMock) {
            callback.onSuccess("pay-intent-mock-" + total + "-" + method);
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("amount", total);
        body.put("method", method);
        api.createPaymentIntent(bearer(token), body).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().get("paymentIntentId"));
                } else {
                    callback.onError("No fue posible crear la intencion de pago");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void createPurchase(String token, String userEmail, PurchaseRequest request, RepositoryCallback<Void> callback) {
        if (useMock) {
            Product selected = null;
            for (Product product : mockProducts) {
                if (product.getProductId().equals(request.getProductId())) {
                    selected = product;
                    break;
                }
            }
            if (selected == null) {
                callback.onError("Producto no encontrado");
                return;
            }
            if (request.getQuantity() > selected.getCantidad()) {
                callback.onError("Inventario insuficiente");
                return;
            }

            selected.setCantidad(selected.getCantidad() - request.getQuantity());

            UserProfile buyerProfile = mockProfiles.get(userEmail);
            if (buyerProfile == null) {
                buyerProfile = new UserProfile(request.getBuyerName(), userEmail, request.getBuyerPhone());
                mockProfiles.put(userEmail, buyerProfile);
            }
            String now = now();
            buyerProfile.getPurchases().add(new Purchase(
                    request.getProductName(),
                    request.getTotalPrice(),
                    String.valueOf(request.getQuantity()),
                    now
            ));

            UserProfile sellerProfile = mockProfiles.get(selected.getNameSeller());
            if (sellerProfile == null) {
                sellerProfile = new UserProfile(selected.getNameSeller(), selected.getNameSeller(), selected.getPhoneContact());
                mockProfiles.put(selected.getNameSeller(), sellerProfile);
            }

            DispatchPlan plan = evaluateDispatch(request.getDispatchCity(), request.getQuantity());
            sellerProfile.getSales().add(new SaleRecord(
                    selected.getTitle(),
                    request.getDispatchCity(),
                    request.getQuantity(),
                    request.getTotalPrice(),
                    plan.getDispatchMode(),
                    now
            ));

            callback.onSuccess(null);
            return;
        }

        api.createPurchase(bearer(token), request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("No fue posible procesar compra");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getProfile(String token, String userEmail, RepositoryCallback<UserProfile> callback) {
        if (useMock) {
            UserProfile profile = mockProfiles.get(userEmail);
            if (profile == null) {
                profile = new UserProfile("Usuario", userEmail, "");
            }
            callback.onSuccess(profile);
            return;
        }

        api.getUserProfile(bearer(token)).enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("No fue posible cargar el perfil");
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public DispatchPlan evaluateDispatch(String city, int orderQuantityKg) {
        int current = cityLoads.containsKey(city) ? cityLoads.get(city) : 0;
        int updated = current + orderQuantityKg;
        cityLoads.put(city, updated);

        int minimum = 2000;
        int max = 5000;

        if (updated >= minimum && updated <= max) {
            return new DispatchPlan(city, updated, minimum, max,
                    "Consolidado", "Carga consolidada lista para despacho troncal");
        }

        if (updated > max) {
            return new DispatchPlan(city, updated, minimum, max,
                    "Consolidado", "Se requiere dividir en dos viajes para mantener rentabilidad");
        }

        return new DispatchPlan(city, updated, minimum, max,
                "Pendiente de consolidacion",
                "El pedido queda en consolidacion hasta completar carga minima. Alternativa: envio individual con tarifa dinamica.");
    }

    private void seedMockData() {
        if (!mockProducts.isEmpty()) {
            return;
        }

        List<String> img = new ArrayList<>();
        img.add("https://images.unsplash.com/photo-1464226184884-fa280b87c399?w=1200");

        mockProducts.add(new Product(
                "Papa pastusa",
                "Cosecha fresca lista para despacho",
                img,
                "Santander",
                "2500",
                "3001234567",
                "finca-san-jose@agroventa.co",
                "Cosechas",
                "Bultos",
                3000
        ).setProductId("prd-1"));

        mockProducts.add(new Product(
                "Tomate chonto",
                "Producto de invernadero",
                img,
                "Norte de Santander",
                "1800",
                "3004445566",
                "finca-las-flores@agroventa.co",
                "Cosechas",
                "Canastillas",
                1800
        ).setProductId("prd-2"));

        mockProducts.add(new Product(
                "Pulpa de mango",
                "Derivado artesanal",
                img,
                "Cesar",
                "7500",
                "3007654321",
                "agroindustria-verde@agroventa.co",
                "Derivados",
                "Cajas",
                600
        ).setProductId("prd-3"));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}

