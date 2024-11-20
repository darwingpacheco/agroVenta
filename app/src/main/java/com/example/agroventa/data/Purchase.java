package com.example.agroventa.data;

import java.sql.Timestamp;

public class Purchase {
    private String productComprado;
    private String priceComprado;
    private String cantidad;
    private String fecha;

    public Purchase (String productComprado, String priceComprado, String cantidad, String fecha) {
        this.productComprado = productComprado;
        this.priceComprado = priceComprado;
        this.cantidad = cantidad;
        this.fecha = fecha;
    }

    public String getProductComprado() {
        return productComprado;
    }

    public void setProductComprado(String productComprado) {
        this.productComprado = productComprado;
    }

    public String getPriceComprado() {
        return priceComprado;
    }

    public void setPriceComprado(String priceComprado) {
        this.priceComprado = priceComprado;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
