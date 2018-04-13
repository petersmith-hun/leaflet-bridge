package hu.psprog.leaflet.bridge.service.impl;

import hu.psprog.leaflet.api.rest.request.attachment.AttachmentRequestModel;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeService;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.service.AttachmentBridgeService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link AttachmentBridgeService}.
 *
 * @author Peter Smith
 */
@BridgeService(client = "leaflet")
class AttachmentBridgeServiceImpl implements AttachmentBridgeService {

    private BridgeClient bridgeClient;

    @Autowired
    public AttachmentBridgeServiceImpl(BridgeClient bridgeClient) {
        this.bridgeClient = bridgeClient;
    }

    @Override
    public void attach(AttachmentRequestModel attachmentRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.ATTACHMENTS)
                .requestBody(attachmentRequestModel)
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }

    @Override
    public void detach(AttachmentRequestModel attachmentRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.ATTACHMENTS)
                .requestBody(attachmentRequestModel)
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }
}
