package com.example.demo.model;

public class Tractor {
    private int id;
    private String modelo;
    private int velocidad;
    private double precioVenta;
    private int idConstruccion;

    public Tractor(int id, String modelo, int velocidad, double precioVenta, int idConstruccion) {
        this.id = id;
        this.modelo = modelo;
        this.velocidad = velocidad;
        this.precioVenta = precioVenta;
        this.idConstruccion = idConstruccion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getIdConstruccion() {
        return idConstruccion;
    }

    public void setIdConstruccion(int idConstruccion) {
        this.idConstruccion = idConstruccion;
    }
    
}
