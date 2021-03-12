package pl.ksitarski.icf.core.comparison.engine;

public class ExceptionsBehaviour {
    private boolean exceptionsStackTrace;
    private boolean includeErrors;


    public boolean isExceptionsStackTrace() {
        return exceptionsStackTrace;
    }

    public ExceptionsBehaviour setExceptionsStackTrace(boolean exceptionsStackTrace) {
        this.exceptionsStackTrace = exceptionsStackTrace;
        return this;
    }

    public boolean isIncludeErrors() {
        return includeErrors;
    }

    public ExceptionsBehaviour setIncludeErrors(boolean includeErrors) {
        this.includeErrors = includeErrors;
        return this;
    }
}
