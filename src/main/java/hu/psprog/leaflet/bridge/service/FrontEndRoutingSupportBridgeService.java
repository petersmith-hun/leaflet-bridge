package hu.psprog.leaflet.bridge.service;

import hu.psprog.leaflet.api.rest.request.routing.FrontEndRouteUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.routing.ExtendedFrontEndRouteDataModel;
import hu.psprog.leaflet.api.rest.response.routing.ExtendedFrontEndRouteListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;

/**
 * BridgeClient interface for front end route support management operations.
 *
 * @author Peter Smith
 */
public interface FrontEndRoutingSupportBridgeService {

    /**
     * Returns all existing route items.
     *
     * @return list of existing route items
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    ExtendedFrontEndRouteListDataModel getAllRoutes() throws CommunicationFailureException;

    /**
     * Returns route item identified by given ID.
     *
     * @param routeID ID of the route item
     * @return identified route item
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    ExtendedFrontEndRouteDataModel getRouteByID(Long routeID) throws CommunicationFailureException;

    /**
     * Creates a new route item.
     *
     * @param frontEndRouteUpdateRequestModel {@link FrontEndRouteUpdateRequestModel} containing route information
     * @return data of created route item
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    ExtendedFrontEndRouteDataModel createRoute(FrontEndRouteUpdateRequestModel frontEndRouteUpdateRequestModel) throws CommunicationFailureException;

    /**
     * Updates an existing route item.
     *
     * @param routeID ID of the route item
     * @param frontEndRouteUpdateRequestModel {@link FrontEndRouteUpdateRequestModel} containing updated route information
     * @return updated data of route item
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    ExtendedFrontEndRouteDataModel updateRoute(Long routeID, FrontEndRouteUpdateRequestModel frontEndRouteUpdateRequestModel) throws CommunicationFailureException;

    /**
     * Changes route item status (enabled/disabled).
     *
     * @param routeID ID of the route item
     * @return updated data of route item
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    ExtendedFrontEndRouteDataModel changeStatus(Long routeID) throws CommunicationFailureException;

    /**
     * Deletes route item identified by given ID.
     *
     * @param routeID ID of the route item
     * @throws CommunicationFailureException if client fails to reach backend application
     */
    void deleteRoute(Long routeID) throws CommunicationFailureException;
}
