package onestep.exception;

/**
 * Custom exception untuk membungkus SQLException agar pesan ramah di GUI.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
