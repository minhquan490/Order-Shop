package com.bachlinh.order.repository;

import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.jpa.HibernateHints;
import org.hibernate.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.lang.NonNull;
import com.bachlinh.order.annotation.Validated;
import com.bachlinh.order.entity.EntityManagerHolder;
import com.bachlinh.order.entity.HintDecorator;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.repository.adapter.RepositoryAdapter;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Slf4j
public abstract class AbstractRepository<T extends BaseEntity, U> extends RepositoryAdapter<T, U> implements HintDecorator, EntityManagerHolder, JpaRepositoryImplementation<T, U> {

    protected AbstractRepository(Class<T> domainClass, DependenciesResolver dependenciesResolver) {
        super(domainClass, dependenciesResolver);
    }

    @Override
    public void applyCacheQueryHints(TypedQuery<?> query, String region) {
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
    @Validated(targetIndex = 0)
    @NonNull
    public <S extends T> S save(@NonNull S entity) {
        S result = super.save(entity);
        if (entity.getId() != null && getSessionFactory().getCache().containsEntity(entity.getClass(), entity.getId())) {
            getSessionFactory().getCache().evict(entity.getClass(), entity.getId());
        }
        return result;
    }

    @Override
    public void delete(@NonNull T entity) {
        getSessionFactory().getCache().evict(getDomainClass(), entity.getId());
        super.delete(entity);
    }

    @Override
    public long delete(@NonNull Specification<T> spec) {
        getSessionFactory().getCache().evict(getDomainClass());
        return super.delete(spec);
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
        String sqlString = new BasicFormatterImpl().format(typedQuery.unwrap(Query.class).getQueryString()).toLowerCase();
        Collection<String> queries = new HashSet<>();
        ids.forEach(id -> queries.add(sqlString.replace("id =:param0", "id = " + id)));
        String finalQuery = String.join(unionKey, queries.toArray(new String[0]));
        jakarta.persistence.Query query = getEntityManager().createQuery(finalQuery, getDomainClass());
        List<T> result = new ArrayList<>(query.getResultList());
        return new PageImpl<>(result);
    }
}
