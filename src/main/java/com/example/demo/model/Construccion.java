package com.example.demo.model;

public class Construccion {

	 	private int id;
	    private String nombre;
	    private double precio;
	    private int id_granjero;

	    public Construccion (int id, String nombre, double precio, int id_granjero) {
	        this.id = id;
	        this.nombre = nombre;
	        this.precio = precio;
	        this.id_granjero = id_granjero;
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

	    public double getPrecio() {
	        return precio;
	    }

	    public void setPrecio(double precio) {
	        this.precio = precio;
	    }

	    public int getIdGranjero() {
	        return id_granjero;
	    }

	    public void setIdGranjero(int id_granjero) {
	        this.id_granjero = id_granjero;
	    }
}
