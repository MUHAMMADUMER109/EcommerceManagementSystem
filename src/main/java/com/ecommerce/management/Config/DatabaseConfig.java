package com.ecommerce.management.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;


@Configuration
public class DatabaseConfig {

    private static JdbcTemplate instance = null;

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {

        if (instance == null) {
            instance = new JdbcTemplate(dataSource);
        }

        return instance;
    }
    public static JdbcTemplate getInstance() {
        return instance;
    }
}