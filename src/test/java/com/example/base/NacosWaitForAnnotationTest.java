package com.example.base;

import com.example.testcontainer.base.SpringCloudAlibaba;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.DockerComposeContainer;

@Slf4j
@SpringCloudAlibaba(composeFiles = "nacos-compose-test.yml")
public class NacosWaitForAnnotationTest {
    
    
    @BeforeAll
    public static void setUp() {
    
    }
    
    @Test
    @Order(10)
    public void testWaitOnListeningPort() {
       log.info("nacos已经启动....");
    }
    
}
