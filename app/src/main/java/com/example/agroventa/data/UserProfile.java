package com.example.agroventa.data;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private String name;
    private String email;
    private String phone;
    private List<Purchase> purchases;
    private List<SaleRecord> sales;

    public UserProfile(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.purchases = new ArrayList<>();
        this.sales = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public List<SaleRecord> getSales() {
        return sales;
    }

    public void setPurchases(List<Purchase> purchases) {
        this.purchases = purchases;
    }

    public void setSales(List<SaleRecord> sales) {
        this.sales = sales;
    }
}

