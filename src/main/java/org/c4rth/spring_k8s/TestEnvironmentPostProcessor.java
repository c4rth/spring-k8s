package org.c4rth.spring_k8s;

import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TestEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private final Log log;

    public TestEnvironmentPostProcessor(DeferredLogFactory logFactory) {
        this.log = logFactory.getLog(getClass());
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (isKubernetes(environment)) {
            log.info("Running in Kubernetes");
        } else {
            log.info("NOT running in Kubernetes");
            return;
        }
        environment.getPropertySources().forEach(ps -> {
            log.info(ps.getName() + " = " + ps.getClass().getName());
            if (ps instanceof CompositePropertySource cps) {
                log.info("*** Composite");
                cps.getPropertySources().forEach(ps2 ->
                        log.info("*** " + ps2.getName() + " = " + ps2.getClass().getName())
                );
            }
            if (ps instanceof EnumerablePropertySource<?> mps) {
                var str1 = Arrays.stream(mps.getPropertyNames()).map(key -> key + " = " + mps.getProperty(key))
                        .collect(Collectors.joining(",\n", "{", "}"));
                log.info("    properties = " + str1);
            } else {
                log.info("    properties = NOT EnumerablePropertySource");
            }
        });
    }

    private boolean isKubernetes(ConfigurableEnvironment environment) {
        return CloudPlatform.KUBERNETES.isActive(environment);
    }
}
