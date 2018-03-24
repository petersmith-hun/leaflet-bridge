package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.api.rest.request.category.CategoryCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.category.CategoryDataModel;
import hu.psprog.leaflet.api.rest.response.category.CategoryListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

/**
 * BridgeClient interface for category related calls.
 *
 * @author Peter Smith
 */
public interface CategoryBridgeService {

    /**
     * Returns list of all existing categories.
     *
     * @return list of categories
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    CategoryListDataModel getAllCategories() throws CommunicationFailureException;

    /**
     * Returns list of public categories.
     *
     * @return list of public categories
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    CategoryListDataModel getPublicCategories() throws CommunicationFailureException;

    /**
     * Returns category identified by given ID.
     *
     * @param categoryID ID of an existing category
     * @return category data if requested category exists
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    CategoryDataModel getCategory(Long categoryID) throws CommunicationFailureException;

    /**
     * Creates a new category.
     *
     * @param categoryCreateRequestModel category data
     * @return created category data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    CategoryDataModel createCategory(CategoryCreateRequestModel categoryCreateRequestModel) throws CommunicationFailureException;

    /**
     * Updates an existing category.
     *
     * @param categoryID ID of an existing category
     * @param categoryCreateRequestModel category data
     * @return updated category data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    CategoryDataModel updateCategory(Long categoryID, CategoryCreateRequestModel categoryCreateRequestModel) throws CommunicationFailureException;

    /**
     * Changes status of an existing category.
     *
     * @param categoryID ID of an existing category
     * @return updated category data
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    CategoryDataModel changeStatus(Long categoryID) throws CommunicationFailureException;

    /**
     * Deletes an existing category.
     *
     * @param categoryID ID of an existing category
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void deleteCategory(Long categoryID) throws CommunicationFailureException;
}
