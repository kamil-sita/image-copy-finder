package pl.ksitarski.icf.core.exc;

public class WrapperException extends RuntimeException {
    public WrapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrapperException(Throwable cause) {
        super(cause);
    }
}
