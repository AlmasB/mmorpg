package uk.ac.brighton.uni.ab607.mmorpg.server;

public class BadActionRequestException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8834657670181897836L;

    public BadActionRequestException(String message) {
        super(message);
    }
}
