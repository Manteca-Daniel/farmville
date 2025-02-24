package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import java.io.File;
import java.io.IOException;

public class Ficheros implements CommandLineRunner {
	
    @Override
    public void run(String... args) {
    	errores();
    	duplicados();
    }
    
    private void errores() {
    	
    	try {
            File ficheroDatos = new File("errores.log");            
            if (ficheroDatos.createNewFile()) {
                System.out.println("Fichero "+ ficheroDatos.getName() +" creado");
            }            
            else {
                System.out.println("No se ha podido crear el fichero. Probablemente ya exista.");
            }
        }catch(IOException error){
            System.out.println("Error al crear el fichero.");
            error.printStackTrace();
        }
    	
    }
    
    private void duplicados() {
    	
    	try {
            File ficheroDatos = new File("duplicados.log");            
            if (ficheroDatos.createNewFile()) {
                System.out.println("Fichero "+ ficheroDatos.getName() +" creado");
            }            
            else {
                System.out.println("No se ha podido crear el fichero. Probablemente ya exista.");
            }
        }catch(IOException error){
            System.out.println("Error al crear el fichero.");
            error.printStackTrace();
        }
    	
    }
}
