package com.example.demo.model;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Plantacion {

    private int id;
    private String nombre;
    private double precioCompra;
    private double precioVenta;
    private Date proximaCosecha;
    private int idGranjero;

    private static final String DEFAULT_NOMBRE = "USUARIO_DESCONOCIDO";
    private static final double DEFAULT_PRECIO = 0.0;
    private static final Date DEFAULT_FECHA;

    static {
        try {
            DEFAULT_FECHA = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-01-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("Error al inicializar la fecha por defecto", e);
        }
    }

    // Constructor con String para precios
    public Plantacion(int id, String nombre, String precioCompra, String precioVenta, Date proximaCosecha, int idGranjero) {
        this.id = id;
        this.nombre = (nombre == null || nombre.trim().isEmpty()) ? DEFAULT_NOMBRE : nombre;
        this.precioCompra = parseDoubleOrDefault(precioCompra, DEFAULT_PRECIO);
        this.precioVenta = parseDoubleOrDefault(precioVenta, DEFAULT_PRECIO);
        this.proximaCosecha = (proximaCosecha == null) ? DEFAULT_FECHA : proximaCosecha;
        this.idGranjero = idGranjero;
    }
    
    // Constructor con double para precios
    public Plantacion(int id, String nombre, double precioCompra, double precioVenta, Date proximaCosecha, int idGranjero) {
        this.id = id;
        this.nombre = (nombre == null || nombre.trim().isEmpty()) ? DEFAULT_NOMBRE : nombre;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.proximaCosecha = (proximaCosecha == null) ? DEFAULT_FECHA : proximaCosecha;
        this.idGranjero = idGranjero;
    }

    private double parseDoubleOrDefault(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = (nombre == null || nombre.trim().isEmpty()) ? DEFAULT_NOMBRE : nombre;
    }

    public double getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(String precioCompra) {
        this.precioCompra = parseDoubleOrDefault(precioCompra, DEFAULT_PRECIO);
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(String precioVenta) {
        this.precioVenta = parseDoubleOrDefault(precioVenta, DEFAULT_PRECIO);
    }

    public Date getProximaCosecha() {
        return proximaCosecha;
    }

    public void setProximaCosecha(Date proximaCosecha) {
        this.proximaCosecha = (proximaCosecha == null) ? DEFAULT_FECHA : proximaCosecha;
    }

    public int getIdGranjero() {
        return idGranjero;
    }

    public void setIdGranjero(int idGranjero) {
        this.idGranjero = idGranjero;
    }

    @Override
    public String toString() {
        return "Plantacion [id=" + id + ", nombre=" + nombre + ", precioCompra=" + precioCompra + ", precioVenta="
                + precioVenta + ", proximaCosecha=" + proximaCosecha + ", idGranjero=" + idGranjero + "]";
    }
}
