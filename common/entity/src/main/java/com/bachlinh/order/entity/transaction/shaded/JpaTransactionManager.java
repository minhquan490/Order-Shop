package com.bachlinh.order.entity.transaction.shaded;

import com.google.common.base.Objects;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.RollbackException;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.NamedThreadLocal;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.JdbcTransactionObjectSupport;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.DefaultJpaDialect;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.NestedTransactionNotSupportedException;
import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.DelegatingTransactionDefinition;
import org.springframework.transaction.support.ResourceTransactionDefinition;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

public class JpaTransactionManager extends AbstractPlatformTransactionManager
        implements ResourceTransactionManager, BeanFactoryAware, InitializingBean {

    @Nullable
    private transient EntityManagerFactory entityManagerFactory;

    @Nullable
    private String persistenceUnitName;

    private final Map<String, Object> jpaPropertyMap = new HashMap<>();

    @Nullable
    private transient DataSource dataSource;

    private transient JpaDialect jpaDialect = new DefaultJpaDialect();

    @Nullable
    private transient Consumer<EntityManager> entityManagerInitializer;


    /**
     * Create a new JpaTransactionManager instance.
     * <p>An EntityManagerFactory has to be set to be able to use it.
     *
     * @see #setEntityManagerFactory
     */
    public JpaTransactionManager() {
        setNestedTransactionAllowed(true);
    }

    /**
     * Create a new JpaTransactionManager instance.
     *
     * @param emf the EntityManagerFactory to manage transactions for
     */
    public JpaTransactionManager(EntityManagerFactory emf) {
        this();
        this.entityManagerFactory = emf;
        afterPropertiesSet();
    }


    /**
     * Set the EntityManagerFactory that this instance should manage transactions for.
     * <p>Alternatively, specify the persistence unit name of the target EntityManagerFactory.
     * By default, a default EntityManagerFactory will be retrieved by finding a
     * single unique bean of type EntityManagerFactory in the containing BeanFactory.
     *
     * @see #setPersistenceUnitName
     */
    public void setEntityManagerFactory(@Nullable EntityManagerFactory emf) {
        this.entityManagerFactory = emf;
    }

    /**
     * Return the EntityManagerFactory that this instance should manage transactions for.
     */
    @Nullable
    public EntityManagerFactory getEntityManagerFactory() {
        return this.entityManagerFactory;
    }

    /**
     * Obtain the EntityManagerFactory for actual use.
     *
     * @return the EntityManagerFactory (never {@code null})
     * @throws IllegalStateException in case of no EntityManagerFactory set
     * @since 5.0
     */
    protected final EntityManagerFactory obtainEntityManagerFactory() {
        EntityManagerFactory emf = getEntityManagerFactory();
        Assert.state(emf != null, "No EntityManagerFactory set");
        return emf;
    }

    /**
     * Set the name of the persistence unit to manage transactions for.
     * <p>This is an alternative to specifying the EntityManagerFactory by direct reference,
     * resolving it by its persistence unit name instead. If no EntityManagerFactory and
     * no persistence unit name have been specified, a default EntityManagerFactory will
     * be retrieved by finding a single unique bean of type EntityManagerFactory.
     *
     * @see #setEntityManagerFactory
     */
    public void setPersistenceUnitName(@Nullable String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    /**
     * Return the name of the persistence unit to manage transactions for, if any.
     */
    @Nullable
    public String getPersistenceUnitName() {
        return this.persistenceUnitName;
    }

    /**
     * Specify JPA properties, to be passed into
     * {@code EntityManagerFactory.createEntityManager(Map)} (if any).
     * <p>Can be populated with a String "value" (parsed via PropertiesEditor)
     * or a "props" element in XML bean definitions.
     *
     * @see jakarta.persistence.EntityManagerFactory#createEntityManager(java.util.Map)
     */
    public void setJpaProperties(@Nullable Properties jpaProperties) {
        CollectionUtils.mergePropertiesIntoMap(jpaProperties, this.jpaPropertyMap);
    }

    /**
     * Specify JPA properties as a Map, to be passed into
     * {@code EntityManagerFactory.createEntityManager(Map)} (if any).
     * <p>Can be populated with a "map" or "props" element in XML bean definitions.
     *
     * @see jakarta.persistence.EntityManagerFactory#createEntityManager(java.util.Map)
     */
    public void setJpaPropertyMap(@Nullable Map<String, ?> jpaProperties) {
        if (jpaProperties != null) {
            this.jpaPropertyMap.putAll(jpaProperties);
        }
    }

    /**
     * Allow {@code Map} access to the JPA properties to be passed to the persistence
     * provider, with the option to add or override specific entries.
     * <p>Useful for specifying entries directly, for example via {@code jpaPropertyMap[myKey]}.
     */
    public Map<String, Object> getJpaPropertyMap() {
        return this.jpaPropertyMap;
    }

    /**
     * Set the JDBC DataSource that this instance should manage transactions for.
     * The DataSource should match the one used by the JPA EntityManagerFactory:
     * for example, you could specify the same JNDI DataSource for both.
     * <p>If the EntityManagerFactory uses a known DataSource as its connection factory,
     * the DataSource will be autodetected: You can still explicitly specify the
     * DataSource, but you don't need to in this case.
     * <p>A transactional JDBC Connection for this DataSource will be provided to
     * application code accessing this DataSource directly via DataSourceUtils
     * or JdbcTemplate. The Connection will be taken from the JPA EntityManager.
     * <p>Note that you need to use a JPA dialect for a specific JPA implementation
     * to allow for exposing JPA transactions as JDBC transactions.
     * <p>The DataSource specified here should be the target DataSource to manage
     * transactions for, not a TransactionAwareDataSourceProxy. Only data access
     * code may work with TransactionAwareDataSourceProxy, while the transaction
     * manager needs to work on the underlying target DataSource. If there's
     * nevertheless a TransactionAwareDataSourceProxy passed in, it will be
     * unwrapped to extract its target DataSource.
     *
     * @see EntityManagerFactoryInfo#getDataSource()
     * @see #setJpaDialect
     * @see org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
     * @see org.springframework.jdbc.datasource.DataSourceUtils
     * @see org.springframework.jdbc.core.JdbcTemplate
     */
    public void setDataSource(@Nullable DataSource dataSource) {
        if (dataSource instanceof TransactionAwareDataSourceProxy proxy) {
            // If we got a TransactionAwareDataSourceProxy, we need to perform transactions
            // for its underlying target DataSource, else data access code won't see
            // properly exposed transactions (i.e. transactions for the target DataSource).
            this.dataSource = proxy.getTargetDataSource();
        } else {
            this.dataSource = dataSource;
        }
    }

    /**
     * Return the JDBC DataSource that this instance manages transactions for.
     */
    @Nullable
    public DataSource getDataSource() {
        return this.dataSource;
    }

    /**
     * Set the JPA dialect to use for this transaction manager.
     * Used for vendor-specific transaction management and JDBC connection exposure.
     * <p>If the EntityManagerFactory uses a known JpaDialect, it will be autodetected:
     * You can still explicitly specify the DataSource, but you don't need to in this case.
     * <p>The dialect object can be used to retrieve the underlying JDBC connection
     * and thus allows for exposing JPA transactions as JDBC transactions.
     *
     * @see EntityManagerFactoryInfo#getJpaDialect()
     * @see JpaDialect#beginTransaction
     * @see JpaDialect#getJdbcConnection
     */
    public void setJpaDialect(@Nullable JpaDialect jpaDialect) {
        this.jpaDialect = (jpaDialect != null ? jpaDialect : new DefaultJpaDialect());
    }

    /**
     * Return the JPA dialect to use for this transaction manager.
     */
    public JpaDialect getJpaDialect() {
        return this.jpaDialect;
    }

    /**
     * Specify a callback for customizing every {@code EntityManager} resource
     * created for a new transaction managed by this {@code JpaTransactionManager}.
     * <p>This is an alternative to a factory-level {@code EntityManager} customizer
     * and to a {@code JpaVendorAdapter}-level {@code postProcessEntityManager}
     * callback, enabling specific customizations of transactional resources.
     *
     * @see #createEntityManagerForTransaction()
     * @see AbstractEntityManagerFactoryBean#setEntityManagerInitializer
     * @see JpaVendorAdapter#postProcessEntityManager
     * @since 5.3
     */
    public void setEntityManagerInitializer(Consumer<EntityManager> entityManagerInitializer) {
        this.entityManagerInitializer = entityManagerInitializer;
    }

    /**
     * Retrieves an EntityManagerFactory by persistence unit name, if none set explicitly.
     * Falls back to a default EntityManagerFactory bean if no persistence unit specified.
     *
     * @see #setPersistenceUnitName
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (getEntityManagerFactory() == null) {
            if (!(beanFactory instanceof ListableBeanFactory lbf)) {
                throw new IllegalStateException("Cannot retrieve EntityManagerFactory by persistence unit name " +
                        "in a non-listable BeanFactory: " + beanFactory);
            }
            setEntityManagerFactory(EntityManagerFactoryUtils.findEntityManagerFactory(lbf, getPersistenceUnitName()));
        }
    }

    /**
     * Eagerly initialize the JPA dialect, creating a default one
     * for the specified EntityManagerFactory if none set.
     * Auto-detect the EntityManagerFactory's DataSource, if any.
     */
    @Override
    public void afterPropertiesSet() {
        if (getEntityManagerFactory() == null) {
            throw new IllegalArgumentException("'entityManagerFactory' or 'persistenceUnitName' is required");
        }
        if (getEntityManagerFactory() instanceof EntityManagerFactoryInfo emfInfo) {
            DataSource dataSource = emfInfo.getDataSource();
            if (dataSource != null) {
                setDataSource(dataSource);
            }
            JpaDialect jpaDialect = emfInfo.getJpaDialect();
            if (jpaDialect != null) {
                setJpaDialect(jpaDialect);
            }
        }
    }


    @Override
    public Object getResourceFactory() {
        return obtainEntityManagerFactory();
    }

    @Override
    protected Object doGetTransaction() {
        JpaTransactionObject txObject = new JpaTransactionObject();
        txObject.setSavepointAllowed(isNestedTransactionAllowed());

        EntityManagerHolder emHolder = (EntityManagerHolder)
                TransactionSynchronizationManager.getResource(obtainEntityManagerFactory());
        if (emHolder != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Found thread-bound EntityManager [" + emHolder.getEntityManager() +
                        "] for JPA transaction");
            }
            txObject.setEntityManagerHolder(emHolder, false);
        }

        if (getDataSource() != null) {
            ConnectionHolder conHolder = (ConnectionHolder)
                    TransactionSynchronizationManager.getResource(getDataSource());
            txObject.setConnectionHolder(conHolder);
        }

        return txObject;
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) {
        return ((JpaTransactionObject) transaction).hasTransaction();
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        JpaTransactionObject txObject = getJpaTransactionObject((JpaTransactionObject) transaction);

        try {
            if (!txObject.hasEntityManagerHolder() ||
                    txObject.getEntityManagerHolder().isSynchronizedWithTransaction()) {
                EntityManager newEm = createEntityManagerForTransaction();
                if (logger.isDebugEnabled()) {
                    logger.debug("Opened new EntityManager [" + newEm + "] for JPA transaction");
                }
                txObject.setEntityManagerHolder(new EntityManagerHolder(newEm), true);
            }

            EntityManager em = txObject.getEntityManagerHolder().getEntityManager();

            // Delegate to JpaDialect for actual transaction begin.
            int timeoutToUse = determineTimeout(definition);
            Object transactionData = getJpaDialect().beginTransaction(em,
                    new JpaTransactionDefinition(definition, timeoutToUse, txObject.isNewEntityManagerHolder()));
            txObject.setTransactionData(transactionData);
            txObject.setReadOnly(definition.isReadOnly());

            // Register transaction timeout.
            if (timeoutToUse != TransactionDefinition.TIMEOUT_DEFAULT) {
                txObject.getEntityManagerHolder().setTimeoutInSeconds(timeoutToUse);
            }

            // Register the JPA EntityManager's JDBC Connection for the DataSource, if set.
            if (getDataSource() != null) {
                ConnectionHandle conHandle = getJpaDialect().getJdbcConnection(em, definition.isReadOnly());
                if (conHandle != null) {
                    ConnectionHolder conHolder = new ConnectionHolder(conHandle);
                    if (timeoutToUse != TransactionDefinition.TIMEOUT_DEFAULT) {
                        conHolder.setTimeoutInSeconds(timeoutToUse);
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("Exposing JPA transaction as JDBC [" + conHandle + "]");
                    }
                    TransactionSynchronizationManager.bindResource(getDataSource(), conHolder);
                    txObject.setConnectionHolder(conHolder);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Not exposing JPA transaction [" + em + "] as JDBC transaction because " +
                                "JpaDialect [" + getJpaDialect() + "] does not support JDBC Connection retrieval");
                    }
                }
            }

            // Bind the entity manager holder to the thread.
            if (txObject.isNewEntityManagerHolder()) {
                TransactionSynchronizationManager.bindResource(
                        obtainEntityManagerFactory(), txObject.getEntityManagerHolder());
            }
            txObject.getEntityManagerHolder().setSynchronizedWithTransaction(true);
        } catch (TransactionException ex) {
            closeEntityManagerAfterFailedBegin(txObject);
            throw ex;
        } catch (Throwable ex) {
            closeEntityManagerAfterFailedBegin(txObject);
            throw new CannotCreateTransactionException("Could not open JPA EntityManager for transaction", ex);
        }
    }

    private JpaTransactionObject getJpaTransactionObject(JpaTransactionObject transaction) {
        JpaTransactionObject txObject = transaction;

        if (txObject.hasConnectionHolder() && !txObject.getConnectionHolder().isSynchronizedWithTransaction()) {
            throw new IllegalTransactionStateException(
                    "Pre-bound JDBC Connection found! JpaTransactionManager does not support " +
                            "running within DataSourceTransactionManager if told to manage the DataSource itself. " +
                            "It is recommended to use a single JpaTransactionManager for all transactions " +
                            "on a single DataSource, no matter whether JPA or JDBC access.");
        }
        return txObject;
    }

    /**
     * Create a JPA EntityManager to be used for a transaction.
     * <p>The default implementation checks whether the EntityManagerFactory
     * is a Spring proxy and delegates to
     * {@link EntityManagerFactoryInfo#createNativeEntityManager}
     * if possible which in turns applies
     * {@link JpaVendorAdapter#postProcessEntityManager(EntityManager)}.
     *
     * @see jakarta.persistence.EntityManagerFactory#createEntityManager()
     */
    protected EntityManager createEntityManagerForTransaction() {
        EntityManagerFactory emf = obtainEntityManagerFactory();
        Map<String, Object> properties = getJpaPropertyMap();
        EntityManager em;
        if (emf instanceof EntityManagerFactoryInfo emfInfo) {
            em = emfInfo.createNativeEntityManager(properties);
        } else {
            em = (!CollectionUtils.isEmpty(properties) ?
                    emf.createEntityManager(properties) : emf.createEntityManager());
        }
        if (this.entityManagerInitializer != null) {
            this.entityManagerInitializer.accept(em);
        }
        return new EntityManagerThreadLocal(em);
    }

    /**
     * Close the current transaction's EntityManager.
     * Called after a transaction begin attempt failed.
     *
     * @param txObject the current transaction
     */
    protected void closeEntityManagerAfterFailedBegin(JpaTransactionObject txObject) {
        if (txObject.isNewEntityManagerHolder()) {
            EntityManager em = txObject.getEntityManagerHolder().getEntityManager();
            try {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            } catch (Throwable ex) {
                logger.debug("Could not rollback EntityManager after failed transaction begin", ex);
            } finally {
                EntityManagerFactoryUtils.closeEntityManager(em);
            }
            txObject.setEntityManagerHolder(null, false);
        }
    }

    @Override
    protected Object doSuspend(Object transaction) {
        JpaTransactionObject txObject = (JpaTransactionObject) transaction;
        txObject.setEntityManagerHolder(null, false);
        EntityManagerHolder entityManagerHolder = (EntityManagerHolder)
                TransactionSynchronizationManager.unbindResource(obtainEntityManagerFactory());
        txObject.setConnectionHolder(null);
        ConnectionHolder connectionHolder = null;
        if (getDataSource() != null && TransactionSynchronizationManager.hasResource(getDataSource())) {
            connectionHolder = (ConnectionHolder) TransactionSynchronizationManager.unbindResource(getDataSource());
        }
        return new SuspendedResourcesHolder(entityManagerHolder, connectionHolder);
    }

    @Override
    protected void doResume(@Nullable Object transaction, Object suspendedResources) {
        SuspendedResourcesHolder resourcesHolder = (SuspendedResourcesHolder) suspendedResources;
        TransactionSynchronizationManager.bindResource(
                obtainEntityManagerFactory(), resourcesHolder.getEntityManagerHolder());
        if (getDataSource() != null && resourcesHolder.getConnectionHolder() != null) {
            TransactionSynchronizationManager.bindResource(getDataSource(), resourcesHolder.getConnectionHolder());
        }
    }

    /**
     * This implementation returns "true": a JPA commit will properly handle
     * transactions that have been marked rollback-only at a global level.
     */
    @Override
    protected boolean shouldCommitOnGlobalRollbackOnly() {
        return true;
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        JpaTransactionObject txObject = (JpaTransactionObject) status.getTransaction();
        if (status.isDebug()) {
            logger.debug("Committing JPA transaction on EntityManager [" +
                    txObject.getEntityManagerHolder().getEntityManager() + "]");
        }
        try {
            EntityTransaction tx = txObject.getEntityManagerHolder().getEntityManager().getTransaction();
            tx.commit();
        } catch (RollbackException ex) {
            if (ex.getCause() instanceof RuntimeException runtimeException) {
                DataAccessException dae = getJpaDialect().translateExceptionIfPossible(runtimeException);
                if (dae != null) {
                    throw dae;
                }
            }
            throw new TransactionSystemException("Could not commit JPA transaction", ex);
        } catch (RuntimeException ex) {
            // Assumably failed to flush changes to database.
            throw DataAccessUtils.translateIfNecessary(ex, getJpaDialect());
        }
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        JpaTransactionObject txObject = (JpaTransactionObject) status.getTransaction();
        if (status.isDebug()) {
            logger.debug("Rolling back JPA transaction on EntityManager [" +
                    txObject.getEntityManagerHolder().getEntityManager() + "]");
        }
        try {
            EntityTransaction tx = txObject.getEntityManagerHolder().getEntityManager().getTransaction();
            if (tx.isActive()) {
                tx.rollback();
            }
        } catch (PersistenceException ex) {
            throw new TransactionSystemException("Could not roll back JPA transaction", ex);
        } finally {
            if (!txObject.isNewEntityManagerHolder()) {
                // Clear all pending inserts/updates/deletes in the EntityManager.
                // Necessary for pre-bound EntityManagers, to avoid inconsistent state.
                txObject.getEntityManagerHolder().getEntityManager().clear();
            }
        }
    }

    @Override
    protected void doSetRollbackOnly(DefaultTransactionStatus status) {
        JpaTransactionObject txObject = (JpaTransactionObject) status.getTransaction();
        if (status.isDebug()) {
            logger.debug("Setting JPA transaction on EntityManager [" +
                    txObject.getEntityManagerHolder().getEntityManager() + "] rollback-only");
        }
        txObject.setRollbackOnly();
    }

    @Override
    protected void doCleanupAfterCompletion(Object transaction) {
        JpaTransactionObject txObject = (JpaTransactionObject) transaction;

        // Remove the entity manager holder from the thread, if still there.
        // (Could have been removed by EntityManagerFactoryUtils in order
        // to replace it with an unsynchronized EntityManager).
        if (txObject.isNewEntityManagerHolder()) {
            TransactionSynchronizationManager.unbindResourceIfPossible(obtainEntityManagerFactory());
        }
        txObject.getEntityManagerHolder().clear();

        // Remove the JDBC connection holder from the thread, if exposed.
        if (getDataSource() != null && txObject.hasConnectionHolder()) {
            TransactionSynchronizationManager.unbindResource(getDataSource());
            ConnectionHandle conHandle = txObject.getConnectionHolder().getConnectionHandle();
            if (conHandle != null) {
                try {
                    getJpaDialect().releaseJdbcConnection(conHandle,
                            txObject.getEntityManagerHolder().getEntityManager());
                } catch (Throwable ex) {
                    // Just log it, to keep a transaction-related exception.
                    logger.error("Failed to release JDBC connection after transaction", ex);
                }
            }
        }

        getJpaDialect().cleanupTransaction(txObject.getTransactionData());

        // Remove the entity manager holder from the thread.
        if (txObject.isNewEntityManagerHolder()) {
            EntityManager em = txObject.getEntityManagerHolder().getEntityManager();
            if (logger.isDebugEnabled()) {
                logger.debug("Closing JPA EntityManager [" + em + "] after transaction");
            }
            EntityManagerFactoryUtils.closeEntityManager(em);
        } else {
            logger.debug("Not closing pre-bound JPA EntityManager after transaction");
        }
    }

    /**
     * JPA transaction object, representing a EntityManagerHolder.
     * Used as transaction object by JpaTransactionManager.
     */
    public class JpaTransactionObject extends JdbcTransactionObjectSupport {

        @Nullable
        private EntityManagerHolder entityManagerHolder;

        private boolean newEntityManagerHolder;

        @Nullable
        private Object transactionData;

        public void setEntityManagerHolder(
                @Nullable EntityManagerHolder entityManagerHolder, boolean newEntityManagerHolder) {

            this.entityManagerHolder = entityManagerHolder;
            this.newEntityManagerHolder = newEntityManagerHolder;
        }

        public EntityManagerHolder getEntityManagerHolder() {
            Assert.state(this.entityManagerHolder != null, "No EntityManagerHolder available");
            return this.entityManagerHolder;
        }

        public boolean hasEntityManagerHolder() {
            return (this.entityManagerHolder != null);
        }

        public boolean isNewEntityManagerHolder() {
            return this.newEntityManagerHolder;
        }

        public boolean hasTransaction() {
            return (this.entityManagerHolder != null && this.entityManagerHolder.isTransactionActive());
        }

        public void setTransactionData(@Nullable Object transactionData) {
            this.transactionData = transactionData;
            getEntityManagerHolder().setTransactionActive(true);
            if (transactionData instanceof SavepointManager savepointManager) {
                getEntityManagerHolder().setSavepointManager(savepointManager);
            }
        }

        @Nullable
        public Object getTransactionData() {
            return this.transactionData;
        }

        public void setRollbackOnly() {
            EntityTransaction tx = getEntityManagerHolder().getEntityManager().getTransaction();
            if (tx.isActive()) {
                tx.setRollbackOnly();
            }
            if (hasConnectionHolder()) {
                getConnectionHolder().setRollbackOnly();
            }
        }

        @Override
        public boolean isRollbackOnly() {
            EntityTransaction tx = getEntityManagerHolder().getEntityManager().getTransaction();
            return tx.getRollbackOnly();
        }

        @Override
        public void flush() {
            try {
                getEntityManagerHolder().getEntityManager().flush();
            } catch (RuntimeException ex) {
                throw DataAccessUtils.translateIfNecessary(ex, getJpaDialect());
            }
        }

        @Override
        public Object createSavepoint() throws TransactionException {
            if (getEntityManagerHolder().isRollbackOnly()) {
                throw new CannotCreateTransactionException(
                        "Cannot create savepoint for transaction which is already marked as rollback-only");
            }
            return getSavepointManager().createSavepoint();
        }

        @Override
        public void rollbackToSavepoint(Object savepoint) throws TransactionException {
            getSavepointManager().rollbackToSavepoint(savepoint);
            getEntityManagerHolder().resetRollbackOnly();
        }

        @Override
        public void releaseSavepoint(Object savepoint) throws TransactionException {
            getSavepointManager().releaseSavepoint(savepoint);
        }

        private SavepointManager getSavepointManager() {
            if (!isSavepointAllowed()) {
                throw new NestedTransactionNotSupportedException(
                        "Transaction manager does not allow nested transactions");
            }
            SavepointManager savepointManager = getEntityManagerHolder().getSavepointManager();
            if (savepointManager == null) {
                throw new NestedTransactionNotSupportedException(
                        "JpaDialect does not support savepoints - check your JPA provider's capabilities");
            }
            return savepointManager;
        }
    }


    /**
     * JPA-specific transaction definition to be passed to {@link JpaDialect#beginTransaction}.
     *
     * @since 5.1
     */
    private static class JpaTransactionDefinition extends DelegatingTransactionDefinition
            implements ResourceTransactionDefinition {

        private final int timeout;

        private final boolean localResource;

        public JpaTransactionDefinition(TransactionDefinition targetDefinition, int timeout, boolean localResource) {
            super(targetDefinition);
            this.timeout = timeout;
            this.localResource = localResource;
        }

        @Override
        public int getTimeout() {
            return this.timeout;
        }

        @Override
        public boolean isLocalResource() {
            return this.localResource;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            JpaTransactionDefinition that = (JpaTransactionDefinition) o;
            return getTimeout() == that.getTimeout() && isLocalResource() == that.isLocalResource();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(super.hashCode(), getTimeout(), isLocalResource());
        }
    }


    /**
     * Holder for suspended resources.
     * Used internally by {@code doSuspend} and {@code doResume}.
     */
    private static final class SuspendedResourcesHolder {

        private final EntityManagerHolder entityManagerHolder;

        @Nullable
        private final ConnectionHolder connectionHolder;

        private SuspendedResourcesHolder(EntityManagerHolder emHolder, @Nullable ConnectionHolder conHolder) {
            this.entityManagerHolder = emHolder;
            this.connectionHolder = conHolder;
        }

        private EntityManagerHolder getEntityManagerHolder() {
            return this.entityManagerHolder;
        }

        @Nullable
        private ConnectionHolder getConnectionHolder() {
            return this.connectionHolder;
        }
    }

    private static class EntityManagerThreadLocal implements EntityManager {

        private static final ThreadLocal<EntityManager> ENTITY_MANAGER_THREAD_LOCAL = new NamedThreadLocal<>("entityManagerDelegate");

        EntityManagerThreadLocal(EntityManager entityManager) {
            ENTITY_MANAGER_THREAD_LOCAL.set(entityManager);
        }

        @Override
        public void persist(Object entity) {
            ENTITY_MANAGER_THREAD_LOCAL.get().persist(entity);
        }

        @Override
        public <T> T merge(T entity) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().merge(entity);
        }

        @Override
        public void remove(Object entity) {
            ENTITY_MANAGER_THREAD_LOCAL.get().remove(entity);
        }

        @Override
        public <T> T find(Class<T> entityClass, Object primaryKey) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().find(entityClass, primaryKey);
        }

        @Override
        public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().find(entityClass, primaryKey, properties);
        }

        @Override
        public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().find(entityClass, primaryKey, lockMode);
        }

        @Override
        public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().find(entityClass, primaryKey, lockMode, properties);
        }

        @Override
        public <T> T getReference(Class<T> entityClass, Object primaryKey) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().getReference(entityClass, primaryKey);
        }

        @Override
        public void flush() {
            ENTITY_MANAGER_THREAD_LOCAL.get().flush();
        }

        @Override
        public void setFlushMode(FlushModeType flushMode) {
            ENTITY_MANAGER_THREAD_LOCAL.get().setFlushMode(flushMode);
        }

        @Override
        public FlushModeType getFlushMode() {
            return ENTITY_MANAGER_THREAD_LOCAL.get().getFlushMode();
        }

        @Override
        public void lock(Object entity, LockModeType lockMode) {
            ENTITY_MANAGER_THREAD_LOCAL.get().lock(entity, lockMode);
        }

        @Override
        public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
            ENTITY_MANAGER_THREAD_LOCAL.get().lock(entity, lockMode, properties);
        }

        @Override
        public void refresh(Object entity) {
            ENTITY_MANAGER_THREAD_LOCAL.get().refresh(entity);
        }

        @Override
        public void refresh(Object entity, Map<String, Object> properties) {
            ENTITY_MANAGER_THREAD_LOCAL.get().refresh(entity, properties);
        }

        @Override
        public void refresh(Object entity, LockModeType lockMode) {
            ENTITY_MANAGER_THREAD_LOCAL.get().refresh(entity, lockMode);
        }

        @Override
        public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
            ENTITY_MANAGER_THREAD_LOCAL.get().refresh(entity, lockMode, properties);
        }

        @Override
        public void clear() {
            ENTITY_MANAGER_THREAD_LOCAL.get().clear();
        }

        @Override
        public void detach(Object entity) {
            ENTITY_MANAGER_THREAD_LOCAL.get().detach(entity);
        }

        @Override
        public boolean contains(Object entity) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().contains(entity);
        }

        @Override
        public LockModeType getLockMode(Object entity) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().getLockMode(entity);
        }

        @Override
        public void setProperty(String propertyName, Object value) {
            ENTITY_MANAGER_THREAD_LOCAL.get().setProperty(propertyName, value);
        }

        @Override
        public Map<String, Object> getProperties() {
            return ENTITY_MANAGER_THREAD_LOCAL.get().getProperties();
        }

        @Override
        public Query createQuery(String qlString) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createQuery(qlString);
        }

        @Override
        public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createQuery(criteriaQuery);
        }

        @Override
        public Query createQuery(CriteriaUpdate updateQuery) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createQuery(updateQuery);
        }

        @Override
        public Query createQuery(CriteriaDelete deleteQuery) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createQuery(deleteQuery);
        }

        @Override
        public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createQuery(qlString, resultClass);
        }

        @Override
        public Query createNamedQuery(String name) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createNamedQuery(name);
        }

        @Override
        public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createNamedQuery(name, resultClass);
        }

        @Override
        public Query createNativeQuery(String sqlString) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createNativeQuery(sqlString);
        }

        @Override
        public Query createNativeQuery(String sqlString, Class resultClass) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createNativeQuery(sqlString, resultClass);
        }

        @Override
        public Query createNativeQuery(String sqlString, String resultSetMapping) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createNativeQuery(sqlString, resultSetMapping);
        }

        @Override
        public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createNamedStoredProcedureQuery(name);
        }

        @Override
        public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createStoredProcedureQuery(procedureName);
        }

        @Override
        public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createStoredProcedureQuery(procedureName, resultClasses);
        }

        @Override
        public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createStoredProcedureQuery(procedureName, resultSetMappings);
        }

        @Override
        public void joinTransaction() {
            ENTITY_MANAGER_THREAD_LOCAL.get().joinTransaction();
        }

        @Override
        public boolean isJoinedToTransaction() {
            return ENTITY_MANAGER_THREAD_LOCAL.get().isJoinedToTransaction();
        }

        @Override
        public <T> T unwrap(Class<T> cls) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().unwrap(cls);
        }

        @Override
        public Object getDelegate() {
            return ENTITY_MANAGER_THREAD_LOCAL.get().getDelegate();
        }

        @Override
        public void close() {
            ENTITY_MANAGER_THREAD_LOCAL.get().close();
            ENTITY_MANAGER_THREAD_LOCAL.remove();
        }

        @Override
        public boolean isOpen() {
            return ENTITY_MANAGER_THREAD_LOCAL.get() != null && ENTITY_MANAGER_THREAD_LOCAL.get().isOpen();
        }

        @Override
        public EntityTransaction getTransaction() {
            return ENTITY_MANAGER_THREAD_LOCAL.get().getTransaction();
        }

        @Override
        public EntityManagerFactory getEntityManagerFactory() {
            return ENTITY_MANAGER_THREAD_LOCAL.get().getEntityManagerFactory();
        }

        @Override
        public CriteriaBuilder getCriteriaBuilder() {
            return ENTITY_MANAGER_THREAD_LOCAL.get().getCriteriaBuilder();
        }

        @Override
        public Metamodel getMetamodel() {
            return ENTITY_MANAGER_THREAD_LOCAL.get().getMetamodel();
        }

        @Override
        public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createEntityGraph(rootType);
        }

        @Override
        public EntityGraph<?> createEntityGraph(String graphName) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().createEntityGraph(graphName);
        }

        @Override
        public EntityGraph<?> getEntityGraph(String graphName) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().getEntityGraph(graphName);
        }

        @Override
        public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
            return ENTITY_MANAGER_THREAD_LOCAL.get().getEntityGraphs(entityClass);
        }
    }
}
