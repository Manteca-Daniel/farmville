package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.Riego;

@Service
public class RiegoService {
    private static final String FILE_PATH = "ficheros_csv_farmvile\\riegos\\riegos.csv";
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";
    private static final String DATABASE_NAME = "farmville";
    private static final String ERROR_LOG = "errores.log";
    private static final String DUPLICATES_LOG = "duplicados.log";

    public List<Riego> cargarRiegos() {
        String dbUrl = URL + DATABASE_NAME;
        List<Riego> riegos = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);
             FileWriter erroresWriter = new FileWriter(ERROR_LOG, true);
             FileWriter duplicadosWriter = new FileWriter(DUPLICATES_LOG, true)) {

            // Verificar si la tabla existe antes de crearla
            String checkTableSQL = "SHOW TABLES LIKE 'riegos'";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(checkTableSQL)) {
                if (!rs.next()) {
                    // Crear tabla si no existe
                    String createTableSQL = "CREATE TABLE riegos ("
                            + "id INT PRIMARY KEY,"
                            + "tipo VARCHAR(255),"
                            + "velocidad INT,"
                            + "id_plantacion INT,"
                            + "FOREIGN KEY (id_plantacion) REFERENCES plantaciones(id)"
                            + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
                    stmt.executeUpdate(createTableSQL);
                    System.out.println("✅ Tabla 'riegos' creada.");
                }
            }

            // Leer el archivo CSV y actualizar la base de datos
            try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                String linea;
                boolean primeraLinea = true;

                String insertOrUpdateSQL = "INSERT INTO riegos (id, tipo, velocidad, id_plantacion) "
                        + "VALUES (?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "tipo = VALUES(tipo), "
                        + "velocidad = VALUES(velocidad), "
                        + "id_plantacion = VALUES(id_plantacion)";

                try (PreparedStatement pstmt = connection.prepareStatement(insertOrUpdateSQL)) {
                    while ((linea = br.readLine()) != null) {
                        if (primeraLinea) {
                            primeraLinea = false;
                            continue; // Saltar cabecera
                        }

                        String[] datos = linea.split(",");
                        if (datos.length == 4) {
                            try {
                                int id = Integer.parseInt(datos[0].trim());
                                String tipo = datos[1].trim().isEmpty() ? "ERROR" : datos[1].trim();
                                Integer velocidad = datos[2].trim().isEmpty() ? 0 : Integer.parseInt(datos[2].trim());
                                Integer idPlantacion = datos[3].trim().isEmpty() ? 0 : Integer.parseInt(datos[3].trim());

                                Riego r = new Riego(id, tipo, velocidad != null ? velocidad : 0, idPlantacion != null ? idPlantacion : 0);
                                riegos.add(r);

                                // Insertar o actualizar en la base de datos
                                pstmt.setInt(1, r.getId());
                                pstmt.setString(2, r.getTipo());
                                if (velocidad != null) {
                                    pstmt.setInt(3, velocidad);
                                } else {
                                    pstmt.setNull(3, Types.INTEGER);
                                }
                                if (idPlantacion != null) {
                                    pstmt.setInt(4, idPlantacion);
                                } else {
                                    pstmt.setNull(4, Types.INTEGER);
                                }

                                pstmt.executeUpdate();
                            } catch (NumberFormatException e) {
                                String errorMsg = "❌ Error en formato numérico: " + linea;
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

        return riegos;
    }
}
