package com.book.socket.config;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

@EnableTransactionManagement
@Configuration
public class MySQLConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource datasource) {
        return new DataSourceTransactionManager(datasource);
    }

    @Bean
    public TransactionTemplate transactionTemplate(
            DataSourceTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    // 실무에서 많이 사용하는 방식
    @Bean(name = "createUserTransactionManager")
    public PlatformTransactionManager createTransactionManager(DataSource datasource) {
        return new DataSourceTransactionManager(datasource);
    }
}

