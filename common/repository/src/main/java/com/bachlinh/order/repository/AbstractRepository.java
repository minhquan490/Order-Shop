package com.bachlinh.order.repository;

import com.bachlinh.order.entity.EntityManagerHolder;
import com.bachlinh.order.entity.HintDecorator;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.repository.adapter.RepositoryAdapter;
import com.bachlinh.order.repository.query.ResultListProcessing;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.service.container.DependenciesResolver;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.jpa.HibernateHints;
import org.hibernate.query.spi.AbstractQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public abstract class AbstractRepository<T extends BaseEntity<U>, U> extends RepositoryAdapter<T, U> implements HintDecorator, EntityManagerHolder, JpaRepositoryImplementation<T, U>, NativeQueryRepository {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final SqlBuilder sqlBuilder;

    protected AbstractRepository(Class<T> domainClass, DependenciesResolver dependenciesResolver) {
        super(domainClass, dependenciesResolver);
        this.sqlBuilder = dependenciesResolver.resolveDependencies(SqlBuilder.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K> List<K> executeNativeQuery(String query, Map<String, Object> attributes, Class<K> receiverType) {
        Class<?> resultSetType;
        String cacheRegion = getEntityFactory().getEntityContext(getDomainClass()).getCacheRegion();
        if (getDomainClass().isAssignableFrom(receiverType)) {
            resultSetType = Tuple.class;
            var typedQuery = getEntityManager().createNativeQuery(query, resultSetType).unwrap(org.hibernate.query.Query.class);
            applyCacheQueryHints(typedQuery, cacheRegion);
            attributes.forEach(typedQuery::setParameter);
            var processing = ResultListProcessing.nativeProcessing(getEntityFactory(), getDomainClass());
            var result = processing.process(typedQuery::getResultList);
            return (List<K>) result;
        } else {
            resultSetType = receiverType;
            var typedQuery = getEntityManager().createNativeQuery(query, resultSetType).unwrap(AbstractQuery.class);
            attributes.forEach(typedQuery::setParameter);
            return typedQuery.getResultList();
        }
    }

    @Override
    public void applyCacheQueryHints(Query query, String region) {
        query.setHint(HibernateHints.HINT_CACHEABLE, true);
        query.setHint(HibernateHints.HINT_CACHE_REGION, region);
        query.setHint("jakarta.persistence.cache.storeMode", CacheStoreMode.REFRESH);
        query.setHint("jakarta.persistence.cache.retrieveMode", CacheRetrieveMode.USE);
    }

    protected T get(Specification<T> spec) {
        TypedQuery<T> query = getQuery(spec, getDomainClass(), Sort.unsorted());
        query.setMaxResults(2);
        T result;
        try {
            result = query.getSingleResult();
        } catch (NoResultException e) {
            log.info("No result found !");
            result = null;
        }
        return result;
    }

    @Override
    @NonNull
    protected <S extends T> TypedQuery<S> getQuery(Specification<S> spec, @NonNull Class<S> domainClass, @NonNull Sort sort) {
        TypedQuery<S> query = super.getQuery(spec, domainClass, sort);
        if (isUseCache()) {
            applyCacheQueryHints(query, getEntityFactory().getEntityContext(domainClass).getCacheRegion());
        }
        return query;
    }

    @Override
    @NonNull
    public <S extends T> S save(@NonNull S entity) {
        S result = super.save(entity);
        if (entity.getId() != null && getSessionFactory().getCache().containsEntity(entity.getClass(), entity.getId())) {
            evictCache(entity);
        }
        return result;
    }

    @Override
    public void delete(@NonNull T entity) {
        super.delete(entity);
        evictCache(entity);
    }

    @Override
    public long delete(@NonNull Specification<T> spec) {
        long result = super.delete(spec);
        evictCache(null);
        return result;
    }

    @Override
    public void deleteAllInBatch() {
        super.deleteAll();
        getSessionFactory().getCache().evict(getDomainClass());
    }

    @Override
    public void deleteAllInBatch(@NonNull Iterable<T> entities) {
        super.deleteAll(entities);
        entities.forEach(this::evictCache);
    }

    @Override
    public void deleteAllByIdInBatch(@NonNull Iterable<U> ids) {
        super.deleteAllById(ids);
        getSessionFactory().getCache().evict(getDomainClass());
    }

    @NonNull
    @Override
    @Deprecated(forRemoval = true)
    public T getOne(@NonNull U id) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @NonNull
    @Override
    @Deprecated(forRemoval = true)
    public T getById(@NonNull U id) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Page<T> unionQueryWithId(Collection<U> ids, Specification<T> specification, Pageable pageable) {
        String unionKey = " union all ";
        TypedQuery<T> typedQuery = getQuery(specification, getDomainClass(), pageable);
        String sqlString = new BasicFormatterImpl().format(typedQuery.unwrap(org.hibernate.query.Query.class).getQueryString()).toLowerCase();
        Collection<String> queries = new HashSet<>();
        ids.forEach(id -> queries.add(sqlString.replace("id =:param0", "id = " + id)));
        String finalQuery = String.join(unionKey, queries.toArray(new String[0]));
        jakarta.persistence.Query query = getEntityManager().createQuery(finalQuery, getDomainClass());
        List<T> result = new ArrayList<>(query.getResultList());
        return new PageImpl<>(result);
    }

    protected SqlBuilder getSqlBuilder() {
        return this.sqlBuilder;
    }

    private void evictCache(T entity) {
        var cache = getSessionFactory().getCache();
        if (entity == null) {
            cache.evict(getDomainClass());
        } else {
            cache.evict(getDomainClass(), entity.getId());
        }
    }
}
