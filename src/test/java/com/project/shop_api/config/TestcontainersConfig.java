package com.project.shop_api.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;

public class TestcontainersConfig {

    public static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("shop_test")
            .withUsername("test")
            .withPassword("test");

    public static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext ctx) {

            TestcontainersConfig.mysql.start();

            TestPropertyValues.of(
                    "spring.datasource.url=" + mysql.getJdbcUrl(),
                    "spring.datasource.username=" + mysql.getUsername(),
                    "spring.datasource.password=" + mysql.getPassword(),
                    "spring.jpa.hibernate.ddl-auto=create-drop",
                    "spring.jpa.show-sql=false"
            ).applyTo(ctx.getEnvironment());
        }
    }
}