package com.example.demo.model;

public class Riego {
	private int id;
    private String tipo;
    private int velocidad;
    private int id_plantacion;

    public Riego(int id, String tipo, int velocidad, int id_plantacion) {
        this.id = id;
        this.tipo = tipo;
        this.velocidad = velocidad;
        this.id_plantacion = id_plantacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public int getIdPlantacion() {
        return id_plantacion;
    }

    public void setIdPlantacion(int id_plantacion) {
        this.id_plantacion = id_plantacion;
    }
    
    @Override
    public String toString() {
        return String.format("ID: %-3d | tipo: %-25s | velocidad: %-2d | id_plantacion: %-2d",
                id, tipo, velocidad, id_plantacion);
    }
}
