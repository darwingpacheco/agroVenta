package com.example.agroventa;

import java.util.List;

public class Product {
    private String title;
    private String description;
    private List<String> imageResourceId;
    private String ubication;
    private String phoneContact;
    private String nameSeller;
    private String price;
    private String tipo;
    private String medida;
    private int cantidad;
    private String productId;

    public Product() {
        // Firestore necesita este constructor vacío
    }

    public Product(String title, String description, List<String> imageResourceId, String ubication, String price, String phoneContact, String nameSeller, String tipo, String medida, int Cantidad) {
        this.title = title;
        this.description = description;
        this.imageResourceId = imageResourceId;
        this.ubication = ubication;
        this.price = price;
        this.phoneContact = phoneContact;
        this.nameSeller = nameSeller;
        this.tipo = tipo;
        this.medida = medida;
        this.cantidad = Cantidad;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getImageResourceId() {
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getProductId() {
        return productId;
    }

    public Product setProductId(String productId) {
        this.productId = productId;
        return this;
    }
}
