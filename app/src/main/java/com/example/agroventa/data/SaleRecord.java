package com.example.agroventa.data;

public class SaleRecord {
    private String productName;
    private String city;
    private int quantity;
    private String total;
    private String status;
    private String date;

    public SaleRecord(String productName, String city, int quantity, String total, String status, String date) {
        this.productName = productName;
        this.city = city;
        this.quantity = quantity;
        this.total = total;
        this.status = status;
        this.date = date;
    }

    public String getProductName() {
        return productName;
    }

    public String getCity() {
        return city;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getTotal() {
        return total;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }
}

