package uk.ac.brighton.uni.ab607.mmorpg.server;

/**
 * This exception is raised when there is something wrong
 * with the action requested by game client
 *
 * Possible causes: wrong data format, invalid values
 *
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 *
 */
public class BadActionRequestException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -8834657670181897836L;

    /**
     * Constructs a bad action request exception with error message
     *
     * @param message
     *                  the error message
     */
    public BadActionRequestException(String message) {
        super(message);
    }
}
