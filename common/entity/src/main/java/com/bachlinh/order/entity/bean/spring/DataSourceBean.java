package com.bachlinh.order.entity.bean.spring;

import com.bachlinh.order.core.concurrent.ThreadPoolOptionHolder;
import com.bachlinh.order.entity.EntityProxyFactory;
import com.bachlinh.order.entity.index.internal.InternalProvider;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.JdbcSettings;
import org.hibernate.cfg.SchemaToolingSettings;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.sql.DataSource;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.Properties;

@Configuration
public class DataSourceBean {

    private String databaseAddress;
    private String databaseName;
    private String username;
    private String password;
    private String driverName;
    private String useDatabase;
    private String port;


    @Bean(name = "entityManagerFactory")
    LocalSessionFactoryBean sessionFactoryBean(DependenciesResolver dependenciesResolver, ThreadPoolOptionHolder threadPoolOptionHolder) {
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setDataSource(dataSource(threadPoolOptionHolder));
        factoryBean.setPackagesToScan(BaseEntity.class.getPackage().getName());
        factoryBean.setHibernateProperties(hibernateProperties());
        return factoryBean;
    }

    @Bean(name = "transactionManager")
    HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean
    EntityProxyFactory entityProxyFactory() {
        return InternalProvider.useDefaultEntityProxyFactory();
    }

    @Bean
    AuditorAware<Object> auditorProvider() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal;
        if (authentication != null) {
            principal = authentication.getPrincipal();
        } else {
            principal = "Application";
        }
        return () -> Optional.of((principal));
    }

    private Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(SchemaToolingSettings.HBM2DDL_AUTO, "update");
        hibernateProperties.setProperty(JdbcSettings.SHOW_SQL, "true");
        hibernateProperties.setProperty(JdbcSettings.FORMAT_SQL, "false");
        hibernateProperties.setProperty(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        hibernateProperties.setProperty(JdbcSettings.DIALECT, sqlDialect(useDatabase));
        return hibernateProperties;
    }

    private String sqlDialect(String databaseUse) {
        return switch (databaseUse) {
            case "sqlserver" -> SQLServerDialect.class.getName();
            case "mysql" -> MySQLDialect.class.getName();
            default -> throw new CriticalException("Application support mysql and sqlserver only");
        };
    }

    private DataSource dataSource(ThreadPoolOptionHolder threadPoolOptionHolder) {
        String url = MessageFormat.format(
                "jdbc:{0}://{1}:{2};database={3};trustServerCertificate=true;sendTimeAsDateTime=false;", useDatabase,
                databaseAddress, port, databaseName);
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverName);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setAutoCommit(true);
        config.setMaxLifetime(1000L * 60L * 30L);
        config.setConnectionTimeout(1000L * 60L * 30L);
        config.setMaximumPoolSize(1000);
        config.setMinimumIdle(2);
        config.setThreadFactory(threadPoolOptionHolder.getThreadOption().getVirtualThreadFactory());
        return new HikariDataSource(config);
    }

    @Value("${active.profile}")
    public void configProfile(String profile) {
        Environment environment = Environment.getInstance(profile);
        this.port = environment.getProperty("server.database.port");
        this.useDatabase = environment.getProperty("server.database.use");
        this.driverName = environment.getProperty("server.database.driver");
        this.username = environment.getProperty("server.database.username");
        this.password = environment.getProperty("server.database.password");
        this.databaseName = environment.getProperty("server.database.name");
        this.databaseAddress = environment.getProperty("server.database.address");
    }
}
