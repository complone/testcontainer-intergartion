package com.alibaba.springcloud.e2e.cases;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;

@Slf4j
public class NacosWaitStrategyByDockerComposeTest {
    private static final int NACOS_PORT = 8848;
    
    private DockerComposeContainer<?> environment;
    
    @BeforeAll
    public final void setUp() {
        environment = new DockerComposeContainer<>(new File("src/test/resources/nacos-compose-test.yml"));
    }
    
    @AfterAll
    public final void cleanUp() {
        environment.stop();
    }
    
    @Test
    public void testWaitOnListeningPort() {
        environment.withExposedService("nacos_standalone", NACOS_PORT, Wait.forListeningPort());
        
        try {
            environment.start();
            LOGGER.info("Docker compose should start after waiting for listening port");
        } catch (RuntimeException e) {
            LOGGER.error(
                    "Docker compose should start after waiting for listening port with failed with: " + e
            );
        }
    }
}
