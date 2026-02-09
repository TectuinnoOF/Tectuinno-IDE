package org.tectuinno.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Recolección de todos los errores y advertencias ocurridas durante la ejecución del IDE.
 * Los mensajes se almacenan en un archivo de texto como bitácora de eventos.
 *
 * Formato:
 * [YYYY-MM-DD HH:mm:ss] > [TIPO] : Mensaje
 */
public class LoggerInfoManager {
	
	
	private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Path LOG_FILE = Paths.get(
            System.getProperty("user.home"),
            "Documents",
            "Tectuinno",
            "logs",
            "tectuinno.log"
    );
    
    private static synchronized void write(String type, String message, Throwable throwable) {

        try {
            Files.createDirectories(LOG_FILE.getParent());

            try (BufferedWriter writer = Files.newBufferedWriter(
                    LOG_FILE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            )) {

                String timestamp = LocalDateTime.now().format(DATE_FORMAT);

                writer.write("[" + timestamp + "] > [" + type + "] : " + message);
                writer.newLine();

                if (throwable != null) {
                    writer.write("    Exception: " + throwable.getClass().getName());
                    writer.newLine();
                    writer.write("    Message  : " + throwable.getMessage());
                    writer.newLine();

                    for (StackTraceElement ste : throwable.getStackTrace()) {
                        writer.write("        at " + ste.toString());
                        writer.newLine();
                    }
                }

                writer.newLine();
            }

        } catch (IOException e) {
            // Último recurso: no lanzar excepción, evitar bucles
            System.err.println("No se pudo escribir en el archivo de log:");
            e.printStackTrace(System.err);
        }
    }
    
    public static void writteInErrorLogTxtr(String message) {
        write("ERROR", message, null);
    }

    public static void writteInErrorLogTxtr(String message, Throwable throwable) {
        write("ERROR", message, throwable);
    }

    public static void writteInWarnLogTxt(String message) {
        write("WARN", message, null);
    }

    public static void writteInInfoLogTxt(String message) {
        write("INFO", message, null);
    }
    
}
