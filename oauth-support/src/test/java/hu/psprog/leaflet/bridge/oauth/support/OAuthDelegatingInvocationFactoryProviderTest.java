package hu.psprog.leaflet.bridge.oauth.support;

import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactory;
import hu.psprog.leaflet.bridge.client.request.RequestAdapter;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.client.request.strategy.CallStrategy;
import hu.psprog.leaflet.bridge.integration.request.adapter.StaticRequestAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link OAuthDelegatingInvocationFactoryProvider}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
class OAuthDelegatingInvocationFactoryProviderTest {

    private static final String REGISTRATION_ID = "oauth-client-1";

    @Mock
    private InvocationFactory defaultInvocationFactory;

    @Mock(lenient = true)
    private CallStrategy callStrategy;

    @Mock
    private RequestAdapter defaultRequestAdapter;

    @Mock
    private RequestAuthentication requestAuthentication;

    @Mock
    private OAuthRequestAuthenticationFactory oAuthRequestAuthenticationFactory;

    private OAuthDelegatingInvocationFactoryProvider oAuthDelegatingInvocationFactoryProvider;

    @BeforeEach
    public void setup() {

        given(callStrategy.forMethod()).willReturn(RequestMethod.GET);

        oAuthDelegatingInvocationFactoryProvider = new OAuthDelegatingInvocationFactoryProvider(defaultInvocationFactory,
                List.of(callStrategy), defaultRequestAdapter, oAuthRequestAuthenticationFactory);
    }

    @Test
    public void shouldGetInvocationFactoryReturnDefaultInstanceForNonOAuthClient() {

        // given
        BridgeSettings bridgeSettings = prepareBridgeSettings(null, true);

        // when
        InvocationFactory result = oAuthDelegatingInvocationFactoryProvider.getInvocationFactory(bridgeSettings);

        // then
        assertThat(result, equalTo(defaultInvocationFactory));
        verifyNoInteractions(oAuthRequestAuthenticationFactory);
    }

    @Test
    public void shouldGetInvocationFactoryReturnCustomInstanceForOAuthClientWithDefaultRequestAdapter() throws IllegalAccessException {

        // given
        BridgeSettings bridgeSettings = prepareBridgeSettings(REGISTRATION_ID, true);
        given(oAuthRequestAuthenticationFactory.createRequestAuthentication(bridgeSettings)).willReturn(requestAuthentication);

        // when
        InvocationFactory result = oAuthDelegatingInvocationFactoryProvider.getInvocationFactory(bridgeSettings);

        // then
        assertThat(result, not(equalTo(defaultInvocationFactory)));
        assertRequestAuthentication(result);
        assertThat(extractRequestAdapter(result), equalTo(defaultRequestAdapter));
    }

    @Test
    public void shouldGetInvocationFactoryReturnCustomInstanceForOAuthClientWithStaticRequestAdapter() throws IllegalAccessException {

        // given
        BridgeSettings bridgeSettings = prepareBridgeSettings(REGISTRATION_ID, false);

        // when
        InvocationFactory result = oAuthDelegatingInvocationFactoryProvider.getInvocationFactory(bridgeSettings);

        // then
        assertThat(result, not(equalTo(defaultInvocationFactory)));
        assertRequestAuthentication(result);
        RequestAdapter requestAdapter = extractRequestAdapter(result);
        assertThat(requestAdapter, isA(StaticRequestAdapter.class));
        assertThat(requestAdapter.provideClientID(), equalTo(REGISTRATION_ID));
    }

    private void assertRequestAuthentication(InvocationFactory invocationFactory) throws IllegalAccessException {

        RequestAuthentication requestAuthentication = extractFieldValue(invocationFactory, "requestAuthentication");
        assertThat(requestAuthentication, equalTo(requestAuthentication));
    }

    private RequestAdapter extractRequestAdapter(InvocationFactory invocationFactory) throws IllegalAccessException {
        return extractFieldValue(invocationFactory, "requestAdapter");
    }

    private <T> T extractFieldValue(Object targetObject, String fieldName) throws IllegalAccessException {

        Field field = ReflectionUtils.findField(targetObject.getClass(), fieldName);
        Objects.requireNonNull(field).setAccessible(true);

        return (T) field.get(targetObject);
    }

    private static BridgeSettings prepareBridgeSettings(String registrationID, boolean useLeafletLink) {

        return BridgeSettings.getBuilder()
                .withOAuthRegistrationID(registrationID)
                .withUseLeafletLink(useLeafletLink)
                .build();
    }
}
