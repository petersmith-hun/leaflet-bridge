package hu.psprog.leaflet.bridge.oauth.support;

import hu.psprog.leaflet.bridge.client.domain.BridgeSettings;
import hu.psprog.leaflet.bridge.client.handler.InvocationFactory;
import hu.psprog.leaflet.bridge.client.request.RequestAdapter;
import hu.psprog.leaflet.bridge.client.request.RequestAuthentication;
import hu.psprog.leaflet.bridge.client.request.RequestMethod;
import hu.psprog.leaflet.bridge.client.request.strategy.CallStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;

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
    private RequestAdapter requestAdapter;

    @Mock
    private OAuth2AuthorizedClientManager clientManager;

    private OAuthDelegatingInvocationFactoryProvider oAuthDelegatingInvocationFactoryProvider;

    @BeforeEach
    public void setup() {

        given(callStrategy.forMethod()).willReturn(RequestMethod.GET);

        oAuthDelegatingInvocationFactoryProvider = new OAuthDelegatingInvocationFactoryProvider(defaultInvocationFactory,
                List.of(callStrategy), requestAdapter, clientManager);
    }

    @Test
    public void shouldGetInvocationFactoryReturnDefaultInstanceForNonOAuthClient() {

        // given
        BridgeSettings bridgeSettings = prepareBridgeSettings(null);

        // when
        InvocationFactory result = oAuthDelegatingInvocationFactoryProvider.getInvocationFactory(bridgeSettings);

        // then
        assertThat(result, equalTo(defaultInvocationFactory));
    }

    @Test
    public void shouldGetInvocationFactoryReturnCustomInstanceForOAuthClient() throws IllegalAccessException {

        // given
        BridgeSettings bridgeSettings = prepareBridgeSettings(REGISTRATION_ID);

        // when
        InvocationFactory result = oAuthDelegatingInvocationFactoryProvider.getInvocationFactory(bridgeSettings);

        // then
        assertThat(result, not(equalTo(defaultInvocationFactory)));
        RequestAuthentication requestAuthentication = assertRequestAuthentication(result);
        assertRegistrationID(requestAuthentication);
    }

    private RequestAuthentication assertRequestAuthentication(InvocationFactory invocationFactory) throws IllegalAccessException {

        RequestAuthentication requestAuthentication = extractFieldValue(invocationFactory, "requestAuthentication");
        assertThat(requestAuthentication, isA(SpringIntegratedOAuthRequestAuthentication.class));

        return requestAuthentication;
    }

    private void assertRegistrationID(RequestAuthentication requestAuthentication) throws IllegalAccessException {

        String registrationID = extractFieldValue(requestAuthentication, "registrationID");
        assertThat(registrationID, equalTo(REGISTRATION_ID));
    }

    private <T> T extractFieldValue(Object targetObject, String fieldName) throws IllegalAccessException {

        Field field = ReflectionUtils.findField(targetObject.getClass(), fieldName);
        Objects.requireNonNull(field).setAccessible(true);

        return (T) field.get(targetObject);
    }

    private static BridgeSettings prepareBridgeSettings(String registrationID) {

        return BridgeSettings.getBuilder()
                .withOAuthRegistrationID(registrationID)
                .build();
    }
}
