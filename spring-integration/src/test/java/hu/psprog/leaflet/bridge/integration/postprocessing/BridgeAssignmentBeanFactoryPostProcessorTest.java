package hu.psprog.leaflet.bridge.integration.postprocessing;

import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link BridgeAssignmentBeanFactoryPostProcessor}.
 *
 * @author Peter Smith
 */
@RunWith(MockitoJUnitRunner.class)
public class BridgeAssignmentBeanFactoryPostProcessorTest {

    private static final String CLIENT_BEAN_NAME = "testClient";
    private static final RuntimeBeanReference RUNTIME_BEAN_REFERENCE = new RuntimeBeanReference(CLIENT_BEAN_NAME);

    @Mock
    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Mock
    private BeanDefinition beanDefinition;

    @Mock
    private ConstructorArgumentValues constructorArgumentValues;

    @InjectMocks
    private BridgeAssignmentBeanFactoryPostProcessor bridgeAssignmentBeanFactoryPostProcessor;

    @Test
    public void shouldPostProcessorProcessClientAssignments() {

        // given
        prepareMocks(TestClient.class);

        // when
        bridgeAssignmentBeanFactoryPostProcessor.postProcessBeanFactory(configurableListableBeanFactory);

        // then
        verify(constructorArgumentValues).addIndexedArgumentValue(eq(0), eq(RUNTIME_BEAN_REFERENCE));
    }

    @Test
    public void shouldPostProcessorProcessClientAssignmentsWithDifferentConstructor() {

        // given
        prepareMocks(TestClientWithMultiParameterConstructor.class);

        // when
        bridgeAssignmentBeanFactoryPostProcessor.postProcessBeanFactory(configurableListableBeanFactory);

        // then
        verify(constructorArgumentValues).addIndexedArgumentValue(eq(1), eq(RUNTIME_BEAN_REFERENCE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPostProcessorThrowExceptionForMultipleConstructors() {

        // given
        prepareMocks(TestClientWithMultipleConstructors.class);

        // when
        bridgeAssignmentBeanFactoryPostProcessor.postProcessBeanFactory(configurableListableBeanFactory);

        // then
        // exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPostProcessorThrowExceptionForInvalidConstructor() {

        // given
        prepareMocks(TestClientWithInvalidConstructor.class);

        // when
        bridgeAssignmentBeanFactoryPostProcessor.postProcessBeanFactory(configurableListableBeanFactory);

        // then
        // exception expected
    }

    private void prepareMocks(Class<?> testClientClass) {
        given(configurableListableBeanFactory.getBeanNamesForAnnotation(BridgeService.class)).willReturn(new String[] {CLIENT_BEAN_NAME});
        doReturn(testClientClass).when(configurableListableBeanFactory).getType(CLIENT_BEAN_NAME);
        given(configurableListableBeanFactory.getBeanDefinition(CLIENT_BEAN_NAME)).willReturn(beanDefinition);
        given(beanDefinition.getConstructorArgumentValues()).willReturn(constructorArgumentValues);
    }

    @BridgeService(client = CLIENT_BEAN_NAME)
    public static class TestClient {

        @Autowired
        public TestClient(BridgeClient bridgeClient) {
        }
    }

    @BridgeService(client = CLIENT_BEAN_NAME)
    public static class TestClientWithMultiParameterConstructor {

        @Autowired
        public TestClientWithMultiParameterConstructor(String parameter1, BridgeClient bridgeClient) {
        }
    }

    @BridgeService(client = CLIENT_BEAN_NAME)
    public static class TestClientWithMultipleConstructors {

        public TestClientWithMultipleConstructors(String parameter) {
        }

        public TestClientWithMultipleConstructors(String parameter1, Integer parameter2) {
        }
    }

    @BridgeService(client = CLIENT_BEAN_NAME)
    public static class TestClientWithInvalidConstructor {

        public TestClientWithInvalidConstructor(String parameter) {
        }
    }
}