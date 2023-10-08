package com.bachlinh.order.entity.repository;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;
import org.springframework.core.NamedThreadLocal;

import java.util.List;
import java.util.Map;

class InjectableEntityManager implements EntityManager, EntityManagerProxyOperator {
    private static final ThreadLocal<EntityManager> ENTITY_MANAGER_THREAD_LOCAL = new NamedThreadLocal<>("EntityManager");

    public InjectableEntityManager() {
        this(null);
    }

    public InjectableEntityManager(EntityManager entityManager) {
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

    @Override
    public void assignEntityManager(EntityManager otherEntityManager) {
        ENTITY_MANAGER_THREAD_LOCAL.set(otherEntityManager);
    }

    @Override
    public void releaseEntityManager() {
        ENTITY_MANAGER_THREAD_LOCAL.remove();
    }
}
