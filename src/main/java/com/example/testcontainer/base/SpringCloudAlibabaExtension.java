package com.example.testcontainer.base;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringCloudAlibabaExtension implements BeforeAllCallback, AfterAllCallback,
        BeforeEachCallback {
    
    private final boolean LOCAL_MODE = Objects.equals(System.getProperty("local"), "true");
    
    private DockerComposeContainer<?> compose;
    
    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void beforeAll(ExtensionContext context) throws IOException {
        Awaitility.setDefaultTimeout(Duration.ofSeconds(60));
        Awaitility.setDefaultPollInterval(Duration.ofSeconds(10));
        
        if (LOCAL_MODE) {
            runInLocal();
        } else {
            runInDockerContainer(context);
        }
        
        final Class<?> clazz = context.getRequiredTestClass();
        Stream.of(clazz.getDeclaredFields())
                .filter(it -> Modifier.isStatic(it.getModifiers()));
    }
    
    private void runInLocal() {
        Testcontainers.exposeHostPorts(3000);
    }
    
    private void runInDockerContainer(ExtensionContext context) {
        compose = createDockerCompose(context);
        compose.start();
    }
    
    @Override
    public void afterAll(ExtensionContext context) {
        
        if (compose != null) {
            compose.stop();
        }
    }
    
    @Override
    public void beforeEach(ExtensionContext context) {
        final Object instance = context.getRequiredTestInstance();
        Stream.of(instance.getClass().getDeclaredFields());
    }
    
    protected DockerComposeContainer<?> createDockerCompose(ExtensionContext context) {
        final Class<?> clazz = context.getRequiredTestClass();
        final SpringCloudAlibaba annotation = clazz.getAnnotation(SpringCloudAlibaba.class);
        final List<File> files = Stream.of(annotation.composeFiles())
                .map(it -> SpringCloudAlibaba.class.getClassLoader().getResource(it))
                .filter(Objects::nonNull)
                .map(URL::getPath)
                .map(File::new)
                .collect(Collectors.toList());
        compose = new DockerComposeContainer<>(files)
                .withPull(true)
                .withTailChildContainers(true)
                .withLogConsumer("springcloudalibaba_1", outputFrame -> log.info(outputFrame.getUtf8String()))
                .waitingFor("springcloudalibaba_1", Wait.forHealthcheck().withStartupTimeout(Duration.ofSeconds(180)));
        
        return compose;
    }
    
}
