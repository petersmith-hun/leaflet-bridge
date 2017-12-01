package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.api.rest.request.document.DocumentCreateRequestModel;
import hu.psprog.leaflet.api.rest.request.document.DocumentUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.common.WrapperBodyDataModel;
import hu.psprog.leaflet.api.rest.response.document.DocumentDataModel;
import hu.psprog.leaflet.api.rest.response.document.DocumentListDataModel;
import hu.psprog.leaflet.api.rest.response.document.EditDocumentDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

/**
 * BridgeClient interface for document related calls.
 *
 * @author Peter Smith
 */
public interface DocumentBridgeService {

    /**
     * Returns basic information of all existing document.
     *
     * @return list of existing documents
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    DocumentListDataModel getAllDocuments() throws CommunicationFailureException;

    /**
     * Returns basic information of existing public documents.
     *
     * @return list of public documents
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    DocumentListDataModel getPublicDocuments() throws CommunicationFailureException;

    /**
     * Returns detailed information of document identified by given ID (for admin usage).
     *
     * @param documentID ID of an existing document
     * @return identified document
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    WrapperBodyDataModel<EditDocumentDataModel> getDocumentByID(Long documentID) throws CommunicationFailureException;

    /**
     * Returns details detailed information of document identified by given link (for visitors).
     *
     * @param link link to identify document
     * @return identified document
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    WrapperBodyDataModel<DocumentDataModel> getDocumentByLink(String link) throws CommunicationFailureException;

    /**
     * Creates a new document.
     *
     * @param documentCreateRequestModel document data
     * @return created document data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    EditDocumentDataModel createDocument(DocumentCreateRequestModel documentCreateRequestModel) throws CommunicationFailureException;

    /**
     * Updates an existing document.
     *
     * @param documentID ID of an existing document
     * @param documentUpdateRequestModel document data
     * @return updated document data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    EditDocumentDataModel updateDocument(Long documentID, DocumentUpdateRequestModel documentUpdateRequestModel) throws CommunicationFailureException;

    /**
     * Changes status of an existing document.
     *
     * @param documentID ID of the document to update status of
     * @return updated document data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    EditDocumentDataModel changeStatus(Long documentID) throws CommunicationFailureException;

    /**
     * Deletes an existing document.
     *
     * @param documentID ID of an existing document
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void deleteDocument(Long documentID) throws CommunicationFailureException;
}
