package de.footballmanager.backend.exception;

public class TimeTableCreationStuckException extends RuntimeException {

    private static final long serialVersionUID = 5907308741637140168L;

    public TimeTableCreationStuckException() {
        super();
    }

    public TimeTableCreationStuckException(final String message, final Throwable cause,
            final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TimeTableCreationStuckException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TimeTableCreationStuckException(final String message) {
        super(message);
    }

    public TimeTableCreationStuckException(final Throwable cause) {
        super(cause);
    }

}
