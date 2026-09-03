package com.example.agroventa.network;

import com.example.agroventa.data.Product;
import com.example.agroventa.data.PurchaseRequest;
import com.example.agroventa.data.SaleRecord;
import com.example.agroventa.data.UserData;
import com.example.agroventa.data.UserProfile;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AgroVentaApi {

    @POST("auth/login")
    Call<Map<String, String>> login(@Body Map<String, String> body);

    @POST("auth/register")
    Call<Map<String, String>> register(@Body Map<String, String> body);

    @POST("auth/password-reset")
    Call<Void> requestPasswordReset(@Body Map<String, String> body);

    @GET("products")
    Call<List<Product>> getProducts(@Query("category") String category);

    @POST("products")
    Call<Product> publishProduct(@Header("Authorization") String bearerToken, @Body Product product);

    @POST("orders")
    Call<Void> createPurchase(@Header("Authorization") String bearerToken, @Body PurchaseRequest request);

    @GET("users/profile")
    Call<UserProfile> getUserProfile(@Header("Authorization") String bearerToken);

    @GET("users/sales")
    Call<List<SaleRecord>> getSales(@Header("Authorization") String bearerToken);

    @POST("payments/intent")
    Call<Map<String, String>> createPaymentIntent(@Header("Authorization") String bearerToken, @Body Map<String, String> body);
}

