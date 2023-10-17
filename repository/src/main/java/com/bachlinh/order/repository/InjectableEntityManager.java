package com.bachlinh.order.repository;

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
    private final ThreadLocal<EntityManager> entityManagerNamedThreadLocal = new NamedThreadLocal<>("EntityManager");

    public InjectableEntityManager() {
        this(null);
    }

    public InjectableEntityManager(EntityManager entityManager) {
        entityManagerNamedThreadLocal.set(entityManager);
    }

    @Override
    public void persist(Object entity) {
        entityManagerNamedThreadLocal.get().persist(entity);
    }

    @Override
    public <T> T merge(T entity) {
        return entityManagerNamedThreadLocal.get().merge(entity);
    }

    @Override
    public void remove(Object entity) {
        entityManagerNamedThreadLocal.get().remove(entity);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return entityManagerNamedThreadLocal.get().find(entityClass, primaryKey);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return entityManagerNamedThreadLocal.get().find(entityClass, primaryKey, properties);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        return entityManagerNamedThreadLocal.get().find(entityClass, primaryKey, lockMode);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        return entityManagerNamedThreadLocal.get().find(entityClass, primaryKey, lockMode, properties);
    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return entityManagerNamedThreadLocal.get().getReference(entityClass, primaryKey);
    }

    @Override
    public void flush() {
        entityManagerNamedThreadLocal.get().flush();
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {
        entityManagerNamedThreadLocal.get().setFlushMode(flushMode);
    }

    @Override
    public FlushModeType getFlushMode() {
        return entityManagerNamedThreadLocal.get().getFlushMode();
    }

    @Override
    public void lock(Object entity, LockModeType lockMode) {
        entityManagerNamedThreadLocal.get().lock(entity, lockMode);
    }

    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        entityManagerNamedThreadLocal.get().lock(entity, lockMode, properties);
    }

    @Override
    public void refresh(Object entity) {
        entityManagerNamedThreadLocal.get().refresh(entity);
    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties) {
        entityManagerNamedThreadLocal.get().refresh(entity, properties);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode) {
        entityManagerNamedThreadLocal.get().refresh(entity, lockMode);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
        entityManagerNamedThreadLocal.get().refresh(entity, lockMode, properties);
    }

    @Override
    public void clear() {
        entityManagerNamedThreadLocal.get().clear();
    }

    @Override
    public void detach(Object entity) {
        entityManagerNamedThreadLocal.get().detach(entity);
    }

    @Override
    public boolean contains(Object entity) {
        return entityManagerNamedThreadLocal.get().contains(entity);
    }

    @Override
    public LockModeType getLockMode(Object entity) {
        return entityManagerNamedThreadLocal.get().getLockMode(entity);
    }

    @Override
    public void setProperty(String propertyName, Object value) {
        entityManagerNamedThreadLocal.get().setProperty(propertyName, value);
    }

    @Override
    public Map<String, Object> getProperties() {
        return entityManagerNamedThreadLocal.get().getProperties();
    }

    @Override
    public Query createQuery(String qlString) {
        return entityManagerNamedThreadLocal.get().createQuery(qlString);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return entityManagerNamedThreadLocal.get().createQuery(criteriaQuery);
    }

    @Override
    public Query createQuery(CriteriaUpdate updateQuery) {
        return entityManagerNamedThreadLocal.get().createQuery(updateQuery);
    }

    @Override
    public Query createQuery(CriteriaDelete deleteQuery) {
        return entityManagerNamedThreadLocal.get().createQuery(deleteQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return entityManagerNamedThreadLocal.get().createQuery(qlString, resultClass);
    }

    @Override
    public Query createNamedQuery(String name) {
        return entityManagerNamedThreadLocal.get().createNamedQuery(name);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        return entityManagerNamedThreadLocal.get().createNamedQuery(name, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString) {
        return entityManagerNamedThreadLocal.get().createNativeQuery(sqlString);
    }

    @Override
    public Query createNativeQuery(String sqlString, Class resultClass) {
        return entityManagerNamedThreadLocal.get().createNativeQuery(sqlString, resultClass);
    }

    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        return entityManagerNamedThreadLocal.get().createNativeQuery(sqlString, resultSetMapping);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        return entityManagerNamedThreadLocal.get().createNamedStoredProcedureQuery(name);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        return entityManagerNamedThreadLocal.get().createStoredProcedureQuery(procedureName);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
        return entityManagerNamedThreadLocal.get().createStoredProcedureQuery(procedureName, resultClasses);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
        return entityManagerNamedThreadLocal.get().createStoredProcedureQuery(procedureName, resultSetMappings);
    }

    @Override
    public void joinTransaction() {
        entityManagerNamedThreadLocal.get().joinTransaction();
    }

    @Override
    public boolean isJoinedToTransaction() {
        return entityManagerNamedThreadLocal.get().isJoinedToTransaction();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return entityManagerNamedThreadLocal.get().unwrap(cls);
    }

    @Override
    public Object getDelegate() {
        return entityManagerNamedThreadLocal.get().getDelegate();
    }

    @Override
    public void close() {
        entityManagerNamedThreadLocal.get().close();
    }

    @Override
    public boolean isOpen() {
        return entityManagerNamedThreadLocal.get() != null && entityManagerNamedThreadLocal.get().isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {
        return entityManagerNamedThreadLocal.get().getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerNamedThreadLocal.get().getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return entityManagerNamedThreadLocal.get().getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {
        return entityManagerNamedThreadLocal.get().getMetamodel();
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        return entityManagerNamedThreadLocal.get().createEntityGraph(rootType);
    }

    @Override
    public EntityGraph<?> createEntityGraph(String graphName) {
        return entityManagerNamedThreadLocal.get().createEntityGraph(graphName);
    }

    @Override
    public EntityGraph<?> getEntityGraph(String graphName) {
        return entityManagerNamedThreadLocal.get().getEntityGraph(graphName);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        return entityManagerNamedThreadLocal.get().getEntityGraphs(entityClass);
    }

    @Override
    public void assignEntityManager(EntityManager otherEntityManager) {
        entityManagerNamedThreadLocal.set(otherEntityManager);
    }

    @Override
    public void releaseEntityManager() {
        entityManagerNamedThreadLocal.remove();
    }
}
