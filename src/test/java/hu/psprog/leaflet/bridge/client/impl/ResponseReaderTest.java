package hu.psprog.leaflet.bridge.client.impl;

import hu.psprog.leaflet.api.rest.response.common.BaseBodyDataModel;
import hu.psprog.leaflet.api.rest.response.entry.EntryDataModel;
import hu.psprog.leaflet.bridge.client.exception.RequestProcessingFailureException;
import hu.psprog.leaflet.bridge.client.exception.ResourceNotFoundException;
import hu.psprog.leaflet.bridge.client.exception.ValidationFailureException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link ResponseReader}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class ResponseReaderTest {

    @Mock
    private Response response;

    @InjectMocks
    private ResponseReader responseReader;

    private GenericType<BaseBodyDataModel> genericType = new GenericType<BaseBodyDataModel>() {};
    private BaseBodyDataModel baseBodyDataModel = EntryDataModel.getBuilder().build();

    @Before
    public void setup() {
        given(response.readEntity(genericType)).willReturn(baseBodyDataModel);
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
    }

    @Test
    public void shouldReadEntityWithoutContent() {

        // given
        given(response.getStatusInfo()).willReturn(Response.Status.OK);

        // when
        responseReader.read(response);
    }

    @Test(expected = ValidationFailureException.class)
    public void shouldThrowValidationFailureExceptionWhenReadingForGivenType() {

        // given
        given(response.getStatus()).willReturn(400);
        given(response.getStatusInfo()).willReturn(Response.Status.BAD_REQUEST);

        // when
        responseReader.read(response, genericType);

        // then
        // expected exception
        verify(response).readEntity(String.class);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionWhenReadingForGivenType() {

        // given
        given(response.getStatus()).willReturn(404);
        given(response.getStatusInfo()).willReturn(Response.Status.NOT_FOUND);

        // when
        responseReader.read(response, genericType);

        // then
        // expected exception
        verify(response).readEntity(String.class);
    }

    @Test(expected = RequestProcessingFailureException.class)
    public void shouldThrowRequestProcessingFailureExceptionWhenReadingForGivenType() {

        // given
        given(response.getStatus()).willReturn(500);
        given(response.getStatusInfo()).willReturn(Response.Status.INTERNAL_SERVER_ERROR);

        // when
        responseReader.read(response, genericType);

        // then
        // expected exception
        verify(response).readEntity(String.class);
    }

    @Test(expected = ValidationFailureException.class)
    public void shouldThrowValidationFailureExceptionWhenReadingWithoutContent() {

        // given
        given(response.getStatus()).willReturn(400);
        given(response.getStatusInfo()).willReturn(Response.Status.BAD_REQUEST);

        // when
        responseReader.read(response);

        // then
        // expected exception
        verify(response).readEntity(String.class);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void shouldThrowResourceNotFoundExceptionWhenReadingWithoutContent() {

        // given
        given(response.getStatus()).willReturn(404);
        given(response.getStatusInfo()).willReturn(Response.Status.NOT_FOUND);

        // when
        responseReader.read(response);

        // then
        // expected exception
        verify(response).readEntity(String.class);
    }

    @Test(expected = RequestProcessingFailureException.class)
    public void shouldThrowRequestProcessingFailureExceptionWhenReadingWithoutContent() {

        // given
        given(response.getStatus()).willReturn(500);
        given(response.getStatusInfo()).willReturn(Response.Status.INTERNAL_SERVER_ERROR);

        // when
        responseReader.read(response);

        // then
        // expected exception
        verify(response).readEntity(String.class);
    }
}
