package com.concessio.crm.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class RailwayDataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        String effectiveUrl = this.url;
        if (effectiveUrl != null && effectiveUrl.startsWith("postgresql://")) {
            effectiveUrl = "jdbc:" + effectiveUrl;
        }

        return DataSourceBuilder
                .create()
                .url(effectiveUrl)
                .username(username)
                .password(password)
                .driverClassName(driverClassName)
                .build();
    }
}
