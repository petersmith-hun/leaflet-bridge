package hu.psprog.leaflet.bridge.service.impl;

import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntryListDataModel;
import hu.psprog.leaflet.api.rest.response.entry.ExtendedEntryDataModel;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.model.WrappedResponseGenericType;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.service.EntryBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Peter Smith
 */
@Component
public class EntryBridgeServiceImpl implements EntryBridgeService {

    private BridgeClient bridgeClient;

    @Autowired
    public EntryBridgeServiceImpl(BridgeClient bridgeClient) {
        this.bridgeClient = bridgeClient;
    }

    @Override
    public EntryListDataModel getAllEntries() throws CommunicationFailureException {

        RESTRequest request = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.ENTRIES)
                .authenticated()
                .build();

        return bridgeClient
                .call(request, EntryListDataModel.class);
    }

    @Override
    public WrapperBodyDataModel<ExtendedEntryDataModel> getEntryByLink(String link) throws CommunicationFailureException {

        RESTRequest request = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.ENTRY_BY_LINK)
                .addPathParameter("link", link)
                .authenticated()
                .build();

        return bridgeClient
                .call(request, WrappedResponseGenericType.forType(ExtendedEntryDataModel.class));
    }
}
