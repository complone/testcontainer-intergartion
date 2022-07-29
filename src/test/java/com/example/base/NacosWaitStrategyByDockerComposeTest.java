package com.example.base;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.rnorth.visibleassertions.VisibleAssertions;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

public class NacosWaitStrategyByDockerComposeTest {
    private static final int NACOS_PORT = 8848;
    
    private DockerComposeContainer<?> environment;
    
    @Before
    public final void setUp() {
        environment = new DockerComposeContainer<>(new File("src/test/resources/nacos-compose-test.yml"));
    }
    
    @After
    public final void cleanUp() {
        environment.stop();
    }
    
    @Test
    public void testWaitOnListeningPort() {
        environment.withExposedService("nacos_standalone", NACOS_PORT, Wait.forListeningPort());
        
        try {
            environment.starting(Description.createTestDescription(Object.class, "name"));
            VisibleAssertions.pass("Docker compose should start after waiting for listening port");
        } catch (RuntimeException e) {
            VisibleAssertions.fail(
                    "Docker compose should start after waiting for listening port with failed with: " + e
            );
        }
    }
}
