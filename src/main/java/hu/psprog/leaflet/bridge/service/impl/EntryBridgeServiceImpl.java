package hu.psprog.leaflet.bridge.service.impl;

import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.service.EntryBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Peter Smith
 */
@Component
public class EntryBridgeServiceImpl implements EntryBridgeService {

    @Autowired
    private BridgeClient bridgeClient;

    @Override
    public Map<String, Object> getAllEntries() throws CommunicationFailureException {

        RESTRequest request = new RESTRequest.Builder()
                .method(RequestMethod.GET)
                .path(Path.ENTRIES)
                .authenticated()
                .build();

        return bridgeClient.call(request);
    }
}
