package com.example.agroventa.data;

public class PurchaseRequest {
    private String productId;
    private String productName;
    private int quantity;
    private String unitPrice;
    private String totalPrice;
    private String buyerName;
    private String buyerPhone;
    private String dispatchCity;
    private String paymentMethod;

    public PurchaseRequest(String productId, String productName, int quantity, String unitPrice, String totalPrice,
                           String buyerName, String buyerPhone, String dispatchCity, String paymentMethod) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.buyerName = buyerName;
        this.buyerPhone = buyerPhone;
        this.dispatchCity = dispatchCity;
        this.paymentMethod = paymentMethod;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public String getDispatchCity() {
        return dispatchCity;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}

