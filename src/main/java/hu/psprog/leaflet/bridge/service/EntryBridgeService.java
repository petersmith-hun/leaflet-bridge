package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntryListDataModel;
import hu.psprog.leaflet.api.rest.response.entry.ExtendedEntryDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

/**
 * @author Peter Smith
 */
public interface EntryBridgeService {

    EntryListDataModel getAllEntries() throws CommunicationFailureException;

    WrapperBodyDataModel<ExtendedEntryDataModel> getEntryByLink(String link) throws CommunicationFailureException;
}
