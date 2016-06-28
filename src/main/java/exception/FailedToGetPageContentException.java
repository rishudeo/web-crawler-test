package exception;

import java.io.IOException;

/**
 * Exception to indicate that the page content could not be retrieved.
 *
 * @author Ravindra Rishudeo.
 */
public class FailedToGetPageContentException extends Exception {

    /**
     * Constructor.
     *
     * @param message exception message
     * @param throwable cause
     */
    public FailedToGetPageContentException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructor.
     *
     * @param message exception message
     */
    public FailedToGetPageContentException(String message) {
        super(message);
    }
}
