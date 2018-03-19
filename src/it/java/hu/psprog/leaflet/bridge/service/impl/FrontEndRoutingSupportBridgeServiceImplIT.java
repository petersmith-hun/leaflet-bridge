package hu.psprog.leaflet.bridge.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import hu.psprog.leaflet.api.rest.request.routing.FrontEndRouteUpdateRequestModel;
import hu.psprog.leaflet.api.rest.response.routing.ExtendedFrontEndRouteDataModel;
import hu.psprog.leaflet.api.rest.response.routing.ExtendedFrontEndRouteListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.it.config.LeafletBridgeITContextConfig;
import hu.psprog.leaflet.bridge.service.FrontEndRoutingSupportBridgeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for {@link FrontEndRoutingSupportBridgeServiceImpl}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LeafletBridgeITContextConfig.class)
@ActiveProfiles(LeafletBridgeITContextConfig.INTEGRATION_TEST_CONFIG_PROFILE)
public class FrontEndRoutingSupportBridgeServiceImplIT extends WireMockBaseTest {

    private static final long ROUTE_ID = 1L;
    private static final FrontEndRouteUpdateRequestModel FRONT_END_ROUTE_UPDATE_REQUEST_MODEL = new FrontEndRouteUpdateRequestModel();
    private static final String ROUTE_ID_STRING = "route-id";

    static {
        FRONT_END_ROUTE_UPDATE_REQUEST_MODEL.setRouteId(ROUTE_ID_STRING);
    }

    @Autowired
    private FrontEndRoutingSupportBridgeService frontEndRoutingSupportBridgeService;

    @Test
    public void shouldGetAllRoutes() throws CommunicationFailureException {

        // given
        ExtendedFrontEndRouteListDataModel listDataModel = prepareExtendedFrontEndRouteListDataModel();
        givenThat(get(Path.ROUTES.getURI())
                .willReturn(ResponseDefinitionBuilder.okForJson(listDataModel)));

        // when
        ExtendedFrontEndRouteListDataModel result = frontEndRoutingSupportBridgeService.getAllRoutes();

        // then
        assertThat(result, equalTo(listDataModel));
        verify(getRequestedFor(urlEqualTo(Path.ROUTES.getURI()))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldGetRouteByID() throws CommunicationFailureException {

        // given
        ExtendedFrontEndRouteDataModel dataModel = prepareExtendedFrontEndRouteDataModel();
        String path = prepareURI(Path.ROUTES_BY_ID.getURI(), ROUTE_ID);
        givenThat(get(urlPathEqualTo(path))
                .willReturn(ResponseDefinitionBuilder.okForJson(dataModel)));

        // when
        ExtendedFrontEndRouteDataModel result = frontEndRoutingSupportBridgeService.getRouteByID(ROUTE_ID);

        // then
        assertThat(result, equalTo(dataModel));
        verify(getRequestedFor(urlPathEqualTo(path))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldCreateRoute() throws JsonProcessingException, CommunicationFailureException {

        // given
        ExtendedFrontEndRouteDataModel dataModel = prepareExtendedFrontEndRouteDataModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(FRONT_END_ROUTE_UPDATE_REQUEST_MODEL));
        givenThat(post(Path.ROUTES.getURI())
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.okForJson(dataModel)));

        // when
        ExtendedFrontEndRouteDataModel result = frontEndRoutingSupportBridgeService.createRoute(FRONT_END_ROUTE_UPDATE_REQUEST_MODEL);

        // then
        assertThat(result, equalTo(dataModel));
        verify(postRequestedFor(urlEqualTo(Path.ROUTES.getURI()))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldUpdateRoute() throws JsonProcessingException, CommunicationFailureException {

        // given
        ExtendedFrontEndRouteDataModel dataModel = prepareExtendedFrontEndRouteDataModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(FRONT_END_ROUTE_UPDATE_REQUEST_MODEL));
        String path = prepareURI(Path.ROUTES_BY_ID.getURI(), ROUTE_ID);
        givenThat(put(urlPathEqualTo(path))
                .withRequestBody(requestBody)
                .willReturn(ResponseDefinitionBuilder.okForJson(dataModel)));

        // when
        ExtendedFrontEndRouteDataModel result = frontEndRoutingSupportBridgeService.updateRoute(ROUTE_ID, FRONT_END_ROUTE_UPDATE_REQUEST_MODEL);

        // then
        assertThat(result, equalTo(dataModel));
        verify(putRequestedFor(urlEqualTo(path))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldChangeStatus() throws CommunicationFailureException {

        // given
        ExtendedFrontEndRouteDataModel dataModel = prepareExtendedFrontEndRouteDataModel();
        String path = prepareURI(Path.ROUTES_STATUS.getURI(), ROUTE_ID);
        givenThat(put(urlPathEqualTo(path))
                .willReturn(ResponseDefinitionBuilder.okForJson(dataModel)));

        // when
        ExtendedFrontEndRouteDataModel result = frontEndRoutingSupportBridgeService.changeStatus(ROUTE_ID);

        // then
        assertThat(result, equalTo(dataModel));
        verify(putRequestedFor(urlPathEqualTo(path))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldDeleteRoute() throws CommunicationFailureException {

        // given
        String path = prepareURI(Path.ROUTES_BY_ID.getURI(), ROUTE_ID);
        givenThat(delete(urlPathEqualTo(path)));

        // when
        frontEndRoutingSupportBridgeService.deleteRoute(ROUTE_ID);

        // then
        verify(deleteRequestedFor(urlPathEqualTo(path))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    private ExtendedFrontEndRouteListDataModel prepareExtendedFrontEndRouteListDataModel() {
        return ExtendedFrontEndRouteListDataModel.getExtendedBuilder()
                .withItem(prepareExtendedFrontEndRouteDataModel())
                .build();
    }

    private ExtendedFrontEndRouteDataModel prepareExtendedFrontEndRouteDataModel() {
        return ExtendedFrontEndRouteDataModel.getExtendedBuilder()
                .withRouteId(ROUTE_ID_STRING)
                .build();
    }
}