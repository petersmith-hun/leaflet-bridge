package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

import java.util.Map;

/**
 * @author Peter Smith
 */
public interface EntryBridgeService {

    Map<String, Object> getAllEntries() throws CommunicationFailureException;
}
