package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.demo.model.Plantacion;

@Service
public class PlantacionService {
    private static final String FILE_PATH = "ficheros_csv_farmvile\\plantaciones\\plantaciones.csv";
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";
    private static final String DATABASE_NAME = "farmville";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String ERROR_LOG = "errores.log";
    private static final String DUPLICATES_LOG = "duplicados.log";

    public List<Plantacion> cargarPlantaciones() {
        String dbUrl = URL + DATABASE_NAME;
        List<Plantacion> plantaciones = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);
             FileWriter erroresWriter = new FileWriter(ERROR_LOG, true);
             FileWriter duplicadosWriter = new FileWriter(DUPLICATES_LOG, true)) {

            // Verificar si la tabla existe antes de crearla
            String checkTableSQL = "SHOW TABLES LIKE 'plantaciones'";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(checkTableSQL)) {
                if (!rs.next()) {
                    // Crear tabla si no existe
                    String createTableSQL = "CREATE TABLE plantaciones ("
                            + "id INT PRIMARY KEY,"
                            + "nombre VARCHAR(500) NOT NULL,"
                            + "precio_compra FLOAT NOT NULL,"
                            + "precio_venta FLOAT NOT NULL,"
                            + "proxima_cosecha DATE NOT NULL,"
                            + "id_granjero INT NOT NULL,"
                            + "FOREIGN KEY (id_granjero) REFERENCES granjeros(id)"
                            + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
                    stmt.executeUpdate(createTableSQL);
                    System.out.println("✅ Tabla 'plantaciones' creada.");
                }
            }

            // Leer el archivo CSV y actualizar la base de datos
            try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                String linea;
                boolean primeraLinea = true;

                String insertOrUpdateSQL = "INSERT INTO plantaciones (id, nombre, precio_compra, precio_venta, proxima_cosecha, id_granjero) "
                        + "VALUES (?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "nombre = VALUES(nombre), "
                        + "precio_compra = VALUES(precio_compra), "
                        + "precio_venta = VALUES(precio_venta), "
                        + "proxima_cosecha = VALUES(proxima_cosecha), "
                        + "id_granjero = VALUES(id_granjero)";

                try (PreparedStatement pstmt = connection.prepareStatement(insertOrUpdateSQL)) {
                    while ((linea = br.readLine()) != null) {
                        if (primeraLinea) {
                            primeraLinea = false;
                            continue; // Saltar cabecera
                        }

                        String[] datos = linea.split(",");
                        if (datos.length == 6) {
                            try {
                                int id = Integer.parseInt(datos[0].trim());
                                String nombre = datos[1].trim();
                                double precioCompra = Double.parseDouble(datos[2].trim());
                                double precioVenta = Double.parseDouble(datos[3].trim());
                                Date proximaCosecha = DATE_FORMAT.parse(datos[4].trim());
                                int idGranjero = Integer.parseInt(datos[5].trim());

                                Plantacion p = new Plantacion(id, nombre, precioCompra, precioVenta, proximaCosecha, idGranjero);
                                plantaciones.add(p);

                                // Insertar o actualizar en la base de datos
                                pstmt.setInt(1, p.getId());
                                pstmt.setString(2, p.getNombre());
                                pstmt.setDouble(3, p.getPrecioCompra());
                                pstmt.setDouble(4, p.getPrecioVenta());
                                pstmt.setDate(5, new java.sql.Date(p.getProximaCosecha().getTime()));
                                pstmt.setInt(6, p.getIdGranjero());

                                pstmt.executeUpdate();
                            } catch (NumberFormatException e) {
                                String errorMsg = "❌ Error en formato numérico: " + linea;
                                System.err.println(errorMsg);
                                erroresWriter.write(errorMsg + "\n");
                            } catch (ParseException e) {
                                String errorMsg = "❌ Error al parsear fecha: " + linea;
                                System.err.println(errorMsg);
                                erroresWriter.write(errorMsg + "\n");
                            }
                        } else {
                            String errorMsg = "❌ Fila con formato incorrecto: " + linea;
                            System.err.println(errorMsg);
                            erroresWriter.write(errorMsg + "\n");
                        }
                    }
                }
            } catch (IOException e) {
                String errorMsg = "❌ Error al leer el archivo CSV: " + e.getMessage();
                System.err.println(errorMsg);
                erroresWriter.write(errorMsg + "\n");
            }
        } catch (SQLException e) {
            String errorMsg = "❌ Error de conexión o consulta SQL: " + e.getMessage();
            System.err.println(errorMsg);
            try (FileWriter erroresWriter = new FileWriter(ERROR_LOG, true)) {
                erroresWriter.write(errorMsg + "\n");
            } catch (IOException ioException) {
                System.err.println("❌ Error al escribir en el archivo de errores: " + ioException.getMessage());
            }
        } catch (IOException e) {
            System.err.println("❌ Error al manejar los archivos de log: " + e.getMessage());
        }

        return plantaciones;
    }
}
