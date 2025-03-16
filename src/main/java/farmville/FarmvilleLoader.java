package farmville;

import org.apache.commons.csv.*;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class FarmvilleLoader {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("❌ Debes proporcionar el archivo de propiedades como parámetro.");
            return;
        }
        
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(args[0])) {
            properties.load(input);
        } catch (IOException e) {
            System.err.println("❌ Error al leer el archivo de configuración.");
            e.printStackTrace();
            return;
        }

        String dbUrl = properties.getProperty("CADENA_CONEXION");
        String dbUser = properties.getProperty("USUARIO_BBDD");
        String dbPassword = properties.getProperty("PASSWORD_BBDD");
        String basePath =properties.getProperty("BASE_PATH"); 

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            conn.setAutoCommit(false);
            System.out.println("✅ Conexión exitosa a la base de datos.");
            
            
            procesarCSV(conn, 
            	    basePath + "granjeros/granjeros.csv",
            	    basePath + "granjeros/granjeros_error.log",
            	    basePath + "granjeros/granjeros_duplicados.log");

            procesarCSV(conn, 
            		basePath + "plantaciones/plantaciones.csv",
            		basePath + "plantaciones/plantaciones_error.log",
            		basePath + "plantaciones/plantaciones_duplicados.log");

            procesarCSV(conn, 
            		basePath + "riegos/riegos.csv",
            		basePath + "riegos/riegos_error.log",
            		basePath + "riegos/riegos_duplicados.log");

            procesarCSV(conn, 
            		basePath + "construcciones/construcciones.csv", 
            		basePath + "construcciones/construcciones_error.log", 
            		basePath + "construcciones/construcciones_duplicados.log");
            
            procesarCSV(conn, 
            		basePath + "tractores/tractores.csv",
            		basePath + "tractores/tractores_error.log",
            		basePath + "tractores/tractores_duplicados.log");

            procesarCSV(conn, 
            		basePath + "granjero_granjero/granjero_granjero.csv",
            		basePath + "granjero_granjero/granjero_granjero_error.log",
            		basePath + "granjero_granjero/granjero_granjero_duplicados.log");
            conn.commit();
            System.out.println("✅ Todos los datos han sido procesados y confirmados.");
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión con la base de datos.");
            e.printStackTrace();
        }
    }

    private static void procesarArchivosCSV(Connection conn, String csvDirPath, String errorLog, String duplicadosLog) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(csvDirPath), "*.csv")) {
            for (Path filePath : stream) {
                System.out.println("📂 Procesando archivo: " + filePath.getFileName());
                procesarCSV(conn, filePath.toString(), errorLog, duplicadosLog);
            }
        } catch (IOException e) {
            System.err.println("❌ Error al leer los archivos CSV. "+csvDirPath);
            e.printStackTrace();
        }
    }
    
    public static String obtenerNombreTabla(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString(); 
        int lastDotIndex = fileName.lastIndexOf('.'); 
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex).toLowerCase(); 
        }
        return fileName.toLowerCase();
    }
    
    private static void procesarCSV(Connection conn, String filePath, String errorLog, String duplicadosLog) {
        try (Reader reader = new FileReader(filePath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);
            String tabla = obtenerNombreTabla(filePath);
            Savepoint savepoint = conn.setSavepoint("InicioCarga");

            for (CSVRecord record : records) {
                try {
                    procesarEntidad(conn, record, tabla, errorLog, duplicadosLog);
                    conn.commit();
                } catch (SQLException e) {
                    conn.rollback(savepoint);
                    registrarEnLog(errorLog, "❌ Error en entidad " + tabla + ": " + e.getMessage());
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void procesarEntidad(Connection conn, CSVRecord record, String tabla, String errorLog, String duplicadosLog) throws SQLException {
        Map<String, String> valoresCSV = new LinkedHashMap<>();
        List<String> headersList = new ArrayList<>(record.toMap().keySet()); 

     
	     for (String header : headersList) {
	         valoresCSV.put(header, record.get(header));
	     }

        String query = "SELECT * FROM " + tabla + " WHERE "+headersList.getFirst() +" = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(query)) {
            checkStmt.setString(1, valoresCSV.get(headersList.getFirst()));
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                Map<String, String> valoresDB = new HashMap<>();
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    valoresDB.put(metaData.getColumnName(i), rs.getString(i));
                }

                List<String> columnasActualizar = new ArrayList<>();
                List<String> valoresActualizar = new ArrayList<>();
                for (String key : valoresCSV.keySet()) {
                    String valorCSV = valoresCSV.get(key);
                    String valorDB = valoresDB.getOrDefault(key, "");
                    if (valorCSV != null && !valorCSV.equals(valorDB)) {
                        columnasActualizar.add(key + " = ?");
                        valoresActualizar.add(valorCSV);
                    }
                }

                if (!columnasActualizar.isEmpty()) {
                    String updateSQL = "UPDATE " + tabla + " SET " + String.join(", ", columnasActualizar) + " WHERE "+headersList.getFirst()+" = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                        for (int i = 0; i < valoresActualizar.size(); i++) {
                            updateStmt.setString(i + 1, valoresActualizar.get(i));
                        }
                        updateStmt.setString(valoresActualizar.size() + 1, valoresCSV.get(headersList.getFirst()));
                        updateStmt.executeUpdate();
                    }
                } else {
                    registrarEnLog(duplicadosLog, "Duplicado sin cambios en " + tabla + ": " + valoresCSV.get(headersList.getFirst()));
                }
            } else {
                String insertSQL = "INSERT INTO " + tabla + " (" + String.join(", ", valoresCSV.keySet()) + ") VALUES (" + "?, ".repeat(valoresCSV.size() - 1) + "?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                    int index = 1;
                    for (String value : valoresCSV.values()) {
                        insertStmt.setString(index++, value);
                    }
                    insertStmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Error al procesar entidad: " + tabla);
            registrarEnLog(errorLog, "Error en entidad " + tabla + " "+ headersList.getFirst()+ " "+ valoresCSV.get(headersList.getFirst()) + " - " + e.getMessage());
        }
    }

    
    private static void registrarEnLog(String filePath, String mensaje) {
        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.println(mensaje);
        } catch (IOException e) {
            System.err.println("❌ Error al escribir en el log: " + filePath);
            e.printStackTrace();
        }
    }
}
