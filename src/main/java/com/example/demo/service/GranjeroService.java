package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.sql.*;

import com.example.demo.model.Granjero;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GranjeroService {
    private static final String FILE_PATH = "ficheros_csv_farmvile\\granjeros\\granjeros.csv";
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";
    private static final String DATABASE_NAME = "farmville";
    private static final String ERROR_LOG = "errores.log";
    private static final String DUPLICATES_LOG = "duplicados.log";

    public List<Granjero> leerGranjeros() {
        String dbUrl = URL + DATABASE_NAME;
        List<Granjero> granjeros = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);
             FileWriter erroresWriter = new FileWriter(ERROR_LOG, true);
             FileWriter duplicadosWriter = new FileWriter(DUPLICATES_LOG, true)) {

            // Verificar si la tabla existe antes de crearla
            String checkTableSQL = "SHOW TABLES LIKE 'granjeros'";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(checkTableSQL)) {
                if (!rs.next()) {
                    // Si la tabla no existe, crearla
                    String createTableSQL = "CREATE TABLE granjeros ("
                            + "id INT PRIMARY KEY,"
                            + "nombre VARCHAR(500) NOT NULL,"
                            + "descripcion VARCHAR(500) NOT NULL,"
                            + "dinero FLOAT NOT NULL DEFAULT 1000,"
                            + "puntos INT NOT NULL DEFAULT 0,"
                            + "nivel INT NOT NULL DEFAULT 1"
                            + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
                    stmt.executeUpdate(createTableSQL);
                    System.out.println("✅ Tabla 'granjeros' creada.");
                }
            }

            // Leer el archivo CSV y actualizar los datos en la base de datos
            try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                String linea;
                boolean primeraLinea = true;

                String insertOrUpdateSQL = "INSERT INTO granjeros (id, nombre, descripcion, dinero, puntos, nivel) "
                        + "VALUES (?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "nombre = VALUES(nombre), "
                        + "descripcion = VALUES(descripcion), "
                        + "dinero = VALUES(dinero), "
                        + "puntos = VALUES(puntos), "
                        + "nivel = VALUES(nivel)";

                try (PreparedStatement pstmt = connection.prepareStatement(insertOrUpdateSQL)) {
                    while ((linea = br.readLine()) != null) {
                        if (primeraLinea) {
                            primeraLinea = false;
                            continue; // Saltar la cabecera
                        }

                        String[] datos = linea.split(",");
                        if (datos.length == 6) {
                            try {
                                int id = Integer.parseInt(datos[0].trim());
                                String nombre = datos[1].trim();
                                String descripcion = datos[2].trim();
                                double dinero = Double.parseDouble(datos[3].trim());
                                int puntos = Integer.parseInt(datos[4].trim());
                                int nivel = Integer.parseInt(datos[5].trim());

                                Granjero g = new Granjero(id, nombre, descripcion, dinero, puntos, nivel);
                                granjeros.add(g);

                                // Insertar o actualizar en la base de datos
                                pstmt.setInt(1, g.getId());
                                pstmt.setString(2, g.getNombre());
                                pstmt.setString(3, g.getDescripcion());
                                pstmt.setDouble(4, g.getDinero());
                                pstmt.setInt(5, g.getPuntos());
                                pstmt.setInt(6, g.getNivel());

                                pstmt.executeUpdate();
                            } catch (NumberFormatException e) {
                                String errorMsg = "Error en formato numérico: " + linea;
                                System.err.println(errorMsg);
                                erroresWriter.write(errorMsg + "\n");
                            }
                        } else {
                            String errorMsg = "Fila con formato incorrecto: " + linea;
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

        return granjeros;
    }
}
