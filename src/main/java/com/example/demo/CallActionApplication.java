package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.example.demo.model.Construccion;
import com.example.demo.model.Granjero;
import com.example.demo.model.Granjero_Granjero;
import com.example.demo.model.Plantacion;
import com.example.demo.model.Riego;
import com.example.demo.model.Tractor;
import com.example.demo.service.ConstruccionService;
import com.example.demo.service.GranjeroService;
import com.example.demo.service.Granjero_GranjeroService;
import com.example.demo.service.PlantacionService;
import com.example.demo.service.RiegoService;
import com.example.demo.service.TractorService;

import java.util.List;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CallActionApplication {
	
	Database ds=new Database();
	Ficheros fc=new Ficheros();
	
	public CallActionApplication() {
		ds.run(null);
		fc.run(null);
	}

	public static void main(String[] args) {
		SpringApplication.run(CallActionApplication.class, args);
		System.out.println("Spring ejecutadfo con exito");
	}
	
	 @Bean
	    CommandLineRunner init(GranjeroService granjeroService, PlantacionService plantacionesService, RiegoService riegoService, ConstruccionService construccionService, TractorService tractorService, Granjero_GranjeroService granjero_granjeroService) {
	        return args -> {
	        	List<Plantacion> plantaciones=plantacionesService.cargarPlantaciones();
	            List<Granjero> granjeros = granjeroService.leerGranjeros();
	            List<Riego> riegos = riegoService.cargarRiegos();
	            List<Construccion> construcciones=construccionService.cargarConstrucciones();
	            List<Tractor> tractores=tractorService.cargarTractores();
	            List<Granjero_Granjero> granjeros_granjeros=granjero_granjeroService.cargarGranjeros();
	        };
	    }

}
