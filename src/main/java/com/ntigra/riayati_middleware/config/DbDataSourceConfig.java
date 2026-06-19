package com.ntigra.riayati_middleware.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

@Configuration
public class DbDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.riayati")
    public DataSourceProperties riayatiDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "riayatiDataSource")
    public DataSource riayatiDataSource() {
        return riayatiDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "riayatiJdbcTemplate")
    public JdbcTemplate riayatiJdbcTemplate(
            @Qualifier("riayatiDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
