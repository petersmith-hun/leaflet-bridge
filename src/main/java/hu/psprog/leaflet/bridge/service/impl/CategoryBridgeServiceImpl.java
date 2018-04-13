package hu.psprog.leaflet.bridge.service.impl;

import hu.psprog.leaflet.api.rest.request.category.CategoryCreateRequestModel;
import hu.psprog.leaflet.api.rest.response.category.CategoryDataModel;
import hu.psprog.leaflet.api.rest.response.category.CategoryListDataModel;
import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeService;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.client.request.RESTRequest;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.service.CategoryBridgeService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link CategoryBridgeService}.
 *
 * @author Peter Smith
 */
@BridgeService(client = "leaflet")
class CategoryBridgeServiceImpl implements CategoryBridgeService {

    private static final String ID = "id";

    private BridgeClient bridgeClient;

    @Autowired
    public CategoryBridgeServiceImpl(BridgeClient bridgeClient) {
        this.bridgeClient = bridgeClient;
    }

    @Override
    public CategoryListDataModel getAllCategories() throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.CATEGORIES)
                .authenticated()
                .build();

        return bridgeClient.call(restRequest, CategoryListDataModel.class);
    }

    @Override
    public CategoryListDataModel getPublicCategories() throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.CATEGORIES_PUBLIC)
                .build();

        return bridgeClient.call(restRequest, CategoryListDataModel.class);
    }

    @Override
    public CategoryDataModel getCategory(Long categoryID) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.GET)
                .path(Path.CATEGORIES_BY_ID)
                .addPathParameter(ID, String.valueOf(categoryID))
                .authenticated()
                .build();

        return bridgeClient.call(restRequest, CategoryDataModel.class);
    }

    @Override
    public CategoryDataModel createCategory(CategoryCreateRequestModel categoryCreateRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.POST)
                .path(Path.CATEGORIES)
                .requestBody(categoryCreateRequestModel)
                .authenticated()
                .build();

        return bridgeClient.call(restRequest, CategoryDataModel.class);
    }

    @Override
    public CategoryDataModel updateCategory(Long categoryID, CategoryCreateRequestModel categoryCreateRequestModel) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.CATEGORIES_BY_ID)
                .addPathParameter(ID, String.valueOf(categoryID))
                .requestBody(categoryCreateRequestModel)
                .authenticated()
                .build();

        return bridgeClient.call(restRequest, CategoryDataModel.class);
    }

    @Override
    public CategoryDataModel changeStatus(Long categoryID) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.PUT)
                .path(Path.CATEGORIES_STATUS)
                .addPathParameter(ID, String.valueOf(categoryID))
                .authenticated()
                .build();

        return bridgeClient.call(restRequest, CategoryDataModel.class);
    }

    @Override
    public void deleteCategory(Long categoryID) throws CommunicationFailureException {

        RESTRequest restRequest = RESTRequest.getBuilder()
                .method(RequestMethod.DELETE)
                .path(Path.CATEGORIES_BY_ID)
                .addPathParameter(ID, String.valueOf(categoryID))
                .authenticated()
                .build();

        bridgeClient.call(restRequest);
    }
}
