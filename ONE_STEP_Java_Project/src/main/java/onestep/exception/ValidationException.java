package onestep.exception;

/**
 * Custom exception untuk kesalahan validasi input user.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
