package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.*;

public class Database implements CommandLineRunner {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";
    private static final String DATABASE_NAME = "farmville";

    @Override
    public void run(String... args) {
        createDatabase();
        createTablas();
    }


    private void createDatabase() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String sql = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            statement.executeUpdate(sql);
            System.out.println("✅ Base de datos creada o ya existente: " + DATABASE_NAME);

        } catch (SQLException e) {
            System.err.println("❌ Error al crear la base de datos: " + e.getMessage());
        }
    }
    
    private void createTablas() {
    	createGranjeroTable();
    	createPlantacionesTable();
    	createRiegosTable();
    	createConstruccionesTable();
    	createTractoresTable();
    	createGranjero_GranjeroTable();
    }
    
    private void createGranjeroTable() {
        String dbUrl = URL + DATABASE_NAME;
        try (Connection connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);
             Statement stmt = connection.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS granjeros ("
                    + "id INT PRIMARY KEY,"
                    + "nombre VARCHAR(500) NOT NULL,"
                    + "descripcion VARCHAR(500) NOT NULL,"
                    + "dinero FLOAT NOT NULL DEFAULT 1000,"
                    + "puntos INT NOT NULL DEFAULT 0,"
                    + "nivel INT NOT NULL DEFAULT 1"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

            stmt.executeUpdate(createTableSQL);
            System.out.println("✅ Tabla 'granjeros' creada o ya existente.");

        } catch (SQLException e) {
            System.err.println("❌ Error al crear la tabla 'granjeros': " + e.getMessage());
        }
    }
    
    private void createPlantacionesTable() {
        String dbUrl = URL + DATABASE_NAME;
        try (Connection connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);
             Statement stmt = connection.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS `plantaciones` ("
            		+ "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
            		+ "  `nombre` varchar(255) NOT NULL,"
            		+ "  `precio_compra` float NOT NULL DEFAULT '0',"
            		+ "  `precio_venta` float NOT NULL DEFAULT '0',"
            		+ "  `proxima_cosecha` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
            		+ "  `id_granjero` int(11) unsigned NOT NULL,"
            		+ "  PRIMARY KEY (`id`),"
            		+ "  KEY `id_granjero` (`id_granjero`)"
            		+ ") ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=101 ;";

            stmt.executeUpdate(createTableSQL);
            System.out.println("✅ Tabla 'plantaciones' creada o ya existente.");

        } catch (SQLException e) {
            System.err.println("❌ Error al crear la tabla 'plantaciones': " + e.getMessage());
        }
    }
    
    private void createRiegosTable() {
        String dbUrl = URL + DATABASE_NAME;
        try (Connection connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);
             Statement stmt = connection.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS `riegos` ("
            		+ "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
            		+ "  `tipo` varchar(255) NOT NULL,"
            		+ "  `velocidad` int(11) NOT NULL DEFAULT '10',"
            		+ "  `id_plantacion` int(11) unsigned DEFAULT NULL,"
            		+ "  PRIMARY KEY (`id`),"
            		+ "  KEY `id_plantacion` (`id_plantacion`)"
            		+ ") ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=101 ;";

            stmt.executeUpdate(createTableSQL);
            System.out.println("✅ Tabla 'riegos' creada o ya existente.");

        } catch (SQLException e) {
            System.err.println("❌ Error al crear la tabla 'riegos': " + e.getMessage());
        }
    }
    
    private void createConstruccionesTable() {
        String dbUrl = URL + DATABASE_NAME;
        try (Connection connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);
             Statement stmt = connection.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS `construcciones` ("
            		+ "  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,"
            		+ "  `nombre` varchar(255) NOT NULL,"
            		+ "  `precio` float NOT NULL DEFAULT '0',"
            		+ "  `id_granjero` int(11) unsigned NOT NULL,"
            		+ "  PRIMARY KEY (`id`),"
            		+ "  KEY `id_granjero` (`id_granjero`)"
            		+ ") ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=101 ;";

            stmt.executeUpdate(createTableSQL);
            System.out.println("✅ Tabla 'construcciones' creada o ya existente.");

        } catch (SQLException e) {
            System.err.println("❌ Error al crear la tabla 'construcciones': " + e.getMessage());
        }
    }
    
    private void createTractoresTable() {
        String dbUrl = URL + DATABASE_NAME;
        try (Connection connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);
             Statement stmt = connection.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS `tractores` ("
            		+ "  `id` int(11) NOT NULL AUTO_INCREMENT,"
            		+ "  `modelo` varchar(25) NOT NULL,"
            		+ "  `velocidad` int(11) NOT NULL DEFAULT '10',"
            		+ "  `precio_venta` float NOT NULL DEFAULT '0',"
            		+ "  `id_construccion` int(11) unsigned DEFAULT NULL,"
            		+ "  PRIMARY KEY (`id`),"
            		+ "  KEY `id_construccion` (`id_construccion`)"
            		+ ") ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=101 ;";

            stmt.executeUpdate(createTableSQL);
            System.out.println("✅ Tabla 'tractores' creada o ya existente.");

        } catch (SQLException e) {
            System.err.println("❌ Error al crear la tabla 'tractores': " + e.getMessage());
        }
    }
    
    private void createGranjero_GranjeroTable() {
        String dbUrl = URL + DATABASE_NAME;
        try (Connection connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);
             Statement stmt = connection.createStatement()) {

            String createTableSQL = "CREATE TABLE IF NOT EXISTS `granjero_granjero` ("
            		+ "  `id_granjero` int(11) unsigned NOT NULL,"
            		+ "  `id_vecino` int(11) unsigned NOT NULL,"
            		+ "  `puntos_compartidos` int(11) NOT NULL,"
            		+ "  PRIMARY KEY (`id_granjero`,`id_vecino`),"
            		+ "  KEY `id_vecino` (`id_vecino`)"
            		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";

            stmt.executeUpdate(createTableSQL);
            System.out.println("✅ Tabla 'granjero_granjero' creada o ya existente.");

        } catch (SQLException e) {
            System.err.println("❌ Error al crear la tabla 'granjero_granjero': " + e.getMessage());
        }
    }
    
    
}