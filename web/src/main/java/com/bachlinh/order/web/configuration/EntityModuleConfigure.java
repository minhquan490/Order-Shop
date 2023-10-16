package com.bachlinh.order.web.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;

import com.bachlinh.order.core.concurrent.ThreadPoolOptionHolder;
import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityProxyFactory;
import com.bachlinh.order.entity.index.internal.InternalProvider;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.repository.BaseRepositoryManager;
import com.bachlinh.order.entity.repository.RepositoryManager;
import com.bachlinh.order.entity.transaction.internal.SpringTransactionManager;
import com.bachlinh.order.entity.transaction.shaded.JpaTransactionManager;
import com.bachlinh.order.entity.transaction.spi.TransactionManager;
import com.bachlinh.order.web.common.entity.DefaultEntityFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.AuditorAware;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class EntityModuleConfigure {
    private String databaseAddress;
    private String databaseName;
    private String username;
    private String password;
    private String driverName;
    private String useDatabase;
    private String port;
    private Environment environment;

    public LocalContainerEntityManagerFactoryBean sessionFactoryBean(ThreadPoolOptionHolder threadPoolOptionHolder) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource(threadPoolOptionHolder));
        factoryBean.setPackagesToScan(BaseEntity.class.getPackage().getName());
        factoryBean.setJpaProperties(hibernateProperties());
        factoryBean.setPersistenceProvider(new HibernatePersistenceProvider());
        return factoryBean;
    }


    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        var result = new JpaTransactionManager(entityManagerFactory);
        result.setRollbackOnCommitFailure(true);
        result.setJpaDialect(new HibernateJpaDialect());
        return result;
    }

    public EntityProxyFactory entityProxyFactory() {
        return InternalProvider.useDefaultEntityProxyFactory();
    }

    public AuditorAware<Object> auditorProvider() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal;
        if (authentication != null) {
            principal = authentication.getPrincipal();
        } else {
            principal = "Application";
        }
        return () -> Optional.of((principal));
    }

    public EntityFactory entityFactory(ApplicationContext applicationContext, String profile) throws IOException {
        return new DefaultEntityFactory.DefaultEntityFactoryBuilder()
                .container(ContainerWrapper.wrap(applicationContext))
                .profile(profile)
                .build();
    }

    public RepositoryManager repositoryManager(DependenciesContainerResolver containerResolver) {
        return new BaseRepositoryManager(containerResolver);
    }

    public TransactionManager<?> applicationTransactionManager(DependenciesResolver resolver) {
        return new SpringTransactionManager(resolver);
    }

    private Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty(AvailableSettings.HBM2DDL_AUTO, "update");
        hibernateProperties.setProperty(AvailableSettings.SHOW_SQL, "true");
        hibernateProperties.setProperty(AvailableSettings.FORMAT_SQL, "false");
        hibernateProperties.setProperty(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        hibernateProperties.setProperty(AvailableSettings.DIALECT, sqlDialect(useDatabase));
        hibernateProperties.setProperty(AvailableSettings.CONNECTION_HANDLING, PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_HOLD.name());
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
                "jdbc:{0}://{1}:{2};database={3};trustServerCertificate=true;sendTimeAsDateTime=false;",
                useDatabase,
                databaseAddress,
                port,
                databaseName);
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
        config.setThreadFactory(threadPoolOptionHolder.getThreadOption().getThreadFactory());
        return new HikariDataSource(config);
    }

    public void configProfile(String profile) {
        this.environment = Environment.getInstance(profile);
        this.port = environment.getProperty("server.database.port");
        this.useDatabase = environment.getProperty("server.database.use");
        this.driverName = environment.getProperty("server.database.driver");
        this.username = environment.getProperty("server.database.username");
        this.password = environment.getProperty("server.database.password");
        this.databaseName = environment.getProperty("server.database.name");
        this.databaseAddress = environment.getProperty("server.database.address");
    }

    public Environment getEnvironment() {
        return environment;
    }
}
