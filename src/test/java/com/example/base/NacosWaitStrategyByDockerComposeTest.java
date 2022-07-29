package com.example.base;

import com.example.testcontainer.base.SpringCloudAlibaba;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;

@Slf4j
@SpringCloudAlibaba(composeFiles = "nacos-compose-test.yml")
public class NacosWaitStrategyByDockerComposeTest {
    
    private static final int NACOS_PORT = 8848;
    
    private DockerComposeContainer<?> container;
    
    @Before
    public final void setUp() {
    
    }
    
    @After
    public final void cleanUp() {
        container.stop();
    }
    
    @Test
    public void testWaitOnListeningPort() {
       log.info("nacos已经启动....");
    }
    
}
