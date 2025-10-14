package es.uvigo.esei.dai.hybridserver.model.dao;

/**
 * Exception class for handling errors related to Data Access Objects (DAOs).
 */
public class DAOException extends Exception {
    
    private static final long serialVersionUID = 1L;    // For serialization compatibility

    /**
     * Constructs a new instance of DAOException with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to {@link #initCause}.
     */
    public DAOException() {
        super();
    }

    /**
     * Constructs a new instance of DAOException with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to {@link #initCause}.
     * 
     * @param message the detail message. 
     * The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public DAOException(String message) {
        super(message);
    }

    /**
     * Constructs a new instance of DAOException with the specified cause.
     * The detail message is initialized to {@code null}.
     * 
     * @param cause the cause of the exception.
     */
    public DAOException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new instance of DAOException with the specified detail message and cause.
     * 
     * @param message the detail message. 
     * The detail message is saved for later retrieval by the {@link #getMessage()} method.
     * @param cause the cause of the exception.
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new instance of DAOException with the specified detail message, cause,
     * suppression enabled or disabled, and writable stack trace enabled or disabled.
     * 
     * @param message the detail message. 
     * The detail message is saved for later retrieval by the {@link #getMessage()} method.
     * @param cause the cause of the exception.
     * @param enableSuppression whether or not suppression is enabled or disabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    public DAOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
