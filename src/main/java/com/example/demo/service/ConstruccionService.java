package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.model.Construccion;

@Service
public class ConstruccionService {
    private static final String FILE_PATH = "ficheros_csv_farmvile\\construcciones\\construcciones.csv";
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";
    private static final String DATABASE_NAME = "farmville";
    private static final String ERROR_LOG = "errores.log";
    private static final String DUPLICATES_LOG = "duplicados.log";

    public List<Construccion> cargarConstrucciones() {
        String dbUrl = URL + DATABASE_NAME;
        List<Construccion> construcciones = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);
             FileWriter erroresWriter = new FileWriter(ERROR_LOG, true);
             FileWriter duplicadosWriter = new FileWriter(DUPLICATES_LOG, true)) {

            // Verificar si la tabla existe antes de crearla
            String checkTableSQL = "SHOW TABLES LIKE 'construcciones'";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(checkTableSQL)) {
                if (!rs.next()) {
                    String createTableSQL = "CREATE TABLE construcciones ("
                            + "id INT PRIMARY KEY,"
                            + "nombre VARCHAR(500) NOT NULL,"
                            + "precio DOUBLE NOT NULL,"
                            + "id_granjero INT DEFAULT 1,"
                            + "FOREIGN KEY (id_granjero) REFERENCES granjeros(id)"
                            + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
                    stmt.executeUpdate(createTableSQL);
                    System.out.println("✅ Tabla 'construcciones' creada.");
                }
            }

            // Leer el archivo CSV y actualizar la base de datos
            try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                String linea;
                boolean primeraLinea = true;

                String insertOrUpdateSQL = "INSERT INTO construcciones (id, nombre, precio, id_granjero) "
                        + "VALUES (?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "nombre = VALUES(nombre), "
                        + "precio = VALUES(precio), "
                        + "id_granjero = VALUES(id_granjero)";

                try (PreparedStatement pstmt = connection.prepareStatement(insertOrUpdateSQL)) {
                    while ((linea = br.readLine()) != null) {
                        if (primeraLinea) {
                            primeraLinea = false;
                            continue; // Saltar cabecera
                        }

                        String[] datos = linea.split(",");
                        if (datos.length >= 3) {
                            try {
                                int id = Integer.parseInt(datos[0].trim());
                                String nombre = datos[1].trim();
                                double precio = Double.parseDouble(datos[2].trim());
                                int idGranjero = datos.length > 3 && !datos[3].trim().isEmpty()
                                        ? Integer.parseInt(datos[3].trim())
                                        : 1; // Valor predeterminado si está vacío

                                Construccion c = new Construccion(id, nombre, precio, idGranjero);
                                construcciones.add(c);

                                // Insertar o actualizar en la base de datos
                                pstmt.setInt(1, c.getId());
                                pstmt.setString(2, c.getNombre());
                                pstmt.setDouble(3, c.getPrecio());
                                pstmt.setObject(4, c.getIdGranjero() == 0 ? null : c.getIdGranjero(), Types.INTEGER);
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

        return construcciones;
    }
}
