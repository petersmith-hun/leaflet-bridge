package hu.psprog.leaflet.bridge.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import hu.psprog.leaflet.api.rest.request.attachment.AttachmentRequestModel;
import hu.psprog.leaflet.bridge.client.exception.CommunicationFailureException;
import hu.psprog.leaflet.bridge.client.request.Path;
import hu.psprog.leaflet.bridge.it.config.LeafletBridgeITContextConfig;
import hu.psprog.leaflet.bridge.service.AttachmentBridgeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.givenThat;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

/**
 * Integration tests for {@link AttachmentBridgeServiceImpl}.
 *
 * @author Peter Smith
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = LeafletBridgeITContextConfig.class)
@ActiveProfiles(LeafletBridgeITContextConfig.INTEGRATION_TEST_CONFIG_PROFILE)
public class AttachmentBridgeServiceImplIT extends WireMockBaseTest {

    @Autowired
    private AttachmentBridgeService attachmentBridgeService;

    private StringValuePattern requestBody;
    private AttachmentRequestModel attachmentRequestModel;

    @Before
    public void setup() throws JsonProcessingException {
        attachmentRequestModel = new AttachmentRequestModel();
        attachmentRequestModel.setEntryID(10L);
        attachmentRequestModel.setPathUUID(UUID.nameUUIDFromBytes("uuid".getBytes()));
        requestBody = equalToJson(OBJECT_MAPPER.writeValueAsString(attachmentRequestModel));
    }

    @Test
    public void shouldAttach() throws CommunicationFailureException, JsonProcessingException {

        // given
        givenThat(post(Path.ATTACHMENTS.getURI())
                .withRequestBody(requestBody)
                .willReturn(aResponse().withStatus(201)));

        // when
        attachmentBridgeService.attach(attachmentRequestModel);

        // then
        verify(postRequestedFor(urlEqualTo(Path.ATTACHMENTS.getURI()))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }

    @Test
    public void shouldDetach() throws CommunicationFailureException, JsonProcessingException {

        // given
        givenThat(put(Path.ATTACHMENTS.getURI())
                .withRequestBody(requestBody)
                .willReturn(aResponse().withStatus(204)));

        // when
        attachmentBridgeService.detach(attachmentRequestModel);

        // then
        verify(putRequestedFor(urlEqualTo(Path.ATTACHMENTS.getURI()))
                .withRequestBody(requestBody)
                .withHeader(AUTHORIZATION_HEADER, VALUE_PATTERN_BEARER_TOKEN));
    }
}
