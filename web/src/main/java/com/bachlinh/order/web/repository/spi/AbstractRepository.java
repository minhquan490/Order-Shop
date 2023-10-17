package com.bachlinh.order.web.repository.spi;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;
import org.hibernate.SessionFactory;

import com.bachlinh.order.core.annotation.QueryCache;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.enums.TriggerExecution;
import com.bachlinh.order.core.enums.TriggerMode;
import com.bachlinh.order.core.environment.Environment;
import com.bachlinh.order.core.exception.http.ValidationFailureException;
import com.bachlinh.order.core.function.VoidCallback;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityManagerHolder;
import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.repository.CrudRepository;
import com.bachlinh.order.repository.NativeQueryRepository;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.repository.cache.Cache;
import com.bachlinh.order.repository.cache.CacheManager;
import com.bachlinh.order.repository.cache.CacheReader;
import com.bachlinh.order.repository.cache.CacheWriter;
import com.bachlinh.order.repository.query.CacheableQuery;
import com.bachlinh.order.repository.query.FunctionDialect;
import com.bachlinh.order.repository.query.Operation;
import com.bachlinh.order.repository.query.ResultListProcessing;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlDelete;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlUpdate;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.entity.transaction.spi.TransactionHolder;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.trigger.EntityTriggerManager;
import com.bachlinh.order.trigger.EntityTriggerManagerHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public abstract class AbstractRepository<T, U extends BaseEntity<T>> implements EntityManagerHolder, NativeQueryRepository, CrudRepository<T, U>, RepositoryBase {

    private static final String ID_PROPERTY = QueryUtils.getIdProperty();

    private final Class<U> domainClass;
    private final SessionFactory sessionFactory;
    private final EntityFactory entityFactory;
    private final CacheManager<?> cacheManager;
    private final boolean isUseQueryCache;
    private final SqlBuilder builder;
    private final FunctionDialect functionDialect;

    private EntityManager em;

    @SuppressWarnings("unchecked")
    protected AbstractRepository(Class<? extends BaseEntity<?>> domainClass, DependenciesResolver resolver) {
        this.domainClass = (Class<U>) domainClass;
        this.sessionFactory = resolver.resolveDependencies(SessionFactory.class);
        this.entityFactory = resolver.resolveDependencies(EntityFactory.class);
        this.cacheManager = resolver.resolveDependencies(CacheManager.class);
        this.isUseQueryCache = domainClass.isAnnotationPresent(QueryCache.class);
        this.builder = resolver.resolveDependencies(SqlBuilder.class);
        this.functionDialect = createFunctionDialect();
    }

    @Override
    public EntityManager getEntityManager() {
        boolean springActualTransactionActive = entityFactory.getTransactionManager().isActualTransactionActive();
        if (springActualTransactionActive && em != null && em.isOpen()) {
            return em;
        } else {
            return new OutboundThreadEntityManager(sessionFactory.createEntityManager());
        }
    }

    @Override
    public <K> List<K> getResultList(String query, Map<String, Object> attributes, Class<K> receiverType) {
        return getResultList(query, attributes, receiverType, false);
    }

    @Override
    public <K> List<K> getResultList(String query, Map<String, Object> attributes, Class<K> receiverType, boolean forceCache) {
        if (getDomainClass().isAssignableFrom(receiverType)) {
            if (isUseQueryCache && !forceCache) {
                return getDataInCache(query, attributes, receiverType);
            }
            return queryDatabase(query, attributes, receiverType);
        } else {
            return getResultListWithJavaType(query, attributes, receiverType);
        }
    }

    @Override
    public <K> K getSingleResult(String query, Map<String, Object> attributes, Class<K> receiverType) {
        return getSingleResult(query, attributes, receiverType, false);
    }

    @Override
    public <K> K getSingleResult(String query, Map<String, Object> attributes, Class<K> receiverType, boolean forceCache) {
        List<K> results = getResultList(query, attributes, receiverType, forceCache);
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    @Override
    public <S extends U> S save(S entity) {
        if (entity.isNew()) {
            TransactionHolder<?> transactionHolder = entityFactory.getTransactionManager().getCurrentTransaction();

            if (transactionHolder != null && transactionHolder.isActive()) {
                Supplier<S> task = getSaveSupplier(entity, getEntityManager());
                EntityContext entityContext = entityFactory.getEntityContext(getDomainClass());
                try {
                    entityContext.beginTransaction();
                    S result = task.get();
                    entityContext.commit();
                    return result;
                } catch (Exception e) {
                    entityContext.rollback();
                    throw new PersistenceException(e);
                }
            } else {
                EntityManager entityManager = getEntityManager();
                Supplier<S> task = getSaveSupplier(entity, entityManager);
                return execute(entityManager, task);
            }
        } else {
            return update(entity);
        }
    }

    @Override
    public <S extends U> S update(S entity) {
        if (entity.isNew()) {
            return save(entity);
        } else {
            TransactionHolder<?> transactionHolder = entityFactory.getTransactionManager().getCurrentTransaction();
            Supplier<S> task = getUpdateSupplier(entity, getEntityManager());

            if (transactionHolder.isActive()) {
                return task.get();
            } else {
                EntityManager entityManager = getEntityManager();
                return execute(entityManager, task);
            }
        }
    }

    @Override
    public <S extends U> Collection<S> saveAll(Iterable<S> entities) {
        Collection<S> results = new LinkedList<>();
        entities.forEach(entity -> results.add(save(entity)));
        return results;
    }

    @Override
    public <S extends U> Collection<S> updateAll(Iterable<S> entities) {
        Collection<S> results = new LinkedList<>();
        entities.forEach(entity -> results.add(update(entity)));
        return results;
    }

    @Override
    public boolean exists(T id) {
        Where idWhere = Where.builder().attribute(ID_PROPERTY).value(id).operation(Operation.EQ).build();
        return count(idWhere) > 0;
    }

    @Override
    public long count() {
        SqlSelect countSelect = getCountSelect();

        String sql = countSelect.getNativeQuery();

        return getSingleResult(sql, Collections.emptyMap(), Long.class);
    }

    @Override
    public long count(Where... conditions) {

        SqlSelect sqlSelect = getCountSelect();
        SqlWhere sqlWhere = null;
        for (Where condition : conditions) {
            if (sqlWhere == null) {
                sqlWhere = sqlSelect.where(condition);
            } else {
                sqlWhere.where(condition);
            }
        }

        String query;
        Map<String, Object> attributes = new HashMap<>();
        if (sqlWhere != null) {
            query = sqlWhere.getNativeQuery();
            attributes.putAll(QueryUtils.parse(sqlWhere.getQueryBindings()));
        } else {
            query = sqlSelect.getNativeQuery();
        }

        EntityManager entityManager = getEntityManager();
        Query q = entityManager.createNativeQuery(query, Long.class);
        attributes.forEach(q::setParameter);

        return (Long) q.getSingleResult();
    }

    @Override
    public void deleteById(T id) {
        U target = loadCustomerForDelete(id);

        delete(target);
    }

    @Override
    public void delete(U entity) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        Supplier<U> task = getDeleteTask(entity, entityManager);

        if (entityTransaction.isActive()) {
            task.get();
        } else {
            doInTransaction(entityTransaction, task, unused -> closeEntityManager(entityManager));
        }
        evictCache();
    }

    private Supplier<U> getDeleteTask(U entity, EntityManager entityManager) {
        EntityTriggerManager entityTriggerManager = ((EntityTriggerManagerHolder) entityFactory.getEntityContext(getDomainClass())).getEntityTriggerManager();

        return () -> {
            @SuppressWarnings("unchecked")
            Class<? extends AbstractEntity<?>> table = (Class<? extends AbstractEntity<?>>) getDomainClass();

            SqlBuilder queryBuilder = getSqlBuilder();
            SqlDelete sqlDelete = queryBuilder.deleteQueryFor(table);
            sqlDelete.delete((AbstractEntity<?>) entity);
            String deleteQuery = sqlDelete.getNativeQuery();

            Query query = entityManager.createNativeQuery(deleteQuery);
            for (var param : sqlDelete.getQueryBindings()) {
                query.setParameter(param.attribute(), param.value());
            }

            entityTriggerManager.executeTrigger(entity, TriggerMode.BEFORE, TriggerExecution.ON_DELETE);
            query.executeUpdate();
            entityTriggerManager.executeTrigger(entity, TriggerMode.AFTER, TriggerExecution.ON_DELETE);

            // Skip result so return null
            return null;
        };
    }

    @Override
    public void deleteAllById(Iterable<? extends T> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends U> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {

        @SuppressWarnings("unchecked")
        Class<? extends AbstractEntity<?>> table = (Class<? extends AbstractEntity<?>>) getDomainClass();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(table);

        String query = sqlSelect.getNativeQuery();
        Map<String, Object> attributes = Collections.emptyMap();

        List<U> results = getResultList(query, attributes, getDomainClass(), true);

        // Use for loop to delete for all trigger can be executed
        for (U result : results) {
            delete(result);
        }

        evictCache();
    }

    protected EntityFactory getEntityFactory() {
        return this.entityFactory;
    }

    protected Class<U> getDomainClass() {
        return this.domainClass;
    }

    protected SqlBuilder getSqlBuilder() {
        return this.builder;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.em = entityManager;
    }

    protected void doInTransaction(EntityManager entityManager, VoidCallback callback) {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        entityTransaction.begin();
        try {
            callback.execute();
            entityTransaction.commit();
        } catch (Exception e) {
            entityTransaction.rollback();
            throw e;
        }
    }

    protected FunctionDialect getFunctionDialect() {
        return this.functionDialect;
    }

    @SuppressWarnings("unchecked")
    private <K> List<K> getDataInCache(String query, Map<String, Object> attributes, Class<K> receiverType) {
        String alias = QueryUtils.parseQueryToEntitySimpleName(query);
        String processedQuery = QueryUtils.bindAttributes(query, attributes);
        Collection<AbstractEntity<?>> results = ((CacheReader<String, Collection<AbstractEntity<?>>>) cacheManager).readCache(alias, processedQuery);
        if (results == null || results.isEmpty()) {
            List<K> data = queryDatabase(query, attributes, receiverType);
            CacheableQuery cacheableQuery = new CacheableQuery(query, attributes);
            if (!data.isEmpty()) {
                ((CacheWriter<CacheableQuery>) cacheManager).write(cacheableQuery, data);
            }
            return data;
        } else {
            return results.stream().map(receiverType::cast).toList();
        }
    }

    private <K> List<K> queryDatabase(String query, Map<String, Object> attributes, Class<K> receiverType) {
        EntityManager entityManager = getEntityManager();
        var typedQuery = entityManager.createNativeQuery(query, Tuple.class).unwrap(org.hibernate.query.Query.class);
        attributes.forEach(typedQuery::setParameter);
        var processing = ResultListProcessing.nativeProcessing(getEntityFactory(), getDomainClass());
        var results = processing.process(typedQuery::getResultList);
        return results.stream().map(receiverType::cast).toList();
    }

    @SuppressWarnings("unchecked")
    private <K> List<K> getResultListWithJavaType(String query, Map<String, Object> attributes, Class<K> receiverType) {
        var typedQuery = getEntityManager().createNativeQuery(query, receiverType).unwrap(org.hibernate.query.Query.class);
        attributes.forEach(typedQuery::setParameter);
        var results = typedQuery.getResultList();
        return results.stream().map(receiverType::cast).toList();
    }

    private <S extends U> S doInTransaction(EntityTransaction transaction, Supplier<S> task, @Nullable VoidCallback callback) {
        EntityContext entityContext = entityFactory.getEntityContext(getDomainClass());
        entityContext.beginTransaction();
        transaction.begin();
        try {
            S result = task.get();
            transaction.commit();
            entityContext.commit();
            if (callback != null) {
                callback.execute();
            }
            return result;
        } catch (Exception e) {
            transaction.rollback();
            entityContext.rollback();
            throw new PersistenceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <S extends U> Collection<EntityValidator<S>> getValidators() {
        EntityContext entityContext = entityFactory.getEntityContext(getDomainClass());
        Collection<EntityValidator<S>> validators = new LinkedList<>();

        entityContext.getValidators().forEach(validator -> validators.add((EntityValidator<S>) validator));
        return validators;
    }

    private <S extends U> void doValidate(Collection<EntityValidator<S>> validators, S entity) {
        Set<String> errors = new HashSet<>();
        validators.forEach(validator -> {
            ValidateResult result = validator.validate(entity);
            if (result.hasError()) {
                errors.addAll(result.getMessages());
            }
        });
        if (!errors.isEmpty()) {
            throw new ValidationFailureException(errors, String.format("Fail when validate entity [%s]", entity.getClass()));
        }
    }

    @NonNull
    private <S extends U> Supplier<S> getSaveSupplier(S entity, EntityManager entityManager) {
        Collection<EntityValidator<S>> validators = getValidators();

        EntityTriggerManager entityTriggerManager = ((EntityTriggerManagerHolder) entityFactory.getEntityContext(getDomainClass())).getEntityTriggerManager();

        return () -> {
            entityTriggerManager.executeTrigger(entity, TriggerMode.BEFORE, TriggerExecution.ON_INSERT);
            doValidate(validators, entity);
            entityManager.persist(entity);
            entityTriggerManager.executeTrigger(entity, TriggerMode.AFTER, TriggerExecution.ON_INSERT);
            return entity;
        };
    }

    @NonNull
    private <S extends U> Supplier<S> getUpdateSupplier(S entity, EntityManager entityManager) {
        Collection<EntityValidator<S>> validators = getValidators();

        EntityTriggerManager entityTriggerManager = ((EntityTriggerManagerHolder) entityFactory.getEntityContext(getDomainClass())).getEntityTriggerManager();

        return () -> {
            entityTriggerManager.executeTrigger(entity, TriggerMode.BEFORE, TriggerExecution.ON_UPDATE);
            doValidate(validators, entity);
            doUpdate(entity, entityManager);
            entityTriggerManager.executeTrigger(entity, TriggerMode.AFTER, TriggerExecution.ON_UPDATE);
            return entity;
        };
    }

    private <S extends U> void doUpdate(S entity, EntityManager entityManager) {
        @SuppressWarnings("unchecked")
        Class<? extends AbstractEntity<?>> tableType = (Class<? extends AbstractEntity<?>>) getDomainClass();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlUpdate sqlUpdate = sqlBuilder.updateQueryFor(tableType);
        sqlUpdate.update((AbstractEntity<?>) entity);
        var query = entityManager.createNativeQuery(sqlUpdate.getNativeQuery());
        Map<String, Object> params = QueryUtils.parse(sqlUpdate.getQueryBindings());
        params.forEach(query::setParameter);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    private U loadCustomerForDelete(T id) {

        Where idWhere = Where.builder().attribute(ID_PROPERTY).value(id).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        Class<? extends AbstractEntity<?>> table = (Class<? extends AbstractEntity<?>>) getDomainClass();
        SqlSelect sqlSelect = sqlBuilder.from(table);
        SqlWhere sqlWhere = sqlSelect.where(idWhere);

        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(sql, attributes, getDomainClass(), true);
    }

    private void evictCache() {
        if (isUseQueryCache) {
            String alias = getDomainClass().getSimpleName();
            Cache cache = cacheManager.getCache(alias);
            cache.clear();
        }
    }

    private SqlSelect getCountSelect() {
        SqlBuilder sqlBuilder = getSqlBuilder();
        Select idSelect = Select.builder().column(ID_PROPERTY).build();

        @SuppressWarnings("unchecked")
        Class<? extends AbstractEntity<?>> table = (Class<? extends AbstractEntity<?>>) getDomainClass();

        SqlSelect sqlSelect = sqlBuilder.from(table);
        sqlSelect.select(idSelect, functionDialect.count());

        return sqlSelect;
    }

    private <S extends U> S execute(EntityManager entityManager, Supplier<S> task) {
        return doInTransaction(entityManager.getTransaction(), task, unused -> closeEntityManager(entityManager));
    }

    private void closeEntityManager(EntityManager entityManager) {
        if (entityManager instanceof OutboundThreadEntityManager proxy) {
            proxy.close();
        }
    }

    private FunctionDialect createFunctionDialect() {
        Environment environment = Environment.getInstance(Environment.getMainEnvironmentName());
        return FunctionDialect.getDialect(environment.getProperty("server.database.driver"));
    }

    private record OutboundThreadEntityManager(EntityManager delegate) implements EntityManager {

        @Override
        public void persist(Object entity) {
            delegate.persist(entity);
        }

        @Override
        public <T> T merge(T entity) {
            return delegate.merge(entity);
        }

        @Override
        public void remove(Object entity) {
            delegate.remove(entity);
        }

        @Override
        public <T> T find(Class<T> entityClass, Object primaryKey) {
            return delegate.find(entityClass, primaryKey);
        }

        @Override
        public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
            return delegate.find(entityClass, primaryKey, properties);
        }

        @Override
        public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
            return delegate.find(entityClass, primaryKey, lockMode);
        }

        @Override
        public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
            return delegate.find(entityClass, primaryKey, lockMode, properties);
        }

        @Override
        public <T> T getReference(Class<T> entityClass, Object primaryKey) {
            return delegate.getReference(entityClass, primaryKey);
        }

        @Override
        public void flush() {
            delegate.flush();
        }

        @Override
        public void setFlushMode(FlushModeType flushMode) {
            delegate.setFlushMode(flushMode);
        }

        @Override
        public FlushModeType getFlushMode() {
            return delegate.getFlushMode();
        }

        @Override
        public void lock(Object entity, LockModeType lockMode) {
            delegate.lock(entity, lockMode);
        }

        @Override
        public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
            delegate.lock(entity, lockMode, properties);
        }

        @Override
        public void refresh(Object entity) {
            delegate.refresh(entity);
        }

        @Override
        public void refresh(Object entity, Map<String, Object> properties) {
            delegate.refresh(entity, properties);
        }

        @Override
        public void refresh(Object entity, LockModeType lockMode) {
            delegate.refresh(entity, lockMode);
        }

        @Override
        public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
            delegate.refresh(entity, lockMode, properties);
        }

        @Override
        public void clear() {
            delegate.clear();
        }

        @Override
        public void detach(Object entity) {
            delegate.detach(entity);
        }

        @Override
        public boolean contains(Object entity) {
            return delegate.contains(entity);
        }

        @Override
        public LockModeType getLockMode(Object entity) {
            return delegate.getLockMode(entity);
        }

        @Override
        public void setProperty(String propertyName, Object value) {
            delegate.setProperty(propertyName, value);
        }

        @Override
        public Map<String, Object> getProperties() {
            return delegate.getProperties();
        }

        @Override
        public Query createQuery(String qlString) {
            return delegate.createQuery(qlString);
        }

        @Override
        public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
            return delegate.createQuery(criteriaQuery);
        }

        @Override
        public Query createQuery(CriteriaUpdate updateQuery) {
            return delegate.createQuery(updateQuery);
        }

        @Override
        public Query createQuery(CriteriaDelete deleteQuery) {
            return delegate.createQuery(deleteQuery);
        }

        @Override
        public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
            return delegate.createQuery(qlString, resultClass);
        }

        @Override
        public Query createNamedQuery(String name) {
            return delegate.createNamedQuery(name);
        }

        @Override
        public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
            return delegate.createNamedQuery(name, resultClass);
        }

        @Override
        public Query createNativeQuery(String sqlString) {
            return delegate.createNativeQuery(sqlString);
        }

        @Override
        public Query createNativeQuery(String sqlString, Class resultClass) {
            return delegate.createNativeQuery(sqlString, resultClass);
        }

        @Override
        public Query createNativeQuery(String sqlString, String resultSetMapping) {
            return delegate.createNativeQuery(sqlString, resultSetMapping);
        }

        @Override
        public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
            return delegate.createNamedStoredProcedureQuery(name);
        }

        @Override
        public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
            return delegate.createStoredProcedureQuery(procedureName);
        }

        @Override
        public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
            return delegate.createStoredProcedureQuery(procedureName, resultClasses);
        }

        @Override
        public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
            return delegate.createStoredProcedureQuery(procedureName, resultSetMappings);
        }

        @Override
        public void joinTransaction() {
            delegate.joinTransaction();
        }

        @Override
        public boolean isJoinedToTransaction() {
            return delegate.isJoinedToTransaction();
        }

        @Override
        public <T> T unwrap(Class<T> cls) {
            return delegate.unwrap(cls);
        }

        @Override
        public Object getDelegate() {
            return delegate.getDelegate();
        }

        @Override
        public void close() {
            delegate.close();
        }

        @Override
        public boolean isOpen() {
            return delegate.isOpen();
        }

        @Override
        public EntityTransaction getTransaction() {
            return delegate.getTransaction();
        }

        @Override
        public EntityManagerFactory getEntityManagerFactory() {
            return delegate.getEntityManagerFactory();
        }

        @Override
        public CriteriaBuilder getCriteriaBuilder() {
            return delegate.getCriteriaBuilder();
        }

        @Override
        public Metamodel getMetamodel() {
            return delegate.getMetamodel();
        }

        @Override
        public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
            return delegate.createEntityGraph(rootType);
        }

        @Override
        public EntityGraph<?> createEntityGraph(String graphName) {
            return delegate.createEntityGraph(graphName);
        }

        @Override
        public EntityGraph<?> getEntityGraph(String graphName) {
            return delegate.getEntityGraph(graphName);
        }

        @Override
        public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
            return delegate.getEntityGraphs(entityClass);
        }
    }
}
