package com.example.base;

import com.example.testcontainer.base.SpringCloudAlibaba;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.DockerComposeContainer;

@Slf4j
@SpringCloudAlibaba(composeFiles = "nacos-compose-test.yml")
public class NacosWaitForAnnotationTest {
    
    
    @Before
    public final void setUp() {
    
    }
    
    @Test
    public void testWaitOnListeningPort() {
       log.info("nacos已经启动....");
    }
    
}
