package com.example.agroventa;

public class Product {
    private String title;
    private String description;
    private int imageResourceId;
    private String ubication;
    private String phoneContact;
    private String nameSeller;
    private double price;
    private String tipo;

    public Product(String title, String description, int imageResourceId, String ubication, double price, String phoneContact, String nameSeller, String tipo) {
        this.title = title;
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.ubication = ubication;
        this.price = price;
        this.phoneContact = phoneContact;
        this.nameSeller = nameSeller;
        this.tipo = tipo;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getUbication() {
        return ubication;
    }

    public void setUbication(String ubication) {
        this.ubication = ubication;
    }

    public String getPhoneContact() {
        return phoneContact;
    }

    public void setPhoneContact(String phoneContact) {
        this.phoneContact = phoneContact;
    }

    public String getNameSeller() {
        return nameSeller;
    }

    public void setNameSeller(String nameSeller) {
        this.nameSeller = nameSeller;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
