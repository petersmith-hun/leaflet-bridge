package hu.psprog.leaflet.bridge.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import hu.psprog.leaflet.api.rest.request.dcp.DCPRequestModel;
import hu.psprog.leaflet.api.rest.response.dcp.DCPDataModel;
import hu.psprog.leaflet.api.rest.response.dcp.DCPListDataModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.it.config.LeafletBridgeITContextConfig;
import hu.psprog.leaflet.bridge.service.DCPStoreBridgeService;
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
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Integration tests for {@link DCPStoreBridgeServiceImpl}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LeafletBridgeITContextConfig.class)
@ActiveProfiles(LeafletBridgeITContextConfig.INTEGRATION_TEST_CONFIG_PROFILE)
public class DCPStoreBridgeServiceImplIT extends WireMockBaseTest {

    @Autowired
    private DCPStoreBridgeService dcpStoreBridgeService;

    @Test
    public void shouldGetAllDCPEntries() throws CommunicationFailureException {

        // given
        DCPListDataModel dcpListDataModel = prepareDCPListDataModel();
        givenThat(get(Path.DCP.getURI())
                .willReturn(ResponseDefinitionBuilder.okForJson(dcpListDataModel)));

        // when
        DCPListDataModel result = dcpStoreBridgeService.getAllDCPEntries();

        // then
        assertThat(result, equalTo(dcpListDataModel));
        verify(getRequestedFor(urlEqualTo(Path.DCP.getURI()))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldCreateDCPEntry() throws CommunicationFailureException, JsonProcessingException {

        // given
        DCPRequestModel dcpRequestModel = prepareDCPRequestModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(dcpRequestModel));
        givenThat(post(Path.DCP.getURI())
                .withRequestBody(requestBody));

        // when
        dcpStoreBridgeService.createDCPEntry(dcpRequestModel);

        // then
        verify(postRequestedFor(urlEqualTo(Path.DCP.getURI()))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldUpdateDCPEntry() throws CommunicationFailureException, JsonProcessingException {

        // given
        DCPRequestModel dcpRequestModel = prepareDCPRequestModel();
        StringValuePattern requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(dcpRequestModel));
        givenThat(put(Path.DCP.getURI())
                .withRequestBody(requestBody));

        // when
        dcpStoreBridgeService.updateDCPEntry(dcpRequestModel);

        // then
        verify(putRequestedFor(urlEqualTo(Path.DCP.getURI()))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldDeleteDCPEntry() throws CommunicationFailureException, JsonProcessingException {

        // given
        String dcpKey = "dcp";
        String uri = prepareURI(Path.DCP_BY_KEY.getURI(), dcpKey);
        givenThat(delete(uri));

        // when
        dcpStoreBridgeService.removeDCPEntry(dcpKey);

        // then
        verify(deleteRequestedFor(urlEqualTo(uri))
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    private DCPRequestModel prepareDCPRequestModel() {
        DCPRequestModel dcpRequestModel = new DCPRequestModel();
        dcpRequestModel.setKey("DCP key");
        dcpRequestModel.setValue("DCP value");

        return dcpRequestModel;
    }

    private DCPListDataModel prepareDCPListDataModel() {
        return DCPListDataModel.getBuilder()
                .withItem(prepareDCPDataModel(1L))
                .withItem(prepareDCPDataModel(2L))
                .build();
    }

    private DCPDataModel prepareDCPDataModel(Long dcpID) {
        return DCPDataModel.getBuilder()
                .withKey("DCP key #" + dcpID.toString())
                .withValue("DCP value #" + dcpID.toString())
                .build();
    }
}
