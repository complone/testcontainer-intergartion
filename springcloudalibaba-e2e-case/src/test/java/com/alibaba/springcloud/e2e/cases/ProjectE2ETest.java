/*
 * Licensed to Apache Software Foundation (ASF) under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Apache Software Foundation (ASF) licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.alibaba.springcloud.e2e.cases;


import lombok.extern.slf4j.Slf4j;
import com.alibaba.springcloud.e2e.core.SpringCloudAlibaba;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Slf4j
@SpringCloudAlibaba(composeFiles = "docker/basic/docker-compose.yaml", serviceName = "nacos-standalone")
class ProjectE2ETest {
    private static final String project = "test-project-1";
    
    @BeforeAll
    public static void setup() {
    }
    

    @Test
    @Order(30)
    void testDeleteProject() {
      LOGGER.info("task starting... ");
    }
}
