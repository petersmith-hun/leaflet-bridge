package hu.psprog.leaflet.bridge.service.impl;

import hu.psprog.leaflet.api.rest.request.dcp.DCPRequestModel;
import hu.psprog.leaflet.api.rest.response.dcp.DCPListDataModel;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeService;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.service.DCPStoreBridgeService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link DCPStoreBridgeService}.
 *
 * @author Peter Smith
 */
@BridgeService(client = "leaflet")
class DCPStoreBridgeServiceImpl implements DCPStoreBridgeService {

    private static final String KEY = "key";

    private BridgeClient bridgeClient;

    @Autowired
    public DCPStoreBridgeServiceImpl(BridgeClient bridgeClient) {
        this.bridgeClient = bridgeClient;
    }

    @Override
    public DCPListDataModel getAllDCPEntries() throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.DCP)
                .authenticated()
                .build();

        return bridgeClient.call(restRequest, DCPListDataModel.class);
    }

    @Override
    public void createDCPEntry(DCPRequestModel dcpRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.DCP)
                .requestBody(dcpRequestModel)
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }

    @Override
    public void updateDCPEntry(DCPRequestModel dcpRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.DCP)
                .requestBody(dcpRequestModel)
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }

    @Override
    public void removeDCPEntry(String dcpKey) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.DELETE)
                .path(Path.DCP_BY_KEY)
                .addPathParameter(KEY, dcpKey)
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }
}
