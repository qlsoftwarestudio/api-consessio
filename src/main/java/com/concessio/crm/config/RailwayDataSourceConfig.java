package com.concessio.crm.config;

import java.net.URI;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RailwayDataSourceConfig {

    @Value("${spring.datasource.url:jdbc:postgresql://localhost:5432/concessio}")
    private String springUrl;

    @Value("${spring.datasource.username:concessio}")
    private String springUsername;

    @Value("${spring.datasource.password:concessio123}")
    private String springPassword;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");

        String effectiveUrl;
        String effectiveUsername;
        String effectivePassword;

        if (databaseUrl != null && !databaseUrl.isBlank()) {
            URI uri = URI.create(databaseUrl);
            String userInfo = uri.getUserInfo();
            String host = uri.getHost();
            int port = uri.getPort();
            String path = uri.getPath();

            if (path != null && path.startsWith("/")) {
                path = path.substring(1);
            }

            effectiveUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, path);

            if (userInfo != null && userInfo.contains(":")) {
                String[] parts = userInfo.split(":", 2);
                effectiveUsername = parts[0];
                effectivePassword = parts[1];
            } else {
                effectiveUsername = springUsername;
                effectivePassword = springPassword;
            }
        } else {
            effectiveUrl = springUrl;
            effectiveUsername = springUsername;
            effectivePassword = springPassword;
        }

        if (effectiveUrl != null && effectiveUrl.startsWith("postgresql://")) {
            effectiveUrl = "jdbc:" + effectiveUrl;
        }

        return DataSourceBuilder
                .create()
                .url(effectiveUrl)
                .username(effectiveUsername)
                .password(effectivePassword)
                .driverClassName(driverClassName)
                .build();
    }
}
