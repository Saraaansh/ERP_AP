package edu.univ.erp.ui.util;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class LoggerUtil {
    private static final String LOG_FILE = "erp_log.txt"; // saved in project folder
    private static boolean writeToFile = true;

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Enable or disable file logging.
     */
    public static void enableFileLogging(boolean enable) {
        writeToFile = enable;
    }

    /**
     * Generic log method used internally.
     */
    private static synchronized void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        String logEntry = "[" + timestamp + "] [" + level + "] " + message;

        // Print to console
        System.out.println(logEntry);

        // Also write to log file
        if (writeToFile) {
            try (FileWriter writer = new FileWriter(LOG_FILE, true)) { // append = true
                writer.write(logEntry + "\n");
            } catch (IOException e) {
                System.err.println("LoggerUtil ERROR: Unable to write to log file.");
            }
        }
    }

    // ======== PUBLIC LOGGING METHODS ======== //

    public static void info(String msg) {
        log("INFO", msg);
    }

    public static void warn(String msg) {
        log("WARNING", msg);
    }

    public static void error(String msg) {
        log("ERROR", msg);
    }

    public static void debug(String msg) {
        log("DEBUG", msg);
    }
}
