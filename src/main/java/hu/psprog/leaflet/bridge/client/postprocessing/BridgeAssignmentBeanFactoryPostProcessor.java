package hu.psprog.leaflet.bridge.client.postprocessing;

import hu.psprog.leaflet.bridge.client.BridgeClient;
import hu.psprog.leaflet.bridge.client.domain.BridgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * @author Peter Smith
 */
@Component
public class BridgeAssignmentBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BridgeAssignmentBeanFactoryPostProcessor.class);

    private static final String TOO_MANY_CONSTRUCTORS = "Bean class [%s] must specify exactly one constructor.";
    private static final String BRIDGE_CLIENT_PARAMETER_NOT_FOUND = "BridgeClient parameter not found in constructor of bean class [%s]";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        Arrays.stream(beanFactory.getBeanNamesForAnnotation(BridgeService.class)).forEach(beanName -> {

            Class<?> classToInspect = beanFactory.getType(beanName);
            String clientName = extractClientName(classToInspect);
            int bridgeClientConstructorParameterIndex = findBridgeClientParameterInConstructor(classToInspect);

            beanFactory.getBeanDefinition(beanName)
                    .getConstructorArgumentValues()
                    .addIndexedArgumentValue(bridgeClientConstructorParameterIndex, new RuntimeBeanReference(clientName));

            LOGGER.info("Bridge service assignment processed between client [{}] and service [{}]", clientName, beanName);
        });
    }

    private String extractClientName(Class<?> classToInspect) {
        return classToInspect.getAnnotation(BridgeService.class).client();
    }

    private int findBridgeClientParameterInConstructor(Class<?> classToInspect) {

        Constructor<?>[] constructors = classToInspect.getConstructors();
        if (constructors.length > 1) {
            throw new IllegalArgumentException(String.format(TOO_MANY_CONSTRUCTORS, classToInspect.getName()));
        }

        int index = getBridgeClientParameter(constructors[0]);
        if (index == -1) {
            throw new IllegalArgumentException(String.format(BRIDGE_CLIENT_PARAMETER_NOT_FOUND, classToInspect.getName()));
        }

        return index;
    }

    private int getBridgeClientParameter(Constructor<?> constructorToInspect) {
        return Arrays.asList(constructorToInspect.getParameterTypes()).indexOf(BridgeClient.class);
    }
}
