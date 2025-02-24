package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.Tractor;

@Service
public class TractorService {
    private static final String FILE_PATH = "ficheros_csv_farmvile/tractores/tractores.csv";
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";
    private static final String DATABASE_NAME = "farmville";
    private static final String ERROR_LOG = "errores.log";
    private static final String DUPLICATES_LOG = "duplicados.log";

    public List<Tractor> cargarTractores() {
        String dbUrl = URL + DATABASE_NAME;
        List<Tractor> tractores = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);
             FileWriter erroresWriter = new FileWriter(ERROR_LOG, true);
             FileWriter duplicadosWriter = new FileWriter(DUPLICATES_LOG, true)) {

            String checkTableSQL = "SHOW TABLES LIKE 'tractores'";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(checkTableSQL)) {
                if (!rs.next()) {
                    String createTableSQL = "CREATE TABLE tractores ("
                            + "id INT PRIMARY KEY,"
                            + "modelo VARCHAR(255),"
                            + "velocidad INT,"
                            + "precio_venta DOUBLE,"
                            + "id_construccion INT"
                            + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
                    stmt.executeUpdate(createTableSQL);
                    System.out.println("✅ Tabla 'tractores' creada.");
                }
            }

            try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                String linea;
                boolean primeraLinea = true;

                String insertOrUpdateSQL = "INSERT INTO tractores (id, modelo, velocidad, precio_venta, id_construccion) "
                        + "VALUES (?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "modelo = VALUES(modelo), "
                        + "velocidad = VALUES(velocidad), "
                        + "precio_venta = VALUES(precio_venta), "
                        + "id_construccion = VALUES(id_construccion)";

                try (PreparedStatement pstmt = connection.prepareStatement(insertOrUpdateSQL)) {
                    while ((linea = br.readLine()) != null) {
                        if (primeraLinea) {
                            primeraLinea = false;
                            continue; 
                        }

                        String[] datos = linea.split(",");
                        if (datos.length == 5) {
                            try {
                                int id = datos[0].trim().isEmpty() ? 0 : Integer.parseInt(datos[0].trim());
                                String modelo = datos[1].trim().isEmpty() ? "" : datos[1].trim();
                                int velocidad = datos[2].trim().isEmpty() ? 0 : Integer.parseInt(datos[2].trim());
                                double precioVenta = datos[3].trim().isEmpty() ? 0.0 : Double.parseDouble(datos[3].trim());
                                int idConstruccion = datos[4].trim().isEmpty() ? 0 : Integer.parseInt(datos[4].trim());

                                Tractor t = new Tractor(id, modelo, velocidad, precioVenta, idConstruccion);
                                tractores.add(t);

                                pstmt.setInt(1, t.getId());
                                pstmt.setString(2, t.getModelo());
                                pstmt.setInt(3, t.getVelocidad());
                                pstmt.setDouble(4, t.getPrecioVenta());
                                pstmt.setInt(5, t.getIdConstruccion());

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

        return tractores;
    }
}
