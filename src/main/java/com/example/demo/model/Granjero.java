package com.example.demo.model;

public class Granjero {
    private int id;
    private String nombre;
    private String descripcion;
    private double dinero;
    private int puntos;
    private int nivel;

    public Granjero(int id, String nombre, String descripcion, double dinero, int puntos, int nivel) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.dinero = dinero;
        this.puntos = puntos;
        this.nivel = nivel;
    }

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
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getDinero() {
        return dinero;
    }

    public void setDinero(double dinero) {
        this.dinero = dinero;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    @Override
    public String toString() {
        return String.format("ID: %-3d | Nombre: %-25s | Tipo: %-12s | Dinero: %8.2f | Puntos: %-3d | Nivel: %-2d",
                id, nombre, descripcion, dinero, puntos, nivel);
    }
}
