package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.api.rest.request.dcp.DCPRequestModel;
import hu.psprog.leaflet.api.rest.response.dcp.DCPListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

/**
 * BridgeClient interface for DCP Store related calls.
 *
 * @author Peter Smith
 */
public interface DCPStoreBridgeService {

    /**
     * Lists all existing DCP Store entries.
     *
     * @return list of existing DCP entries
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    DCPListDataModel getAllDCPEntries() throws CommunicationFailureException;

    /**
     * Creates a new DCP Store entry.
     *
     * @param dcpRequestModel {@link DCPRequestModel} object holding entry data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void createDCPEntry(DCPRequestModel dcpRequestModel) throws CommunicationFailureException;

    /**
     * Updates an existing DCP Store entry.
     *
     * @param dcpRequestModel {@link DCPRequestModel} object holding entry data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void updateDCPEntry(DCPRequestModel dcpRequestModel) throws CommunicationFailureException;

    /**
     * Deletes an existing DCP Store entry.
     *
     * @param dcpKey key of DCP entry to remove
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void removeDCPEntry(String dcpKey) throws CommunicationFailureException;
}
