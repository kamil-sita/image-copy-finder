package pl.ksitarski.icf.core.exc;

public class UnexpectedError extends Error {
    public UnexpectedError() {
        super();
    }

    public UnexpectedError(String message) {
        super(message);
    }

    public UnexpectedError(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedError(Throwable cause) {
        super(cause);
    }

    protected UnexpectedError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
