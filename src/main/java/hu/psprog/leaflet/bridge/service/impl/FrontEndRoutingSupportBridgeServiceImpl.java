package hu.psprog.leaflet.bridge.service.impl;

import hu.psprog.leaflet.api.rest.request.routing.FrontEndRouteUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.routing.ExtendedFrontEndRouteDataModel;
import hu.psprog.leaflet.api.rest.response.routing.ExtendedFrontEndRouteListDataModel;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeService;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.service.FrontEndRoutingSupportBridgeService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link FrontEndRoutingSupportBridgeService}.
 *
 * @author Peter Smith
 */
@BridgeService(client = "leaflet")
public class FrontEndRoutingSupportBridgeServiceImpl implements FrontEndRoutingSupportBridgeService {

    private static final String ID = "id";

    private BridgeClient bridgeClient;

    @Autowired
    public FrontEndRoutingSupportBridgeServiceImpl(BridgeClient bridgeClient) {
        this.bridgeClient = bridgeClient;
    }

    @Override
    public ExtendedFrontEndRouteListDataModel getAllRoutes() throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.ROUTES)
                .authenticated()
                .build();

        return bridgeClient.call(restRequest, ExtendedFrontEndRouteListDataModel.class);
    }

    @Override
    public ExtendedFrontEndRouteDataModel getRouteByID(Long routeID) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.ROUTES_BY_ID)
                .authenticated()
                .addPathParameter(ID, String.valueOf(routeID))
                .build();

        return bridgeClient.call(restRequest, ExtendedFrontEndRouteDataModel.class);
    }

    @Override
    public ExtendedFrontEndRouteDataModel createRoute(FrontEndRouteUpdateRequestModel frontEndRouteUpdateRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.ROUTES)
                .authenticated()
                .requestBody(frontEndRouteUpdateRequestModel)
                .build();

        return bridgeClient.call(restRequest, ExtendedFrontEndRouteDataModel.class);
    }

    @Override
    public ExtendedFrontEndRouteDataModel updateRoute(Long routeID, FrontEndRouteUpdateRequestModel frontEndRouteUpdateRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.ROUTES_BY_ID)
                .authenticated()
                .addPathParameter(ID, String.valueOf(routeID))
                .requestBody(frontEndRouteUpdateRequestModel)
                .build();

        return bridgeClient.call(restRequest, ExtendedFrontEndRouteDataModel.class);
    }

    @Override
    public ExtendedFrontEndRouteDataModel changeStatus(Long routeID) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.ROUTES_STATUS)
                .authenticated()
                .addPathParameter(ID, String.valueOf(routeID))
                .build();

        return bridgeClient.call(restRequest, ExtendedFrontEndRouteDataModel.class);
    }

    @Override
    public void deleteRoute(Long routeID) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.DELETE)
                .path(Path.ROUTES_BY_ID)
                .authenticated()
                .addPathParameter(ID, String.valueOf(routeID))
                .build();

        bridgeClient.call(restRequest);
    }
}
