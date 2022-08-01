package com.alibaba.springcloud.e2e.cases;

import com.alibaba.springcloud.e2e.core.SpringCloudAlibaba;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Slf4j
@SpringCloudAlibaba(composeFiles = "docker/nacos-compose-test.yml", serviceName = "nacos-standalone")
public class NacosWaitForAnnotationTest {
    
    
    @BeforeAll
    public static void setUp() {

    }
    
    @Test
    @Order(10)
    public void testWaitOnListeningPort() {
       LOGGER.info("nacos已经启动....");
    }
    
}
