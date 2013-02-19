package com.tcs.os.exception;

import org.jboss.seam.annotations.ApplicationException;

@ApplicationException(rollback = true)
public class RestServicesException extends Exception {

    private static final long serialVersionUID = 5037827424063864222L;

    private String errorMessage;

    private String errorNumber;

    public RestServicesException(final Throwable cause) {
        super(cause);
    }

    public RestServicesException() {
        super();
    }

    public RestServicesException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RestServicesException(final String message) {
        super(message);
    }

    public RestServicesException(final String message, final String errNumber) {
        super(message);
        this.errorNumber = errNumber;
    }

    public RestServicesException(final Exception exception) {
        super(exception.getMessage(), exception);
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getErrorNumber() {
        return errorNumber;
    }

    public void setErrorNumber(final String errorNumber) {
        this.errorNumber = errorNumber;
    }
}
