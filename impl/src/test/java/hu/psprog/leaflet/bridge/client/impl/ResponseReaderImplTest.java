package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.api.rest.response.common.BaseBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntryDataModel;
import hu.psprog.leaflet.bridge.client.domain.error.ErrorMessageResponse;
import hu.psprog.leaflet.bridge.client.domain.error.ValidationErrorMessageListResponse;
import hu.psprog.leaflet.bridge.client.domain.error.ValidationErrorMessageResponse;
import hu.psprog.leaflet.bridge.client.exception.ConflictingRequestException;
import hu.psprog.leaflet.bridge.client.exception.ForbiddenOperationException;
import hu.psprog.leaflet.bridge.client.exception.RequestProcessingFailureException;
import hu.psprog.leaflet.bridge.client.exception.ResourceNotFoundException;
import hu.psprog.leaflet.bridge.client.exception.UnauthorizedAccessException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import hu.psprog.leaflet.bridge.client.request.RequestAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.AUTH_TOKEN_HEADER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Unit tests for {@link ResponseReaderImpl}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class ResponseReaderImplTest {

    private static final String TOKEN_VALUE = "token";
    private static final ErrorMessageResponse ERROR_MESSAGE_RESPONSE = ErrorMessageResponse.getBuilder()
            .withMessage("error")
            .build();
    private static final ValidationErrorMessageListResponse VALIDATION_ERROR_MESSAGE_LIST_RESPONSE = ValidationErrorMessageListResponse.getBuilder()
            .withValidation(Collections.singletonList(ValidationErrorMessageResponse.getExtendedBuilder()
                    .withField("field1")
                    .withMessage("constraint violation")
                    .build()))
            .build();

    @Mock
    private Response response;

    @Mock
    private RequestAdapter requestAdapter;

    @InjectMocks
    private ResponseReaderImpl responseReader;

    private GenericType<BaseBodyDataModel> genericType = new GenericType<>() {};
    private BaseBodyDataModel baseBodyDataModel = EntryDataModel.getBuilder().build();

    @Before
    public void setup() {
        given(response.readEntity(genericType)).willReturn(baseBodyDataModel);
        given(response.getHeaderString(AUTH_TOKEN_HEADER)).willReturn(TOKEN_VALUE);
    }

    @Test
    public void shouldReadEntityForGivenType() {

        // given
        given(response.getStatusInfo()).willReturn(Response.Status.OK);

        // when
        BaseBodyDataModel result = responseReader.read(response, genericType);

        // then
        assertThat(result, equalTo(baseBodyDataModel));
        verify(response).readEntity(genericType);
        verify(response).close();
        verify(requestAdapter).consumeAuthenticationToken(TOKEN_VALUE);
    }

    @Test
    public void shouldReadEntityWithoutContent() {

        // given
        given(response.getStatusInfo()).willReturn(Response.Status.OK);

        // when
        responseReader.read(response);

        // then
        verify(requestAdapter).consumeAuthenticationToken(TOKEN_VALUE);
        verify(response).close();
    }

    @Test
    public void shouldNotSetToken() {

        // given
        given(response.getHeaderString(AUTH_TOKEN_HEADER)).willReturn(null);
        given(response.getStatusInfo()).willReturn(Response.Status.OK);

        // when
        responseReader.read(response);

        // then
        verifyZeroInteractions(requestAdapter);
        verify(response).close();
    }

    @Test(expected = ValidationFailureException.class)
    public void shouldThrowValidationFailureExceptionWhenReadingForGivenType() {

        // given
        given(response.getStatus()).willReturn(400);
        given(response.getStatusInfo()).willReturn(Response.Status.BAD_REQUEST);
        given(response.readEntity(ValidationErrorMessageListResponse.class)).willReturn(VALIDATION_ERROR_MESSAGE_LIST_RESPONSE);

        // when
        try {
            responseReader.read(response, genericType);
        } finally {

            // then
            // expected exception
            verify(response).readEntity(ValidationErrorMessageListResponse.class);
            verify(response).close();
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionWhenReadingForGivenType() {

        // given
        given(response.getStatus()).willReturn(404);
        given(response.getStatusInfo()).willReturn(Response.Status.NOT_FOUND);
        given(response.readEntity(ErrorMessageResponse.class)).willReturn(ERROR_MESSAGE_RESPONSE);

        // when
        try {
            responseReader.read(response, genericType);
        } finally {

            // then
            // expected exception
            verify(response).readEntity(ErrorMessageResponse.class);
            verify(response).close();
        }
    }

    @Test(expected = RequestProcessingFailureException.class)
    public void shouldThrowRequestProcessingFailureExceptionWhenReadingForGivenType() {

        // given
        given(response.getStatus()).willReturn(500);
        given(response.getStatusInfo()).willReturn(Response.Status.INTERNAL_SERVER_ERROR);
        given(response.readEntity(ErrorMessageResponse.class)).willReturn(ERROR_MESSAGE_RESPONSE);

        // when
        try {
            responseReader.read(response, genericType);
        } finally {

            // then
            // expected exception
            verify(response).readEntity(ErrorMessageResponse.class);
            verify(response).close();
        }
    }

    @Test(expected = ValidationFailureException.class)
    public void shouldThrowValidationFailureExceptionWhenReadingWithoutContent() {

        // given
        given(response.getStatus()).willReturn(400);
        given(response.getStatusInfo()).willReturn(Response.Status.BAD_REQUEST);
        given(response.readEntity(ValidationErrorMessageListResponse.class)).willReturn(VALIDATION_ERROR_MESSAGE_LIST_RESPONSE);

        // when
        try {
            responseReader.read(response, genericType);
        } finally {

            // then
            // expected exception
            verify(response).readEntity(ValidationErrorMessageListResponse.class);
            verify(response).close();
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionWhenReadingWithoutContent() {

        // given
        given(response.getStatus()).willReturn(404);
        given(response.getStatusInfo()).willReturn(Response.Status.NOT_FOUND);
        given(response.readEntity(ErrorMessageResponse.class)).willReturn(ERROR_MESSAGE_RESPONSE);

        // when
        try {
            responseReader.read(response, genericType);
        } finally {

            // then
            // expected exception
            verify(response).readEntity(ErrorMessageResponse.class);
            verify(response).close();
        }
    }

    @Test(expected = RequestProcessingFailureException.class)
    public void shouldThrowRequestProcessingFailureExceptionWhenReadingWithoutContent() {

        // given
        given(response.getStatus()).willReturn(500);
        given(response.getStatusInfo()).willReturn(Response.Status.INTERNAL_SERVER_ERROR);
        given(response.readEntity(ErrorMessageResponse.class)).willReturn(ERROR_MESSAGE_RESPONSE);

        // when
        responseReader.read(response);

        // when
        try {
            responseReader.read(response, genericType);
        } finally {

            // then
            // expected exception
            verify(response).readEntity(ErrorMessageResponse.class);
            verify(response).close();
        }
    }

    @Test(expected = UnauthorizedAccessException.class)
    public void shouldThrowUnauthorizedAccessExceptionWhenReadingWithoutContent() {

        // given
        given(response.getStatus()).willReturn(401);
        given(response.getStatusInfo()).willReturn(Response.Status.INTERNAL_SERVER_ERROR);
        given(response.readEntity(ErrorMessageResponse.class)).willReturn(ERROR_MESSAGE_RESPONSE);

        // when
        responseReader.read(response);

        // when
        try {
            responseReader.read(response, genericType);
        } finally {

            // then
            // expected exception
            verify(response).readEntity(ErrorMessageResponse.class);
            verify(response).close();
        }
    }

    @Test(expected = ForbiddenOperationException.class)
    public void shouldThrowForbiddenOperationExceptionWhenReadingWithoutContent() {

        // given
        given(response.getStatus()).willReturn(403);
        given(response.getStatusInfo()).willReturn(Response.Status.INTERNAL_SERVER_ERROR);
        given(response.readEntity(ErrorMessageResponse.class)).willReturn(ERROR_MESSAGE_RESPONSE);

        // when
        responseReader.read(response);

        // when
        try {
            responseReader.read(response, genericType);
        } finally {

            // then
            // expected exception
            verify(response).readEntity(ErrorMessageResponse.class);
            verify(response).close();
        }
    }

    @Test(expected = ConflictingRequestException.class)
    public void shouldThrowConflictingRequestExceptionWhenReadingWithoutContent() {

        // given
        given(response.getStatus()).willReturn(409);
        given(response.getStatusInfo()).willReturn(Response.Status.INTERNAL_SERVER_ERROR);
        given(response.readEntity(ErrorMessageResponse.class)).willReturn(ERROR_MESSAGE_RESPONSE);

        // when
        responseReader.read(response);

        // when
        try {
            responseReader.read(response, genericType);
        } finally {

            // then
            // expected exception
            verify(response).readEntity(ErrorMessageResponse.class);
            verify(response).close();
        }
    }
}
