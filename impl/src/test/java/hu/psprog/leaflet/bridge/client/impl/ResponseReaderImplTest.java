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
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ProtocolException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static hu.psprog.leaflet.bridge.client.domain.BridgeConstants.AUTH_TOKEN_HEADER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link ResponseReaderImpl}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class ResponseReaderImplTest {

    private static final String TOKEN_VALUE = "token";
    private static final ErrorMessageResponse ERROR_MESSAGE_RESPONSE = ErrorMessageResponse.getBuilder()
            .withMessage("error")
            .build();
    private static final ValidationErrorMessageListResponse VALIDATION_ERROR_MESSAGE_LIST_RESPONSE = ValidationErrorMessageListResponse.getBuilder()
            .withValidation(Collections.singletonList(ValidationErrorMessageResponse.getBuilder()
                    .withField("field1")
                    .withMessage("constraint violation")
                    .build()))
            .build();

    @Mock
    private ClassicHttpResponse response;

    @Mock
    private HttpEntity entity;

    @Mock
    private InputStream inputStream;

    @Mock
    private RequestAdapter requestAdapter;

    @Mock
    private JsonMapper jsonMapper;

    @Mock
    private Header authHeader;

    @InjectMocks
    private ResponseReaderImpl responseReader;

    private final TypeReference<BaseBodyDataModel> genericType = new TypeReference<>() {};
    private final BaseBodyDataModel baseBodyDataModel = EntryDataModel.getBuilder().build();

    @Test
    public void shouldReadEntityForGivenType() throws IOException, ProtocolException {

        // given
        given(response.getCode()).willReturn(HttpStatus.SC_OK);
        given(response.getEntity()).willReturn(entity);
        given(response.getHeader(AUTH_TOKEN_HEADER)).willReturn(authHeader);
        given(authHeader.getValue()).willReturn(TOKEN_VALUE);
        given(entity.getContent()).willReturn(inputStream);
        given(jsonMapper.readValue(inputStream, genericType)).willReturn(baseBodyDataModel);

        // when
        BaseBodyDataModel result = responseReader.read(response, genericType);

        // then
        assertThat(result, equalTo(baseBodyDataModel));
        verify(response).close();
        verify(requestAdapter).consumeAuthenticationToken(TOKEN_VALUE);
    }

    @Test
    public void shouldReadEntityWithoutContent() throws ProtocolException, IOException {

        // given
        given(response.getCode()).willReturn(HttpStatus.SC_OK);
        given(response.getHeader(AUTH_TOKEN_HEADER)).willReturn(authHeader);
        given(authHeader.getValue()).willReturn(TOKEN_VALUE);

        // when
        responseReader.read(response);

        // then
        verify(requestAdapter).consumeAuthenticationToken(TOKEN_VALUE);
        verify(response).close();
        verifyNoInteractions(entity, inputStream, jsonMapper);
    }

    @Test
    public void shouldNotSetToken() throws IOException, ProtocolException {

        // given
        given(response.getCode()).willReturn(HttpStatus.SC_OK);
        given(response.getHeader(AUTH_TOKEN_HEADER)).willReturn(null);

        // when
        responseReader.read(response);

        // then
        verify(response).close();
        verifyNoInteractions(entity, inputStream, jsonMapper, requestAdapter, authHeader);
    }

    @Test
    public void shouldThrowValidationFailureExceptionWhenReadingForGivenType() throws IOException {

        // given
        given(response.getCode()).willReturn(400);
        given(response.getEntity()).willReturn(entity);
        given(entity.getContentLength()).willReturn(128L);
        given(entity.getContent()).willReturn(inputStream);
        given(jsonMapper.readValue(inputStream, ValidationErrorMessageListResponse.class)).willReturn(VALIDATION_ERROR_MESSAGE_LIST_RESPONSE);

        // when
        var exception = assertThrows(ValidationFailureException.class, () -> responseReader.read(response, genericType));

        // then
        // expected exception
        assertThat(exception.getMessage(), equalTo("Validation failure."));
        assertThat(exception.getErrorMessage(), equalTo(VALIDATION_ERROR_MESSAGE_LIST_RESPONSE));

        verify(response).close();
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenReadingForGivenType() throws IOException {

        // given
        given(response.getCode()).willReturn(404);
        given(response.getEntity()).willReturn(entity);
        given(entity.getContentLength()).willReturn(128L);
        given(entity.getContent()).willReturn(inputStream);
        given(jsonMapper.readValue(inputStream, ErrorMessageResponse.class)).willReturn(ERROR_MESSAGE_RESPONSE);

        // when
        var exception = assertThrows(ResourceNotFoundException.class, () -> responseReader.read(response, genericType));

        // then
        // expected exception
        assertThat(exception.getMessage(), equalTo(ERROR_MESSAGE_RESPONSE.message()));

        verify(response).close();
    }

    @Test
    public void shouldThrowRequestProcessingFailureExceptionWhenReadingForGivenType() throws IOException {

        // given
        given(response.getCode()).willReturn(500);
        given(response.getEntity()).willReturn(entity);
        given(entity.getContentLength()).willReturn(128L);
        given(entity.getContent()).willReturn(inputStream);
        given(jsonMapper.readValue(inputStream, ErrorMessageResponse.class)).willReturn(ERROR_MESSAGE_RESPONSE);

        // when
        var exception = assertThrows(RequestProcessingFailureException.class, () -> responseReader.read(response, genericType));

        // then
        // expected exception
        assertThat(exception.getMessage(), equalTo(ERROR_MESSAGE_RESPONSE.message()));

        verify(response).close();
    }

    @Test
    public void shouldThrowValidationFailureExceptionWhenReadingWithoutContent() throws IOException {

        // given
        given(response.getCode()).willReturn(400);
        given(response.getEntity()).willReturn(entity);
        given(entity.getContentLength()).willReturn(128L);
        given(entity.getContent()).willReturn(inputStream);
        given(jsonMapper.readValue(inputStream, ValidationErrorMessageListResponse.class)).willReturn(VALIDATION_ERROR_MESSAGE_LIST_RESPONSE);

        // when
        var exception = assertThrows(ValidationFailureException.class, () -> responseReader.read(response));

        // then
        // expected exception
        assertThat(exception.getMessage(), equalTo("Validation failure."));
        assertThat(exception.getErrorMessage(), equalTo(VALIDATION_ERROR_MESSAGE_LIST_RESPONSE));

        verify(response).close();
    }

    @Test
    public void shouldThrowResourceNotFoundExceptionWhenReadingWithoutContent() throws IOException {

        // given
        given(response.getCode()).willReturn(404);
        given(response.getEntity()).willReturn(null);

        // when
        var exception = assertThrows(ResourceNotFoundException.class, () -> responseReader.read(response));

        // then
        // expected exception
        assertThat(exception.getMessage(), equalTo("Unknown error occurred."));

        verify(response).close();
    }

    @Test
    public void shouldThrowRequestProcessingFailureExceptionWhenReadingWithoutContent() throws IOException {

        // given
        given(response.getCode()).willReturn(500);
        given(response.getEntity()).willReturn(entity);
        given(entity.getContentLength()).willReturn(0L);

        // when
        var exception = assertThrows(RequestProcessingFailureException.class, () -> responseReader.read(response));

        // then
        // expected exception
        assertThat(exception.getMessage(), equalTo("Unknown error occurred."));

        verify(response).close();
    }

    @Test
    public void shouldThrowUnauthorizedAccessExceptionWhenReadingWithoutContent() throws IOException {

        // given
        given(response.getCode()).willReturn(401);
        given(response.getEntity()).willReturn(null);

        // when
        var exception = assertThrows(UnauthorizedAccessException.class, () -> responseReader.read(response));

        // then
        // expected exception
        assertThat(exception.getMessage(), equalTo("Unknown error occurred."));

        verify(response).close();
    }

    @Test
    public void shouldThrowForbiddenOperationExceptionWhenReadingWithoutContent() throws IOException {

        // given
        given(response.getCode()).willReturn(403);
        given(response.getEntity()).willReturn(null);

        // when
        var exception = assertThrows(ForbiddenOperationException.class, () -> responseReader.read(response));

        // then
        // expected exception
        assertThat(exception.getMessage(), equalTo("Unknown error occurred."));

        verify(response).close();
    }

    @Test
    public void shouldThrowConflictingRequestExceptionWhenReadingWithoutContent() throws IOException {

        // given
        given(response.getCode()).willReturn(409);
        given(response.getEntity()).willReturn(null);

        // when
        var exception = assertThrows(ConflictingRequestException.class, () -> responseReader.read(response));

        // then
        // expected exception
        assertThat(exception.getMessage(), equalTo("Unknown error occurred."));

        verify(response).close();
    }

    @Test
    public void shouldThrowRequestProcessingFailureExceptionWhenExceptionOccursWhileReadingErrorResponse() throws IOException {

        // given
        given(response.getCode()).willReturn(500);
        given(response.getEntity()).willReturn(entity);
        given(entity.getContentLength()).willReturn(128L);
        given(entity.getContent()).willThrow(new IOException("End of stream"));

        // when
        var exception = assertThrows(RequestProcessingFailureException.class, () -> responseReader.read(response));

        // then
        // expected exception
        assertThat(exception.getMessage(), equalTo("End of stream"));

        verify(response).close();
    }

    @Test
    public void shouldThrowRequestProcessingFailureExceptionWhenExceptionOccursWhileReadingResponseOnTypeEntryPoint() throws IOException {

        // given
        given(response.getCode()).willThrow(new RuntimeException("Something went wrong"));

        // when
        var exception = assertThrows(RequestProcessingFailureException.class, () -> responseReader.read(response, genericType));

        // then
        // expected exception
        assertThat(exception.getMessage(), equalTo("Something went wrong"));

        verify(response).close();
    }

    @Test
    public void shouldThrowRequestProcessingFailureExceptionWhenExceptionOccursWhileReadingResponseOnVoidEntryPoint() throws IOException {

        // given
        given(response.getCode()).willThrow(new RuntimeException("Something went wrong"));

        // when
        var exception = assertThrows(RequestProcessingFailureException.class, () -> responseReader.read(response));

        // then
        // expected exception
        assertThat(exception.getMessage(), equalTo("Something went wrong"));

        verify(response).close();
    }
}
