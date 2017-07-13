package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.api.rest.request.attachment.AttachmentRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

/**
 * Bridge Client interface for attachment related calls.
 *
 * @author Peter Smith
 */
public interface AttachmentBridgeService {

    /**
     * Attaches an existing file to an existing entry.
     *
     * @param attachmentRequestModel attachment request
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void attach(AttachmentRequestModel attachmentRequestModel) throws CommunicationFailureException;

    /**
     * Detaches an existing file from an existing entry.
     *
     * @param attachmentRequestModel attachment request
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void detach(AttachmentRequestModel attachmentRequestModel) throws CommunicationFailureException;
}
