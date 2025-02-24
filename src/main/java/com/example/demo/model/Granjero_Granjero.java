package com.example.demo.model;

public class Granjero_Granjero {
	private int id_granjero;
    private int id_vecino;
    private int puntos_compartidos;

    public Granjero_Granjero(int id_granjero, int id_vecino, int puntos_compartidos) {
        this.id_granjero = id_granjero;
        this.id_vecino = id_vecino;
        this.puntos_compartidos = puntos_compartidos;
    }

    public int getIdGranjero() {
        return id_granjero;
    }

    public void setIdGranjero(int id_granjero) {
        this.id_granjero = id_granjero;
    }

    public int getIdVecino() {
        return id_vecino;
    }

    public void setIdVecino(int id_vecino) {
        this.id_vecino = id_vecino;
    }

    public int getPuntosCompartidos() {
        return puntos_compartidos;
    }

    public void setPuntosCompartidos(int puntos_compartidos) {
        this.puntos_compartidos = puntos_compartidos;
    }
}
