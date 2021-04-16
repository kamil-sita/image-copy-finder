package pl.ksitarski.icf.core.prototype.exc;

public class AlgorithmFailureException extends RuntimeException {
    public AlgorithmFailureException() {
        super();
    }

    public AlgorithmFailureException(String message) {
        super(message);
    }

    public AlgorithmFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public AlgorithmFailureException(Throwable cause) {
        super(cause);
    }

    protected AlgorithmFailureException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
