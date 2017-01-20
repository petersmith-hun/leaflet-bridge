package hu.psprog.leaflet.bridge.client.exception;

/**
 * Wrapper exception for communication errors.
 *
 * @author Peter Smith
 */
public class CommunicationFailureException extends Exception {

    private static final String BRIDGE_COMMUNICATION_FAILURE = "Bridge communication failure";

    public CommunicationFailureException(String message) {
        super(message);
    }

    public CommunicationFailureException(Throwable cause) {
        super(BRIDGE_COMMUNICATION_FAILURE, cause);
    }
}
