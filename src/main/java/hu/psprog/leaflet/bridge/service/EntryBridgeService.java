package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.api.rest.request.entry.EntryCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.entry.EntryUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EditEntryDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntryListDataModel;
import hu.psprog.leaflet.api.rest.response.entry.ExtendedEntryDataModel;
import hu.psprog.leaflet.bridge.client.domain.OrderBy;
import hu.psprog.leaflet.bridge.client.domain.OrderDirection;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

/**
 * Bridge Client interface for entry related calls.
 *
 * @author Peter Smith
 */
public interface EntryBridgeService {

    /**
     * Returns basic information of all existing entry.
     *
     * @return list of existing entries
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    EntryListDataModel getAllEntries() throws CommunicationFailureException;

    /**
     * Returns basic information of given page of public entries.
     *
     * @param page page number (page indexing starts at 1)
     * @param limit number of entries on one page
     * @param orderBy order by {@link OrderBy.Entry} options
     * @param orderDirection order direction (ASC|DESC)
     * @return page of public entries
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    WrapperBodyDataModel<EntryListDataModel> getPageOfPublicEntries(int page, int limit, OrderBy.Entry orderBy, OrderDirection orderDirection)
            throws CommunicationFailureException;

    /**
     * Returns basic information of given page of (public and non-public) entries.
     *
     * @param page page number (page indexing starts at 1)
     * @param limit number of entries on one page
     * @param orderBy order by {@link OrderBy.Entry} options
     * @param orderDirection order direction (ASC|DESC)
     * @return page of entries
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    WrapperBodyDataModel<EntryListDataModel> getPageOfEntries(int page, int limit, OrderBy.Entry orderBy, OrderDirection orderDirection)
            throws CommunicationFailureException;

    /**
     * Returns basic information of given page of public entries filtered by given category ID.
     *
     * @param categoryID category ID to filter by
     * @param page page number (page indexing starts at 1)
     * @param limit number of entries on one page
     * @param orderBy order by {@link OrderBy} options
     * @param orderDirection order direction (ASC|DESC)
     * @return page of public entries under given category
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    WrapperBodyDataModel<EntryListDataModel> getPageOfPublicEntriesByCategory(Long categoryID, int page, int limit, OrderBy.Entry orderBy, OrderDirection orderDirection)
            throws CommunicationFailureException;

    /**
     * Returns entry identified by given link.
     *
     * @param link link to identify entry
     * @return identified entry
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    WrapperBodyDataModel<ExtendedEntryDataModel> getEntryByLink(String link) throws CommunicationFailureException;

    /**
     * Returns entry identified by given ID.
     *
     * @param entryID ID of an existing entry
     * @return identified entry
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    WrapperBodyDataModel<EditEntryDataModel> getEntryByID(Long entryID) throws CommunicationFailureException;

    /**
     * Creates a new entry.
     *
     * @param entryCreateRequestModel entry data
     * @return created entry data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    EditEntryDataModel createEntry(EntryCreateRequestModel entryCreateRequestModel) throws CommunicationFailureException;

    /**
     * Updates an existing entry.
     *
     * @param entryID ID of an existing entry
     * @param entryUpdateRequestModel entry data
     * @return updated entry data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    EditEntryDataModel updateEntry(Long entryID, EntryUpdateRequestModel entryUpdateRequestModel) throws CommunicationFailureException;

    /**
     * Changes status of an existing entry.
     *
     * @param entryID ID of an existing entry
     * @return updated entry data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    EditEntryDataModel changeStatus(Long entryID) throws CommunicationFailureException;

    /**
     * Deletes an existing entry.
     *
     * @param entryID ID of an existing entry
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void deleteEntry(Long entryID) throws CommunicationFailureException;
}
