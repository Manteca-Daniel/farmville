package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.sql.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.model.Granjero_Granjero;

@Service
public class Granjero_GranjeroService {
    private static final String FILE_PATH = "ficheros_csv_farmvile/granjero_granjero/granjero_granjero.csv";
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";
    private static final String DATABASE_NAME = "farmville";
    private static final String ERROR_LOG = "errores.log";
    private static final String DUPLICATES_LOG = "duplicados.log";

    public List<Granjero_Granjero> cargarGranjeros() {
        String dbUrl = URL + DATABASE_NAME;
        List<Granjero_Granjero> granjeros = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(dbUrl, USER, PASSWORD);
             FileWriter erroresWriter = new FileWriter(ERROR_LOG, true);
             FileWriter duplicadosWriter = new FileWriter(DUPLICATES_LOG, true)) {

            // Verificar si la tabla existe
            String checkTableSQL = "SHOW TABLES LIKE 'granjero_granjero'";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(checkTableSQL)) {
                if (!rs.next()) {
                    String createTableSQL = "CREATE TABLE granjero_granjero ("
                            + "id_granjero INT,"
                            + "id_vecino INT,"
                            + "puntos_compartidos INT DEFAULT 0,"
                            + "PRIMARY KEY (id_granjero, id_vecino)"
                            + ") ENGINE=InnoDB DEFAULT CHARSET=latin1;";
                    stmt.executeUpdate(createTableSQL);
                    System.out.println("✅ Tabla 'granjero_granjero' creada.");
                }
            }

            // Leer el archivo CSV y actualizar la base de datos
            try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
                String linea;
                boolean primeraLinea = true;

                String insertOrUpdateSQL = "INSERT INTO granjero_granjero (id_granjero, id_vecino, puntos_compartidos) "
                        + "VALUES (?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE "
                        + "puntos_compartidos = VALUES(puntos_compartidos)";

                try (PreparedStatement pstmt = connection.prepareStatement(insertOrUpdateSQL)) {
                    while ((linea = br.readLine()) != null) {
                        if (primeraLinea) {
                            primeraLinea = false;
                            continue; // Saltar cabecera
                        }

                        String[] datos = linea.split(",");
                        if (datos.length == 3) {
                            try {
                                int idGranjero = datos[0].trim().isEmpty() ? 0 : Integer.parseInt(datos[0].trim());
                                int idVecino = datos[1].trim().isEmpty() ? 0 : Integer.parseInt(datos[1].trim());
                                int puntosCompartidos = datos[2].trim().isEmpty() ? 0 : Integer.parseInt(datos[2].trim());

                                Granjero_Granjero g = new Granjero_Granjero(idGranjero, idVecino, puntosCompartidos);
                                granjeros.add(g);

                                // Insertar o actualizar en la base de datos
                                pstmt.setInt(1, g.getIdGranjero());
                                pstmt.setInt(2, g.getIdVecino());
                                pstmt.setInt(3, g.getPuntosCompartidos());

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

        return granjeros;
    }
}
