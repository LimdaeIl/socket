package com.book.socket.config;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
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

    // 지금처럼 직접 작성하게 된다면,
    // 스프링 부트가 MySQL Config 자동 설정하지 않으므로 직접 작성해야 한다.
    // 엔티티를 정확하게 넣어주어야 한다는 것.
    @Bean(name = "createChatTransactionManager")
    public PlatformTransactionManager createChatTransactionManager(
            EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    // 실무에서 많이 사용하는 방식
    @Bean(name = "createUserTransactionManager")
    public PlatformTransactionManager createUserTransactionManager(DataSource datasource) {
        return new DataSourceTransactionManager(datasource);
    }
}

