package com.bachlinh.order.entity.repository;

import com.bachlinh.order.annotation.QueryCache;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityManagerHolder;
import com.bachlinh.order.entity.EntityTrigger;
import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.repository.cache.Cache;
import com.bachlinh.order.entity.repository.cache.CacheManager;
import com.bachlinh.order.entity.repository.cache.CacheReader;
import com.bachlinh.order.entity.repository.cache.CacheWriter;
import com.bachlinh.order.entity.repository.query.CacheableQuery;
import com.bachlinh.order.entity.repository.query.FunctionDialect;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.ResultListProcessing;
import com.bachlinh.order.entity.repository.query.Select;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlUpdate;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.repository.utils.QueryUtils;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.http.ValidationFailureException;
import com.bachlinh.order.service.container.DependenciesResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import org.hibernate.SessionFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public abstract class AbstractRepository<T, U extends BaseEntity<T>> implements EntityManagerHolder, NativeQueryRepository, CrudRepository<T, U> {

    private static final String ID_PROPERTY = "id";

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
        Environment environment = Environment.getInstance(Environment.getMainEnvironmentName());
        this.functionDialect = FunctionDialect.getDialect(environment.getProperty("server.database.driver"));
    }

    @Override
    public EntityManager getEntityManager() {
        boolean springActualTransactionActive = entityFactory.getTransactionManager().isActualTransactionActive();
        if (springActualTransactionActive) {
            return em;
        } else {
            return sessionFactory.createEntityManager();
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
            // FIXME add custom type mapping
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
    @Transactional
    public <S extends U> S save(S entity) {
        if (entity.isNew()) {
            EntityManager entityManager = getEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();

            Supplier<S> task = getSaveSupplier(entity, entityManager);

            if (entityTransaction.isActive()) {
                EntityContext entityContext = entityFactory.getEntityContext(getDomainClass());
                try {
                    entityContext.beginTransaction();
                    return task.get();
                } catch (Exception e) {
                    entityContext.rollback();
                    throw new PersistenceException(e);
                }
            } else {
                return doInTransaction(entityManager.getTransaction(), task, entityManager::flush);
            }
        } else {
            return update(entity);
        }
    }

    @Override
    @Transactional
    public <S extends U> S update(S entity) {
        if (entity.isNew()) {
            return save(entity);
        } else {
            EntityManager entityManager = getEntityManager();
            EntityTransaction entityTransaction = entityManager.getTransaction();

            Supplier<S> task = getUpdateSupplier(entity, entityManager);

            if (entityTransaction.isActive()) {
                return task.get();
            } else {
                return doInTransaction(entityManager.getTransaction(), task, entityManager::flush);
            }
        }
    }

    @Override
    @Transactional
    public <S extends U> Collection<S> saveAll(Iterable<S> entities) {
        Collection<S> results = new LinkedList<>();
        entities.forEach(entity -> results.add(save(entity)));
        return results;
    }

    @Override
    @Transactional
    public <S extends U> Collection<S> updateAll(Iterable<S> entities) {
        Collection<S> results = new LinkedList<>();
        entities.forEach(entity -> results.add(update(entity)));
        return results;
    }

    @Override
    public boolean exists(T id) {
        return false;
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
    @Transactional
    public void deleteById(T id) {
        U target = loadCustomerForDelete(id);

        delete(target);
    }

    @Override
    @Transactional
    public void delete(U entity) {
        EntityManager entityManager = getEntityManager();
        EntityTransaction entityTransaction = entityManager.getTransaction();
        EntityContext entityContext = entityFactory.getEntityContext(getDomainClass());
        Collection<EntityTrigger<U>> beforeDeleteTriggers = getBeforeDeleteTriggers();
        Collection<EntityTrigger<U>> afterDeleteTriggers = getAfterDeleteTriggers();

        Supplier<U> task = () -> {
            String deleteQuery = getDeleteQuery(entityContext.getTableName());
            Query query = entityManager.createNativeQuery(deleteQuery);
            query.setParameter(ID_PROPERTY, entity.getId());

            beforeDeleteTriggers.forEach(trigger -> trigger.execute(entity));
            query.executeUpdate();
            afterDeleteTriggers.forEach(trigger -> trigger.execute(entity));
            return null;
        };

        if (entityTransaction.isActive()) {
            task.get();
        } else {
            doInTransaction(entityTransaction, task, null);
        }

        evictCache();
    }

    @Override
    @Transactional
    public void deleteAllById(Iterable<? extends T> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    @Transactional
    public void deleteAll(Iterable<? extends U> entities) {
        entities.forEach(this::delete);
    }

    @Override
    @Transactional
    public void deleteAll() {

        @SuppressWarnings("unchecked")
        Class<? extends AbstractEntity<?>> table = (Class<? extends AbstractEntity<?>>) getDomainClass();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(table);

        String query = sqlSelect.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlSelect.getQueryBindings());

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

    protected void setEntityManager(EntityManager entityManager) {
        this.em = entityManager;
    }

    protected void doInTransaction(EntityManager entityManager, TransactionCallback callback) {
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
        if (results == null) {
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
        var typedQuery = getEntityManager().createNativeQuery(query, Tuple.class).unwrap(org.hibernate.query.Query.class);
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

    private <S extends U> S doInTransaction(EntityTransaction transaction, Supplier<S> task, @Nullable TransactionCallback callback) {
        EntityContext entityContext = entityFactory.getEntityContext(getDomainClass());
        entityContext.beginTransaction();
        transaction.begin();
        try {
            S result = task.get();
            if (callback != null) {
                callback.execute();
            }
            transaction.commit();
            entityContext.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            entityContext.rollback();
            throw new PersistenceException(e);
        }
    }

    private String getDeleteQuery(String tableName) {
        String template = "DELETE FROM {0} WHERE ID = :" + ID_PROPERTY;
        return MessageFormat.format(template, tableName);
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

    @SuppressWarnings("unchecked")
    private <S extends U> Collection<EntityTrigger<S>> getBeforeTriggers() {
        EntityContext entityContext = entityFactory.getEntityContext(getDomainClass());
        Collection<EntityTrigger<S>> triggers = new LinkedList<>();

        entityContext.getTrigger().forEach(trigger -> {
            if (trigger.getMode().equals(TriggerMode.BEFORE)) {
                triggers.add((EntityTrigger<S>) trigger);
            }
        });

        return triggers;
    }

    @SuppressWarnings("unchecked")
    private <S extends U> Collection<EntityTrigger<S>> getAfterTriggers() {
        EntityContext entityContext = entityFactory.getEntityContext(getDomainClass());
        Collection<EntityTrigger<S>> triggers = new LinkedList<>();

        entityContext.getTrigger().forEach(trigger -> {
            if (trigger.getMode().equals(TriggerMode.AFTER)) {
                triggers.add((EntityTrigger<S>) trigger);
            }
        });

        return triggers;
    }

    private <S extends U> Collection<EntityTrigger<S>> getBeforeSaveTriggers() {
        Collection<EntityTrigger<S>> triggers = getBeforeTriggers();
        return triggers.stream()
                .filter(entityTrigger -> Arrays.asList(entityTrigger.getExecuteOn()).contains(TriggerExecution.ON_INSERT))
                .toList();
    }

    private <S extends U> Collection<EntityTrigger<S>> getAfterSaveTriggers() {
        Collection<EntityTrigger<S>> triggers = getAfterTriggers();
        return triggers.stream()
                .filter(entityTrigger -> Arrays.asList(entityTrigger.getExecuteOn()).contains(TriggerExecution.ON_INSERT))
                .toList();
    }

    private <S extends U> Collection<EntityTrigger<S>> getBeforeUpdateTriggers() {
        Collection<EntityTrigger<S>> triggers = getBeforeTriggers();
        return triggers.stream()
                .filter(entityTrigger -> Arrays.asList(entityTrigger.getExecuteOn()).contains(TriggerExecution.ON_UPDATE))
                .toList();
    }

    private <S extends U> Collection<EntityTrigger<S>> getAfterUpdateTriggers() {
        Collection<EntityTrigger<S>> triggers = getAfterTriggers();
        return triggers.stream()
                .filter(entityTrigger -> Arrays.asList(entityTrigger.getExecuteOn()).contains(TriggerExecution.ON_UPDATE))
                .toList();
    }

    private <S extends U> Collection<EntityTrigger<S>> getAfterDeleteTriggers() {
        Collection<EntityTrigger<S>> triggers = getAfterTriggers();
        return triggers.stream()
                .filter(entityTrigger -> Arrays.asList(entityTrigger.getExecuteOn()).contains(TriggerExecution.ON_DELETE))
                .toList();
    }

    private <S extends U> Collection<EntityTrigger<S>> getBeforeDeleteTriggers() {
        Collection<EntityTrigger<S>> triggers = getBeforeTriggers();
        return triggers.stream()
                .filter(entityTrigger -> Arrays.asList(entityTrigger.getExecuteOn()).contains(TriggerExecution.ON_DELETE))
                .toList();
    }

    @NonNull
    private <S extends U> Supplier<S> getSaveSupplier(S entity, EntityManager entityManager) {
        Collection<EntityValidator<S>> validators = getValidators();
        Collection<EntityTrigger<S>> onSaveBefore = getBeforeSaveTriggers();
        Collection<EntityTrigger<S>> onSaveAfter = getAfterSaveTriggers();

        return () -> {
            onSaveBefore.forEach(beforeSave -> beforeSave.execute(entity));
            doValidate(validators, entity);
            entityManager.persist(entity);
            onSaveAfter.forEach(afterSave -> afterSave.execute(entity));
            return entity;
        };
    }

    @NonNull
    private <S extends U> Supplier<S> getUpdateSupplier(S entity, EntityManager entityManager) {
        Collection<EntityValidator<S>> validators = getValidators();
        Collection<EntityTrigger<S>> onUpdateBefore = getBeforeUpdateTriggers();
        Collection<EntityTrigger<S>> onUpdateAfter = getAfterUpdateTriggers();

        return () -> {
            onUpdateBefore.forEach(trigger -> trigger.execute(entity));
            doValidate(validators, entity);
            doUpdate(entity, entityManager);
            onUpdateAfter.forEach(trigger -> trigger.execute(entity));
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

    protected interface TransactionCallback {
        void execute();
    }
}
