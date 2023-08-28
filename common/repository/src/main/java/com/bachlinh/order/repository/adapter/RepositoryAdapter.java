package com.bachlinh.order.repository.adapter;


import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.EntityManagerHolder;
import com.bachlinh.order.entity.EntityTrigger;
import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.entity.HintDecorator;
import com.bachlinh.order.entity.ValidateResult;
import com.bachlinh.order.entity.context.IdContext;
import com.bachlinh.order.entity.enums.TriggerExecution;
import com.bachlinh.order.entity.enums.TriggerMode;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.exception.HttpException;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.exception.http.ValidationFailureException;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.service.container.DependenciesResolver;
import jakarta.persistence.Cacheable;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.jpa.repository.support.QueryHints;
import org.springframework.data.jpa.support.PageableUtils;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.data.util.ProxyUtils;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.data.jpa.repository.query.QueryUtils.COUNT_QUERY_STRING;
import static org.springframework.data.jpa.repository.query.QueryUtils.DELETE_ALL_QUERY_BY_ID_STRING;
import static org.springframework.data.jpa.repository.query.QueryUtils.DELETE_ALL_QUERY_STRING;
import static org.springframework.data.jpa.repository.query.QueryUtils.applyAndBind;
import static org.springframework.data.jpa.repository.query.QueryUtils.getQueryString;
import static org.springframework.data.jpa.repository.query.QueryUtils.toOrders;

public abstract class RepositoryAdapter<T extends BaseEntity<U>, U> implements HintDecorator, EntityManagerHolder, JpaRepositoryImplementation<T, U> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String ENTITY_NULL_MESSAGE = "Entity must not be null";
    private final Class<T> domainClass;
    private final EntityManager asyncEntityManager;
    private JpaEntityInformation<T, ?> entityInformation;
    private PersistenceProvider provider;
    private @Nullable CrudMethodMetadata metadata;
    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;
    private EntityManager em;
    private final SessionFactory sessionFactory;
    private final EntityFactory entityFactory;
    private final boolean useCache;
    private final InternalTriggerExecution triggerExecution;
    protected final SqlBuilder sqlBuilder;

    protected RepositoryAdapter(Class<T> domainClass, DependenciesResolver resolver) {
        this.domainClass = domainClass;
        this.sessionFactory = resolver.resolveDependencies(SessionFactory.class);
        this.entityFactory = resolver.resolveDependencies(EntityFactory.class);
        this.useCache = getDomainClass().isAnnotationPresent(Cacheable.class) && getDomainClass().isAnnotationPresent(Cache.class);
        this.asyncEntityManager = resolver.resolveDependencies(EntityManager.class);
        this.triggerExecution = new InternalTriggerExecution(entityFactory);
        this.sqlBuilder = resolver.resolveDependencies(SqlBuilder.class);
    }

    protected void setEntityManager(EntityManager entityManager) {
        this.entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
        this.provider = PersistenceProvider.fromEntityManager(entityManager);
        this.em = entityManager;
    }

    protected EntityFactory getEntityFactory() {
        return entityFactory;
    }

    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    protected boolean isUseCache() {
        return useCache;
    }

    @Override
    public void setRepositoryMethodMetadata(@NonNull CrudMethodMetadata crudMethodMetadata) {
        this.metadata = crudMethodMetadata;
    }

    @Override
    public void setEscapeCharacter(@NonNull EscapeCharacter escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    @Nullable
    protected CrudMethodMetadata getRepositoryMethodMetadata() {
        return metadata;
    }

    protected Class<T> getDomainClass() {
        if (entityInformation == null) {
            return domainClass;
        }
        return entityInformation.getJavaType();
    }

    private String getDeleteAllQueryString() {
        return getQueryString(DELETE_ALL_QUERY_STRING, entityInformation.getEntityName());
    }

    private String getCountQueryString() {

        String countQuery = String.format(COUNT_QUERY_STRING, provider.getCountQueryPlaceholder(), "%s");
        return getQueryString(countQuery, entityInformation.getEntityName());
    }

    @Transactional
    @Override
    public void deleteById(@NonNull U id) {
        findById(id).ifPresent(this::delete);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public void delete(@NonNull T entity) {

        triggerExecution.trigger(t -> {
            Assert.notNull(entity, ENTITY_NULL_MESSAGE);

            if (entityInformation.isNew(entity)) {
                return null;
            }

            Class<?> type = ProxyUtils.getUserClass(entity);

            T existing = (T) em.find(type, entityInformation.getId(entity));

            // if the entity to be deleted doesn't exist, delete is a NOOP
            if (existing == null) {
                // throw exception for trigger transaction rollback and stop trigger process
                throw new ResourceNotFoundException(String.format("Entity with id [%s] not existed", entity.getId()), "");
            }
            boolean springActualTransactionActive = entityFactory.getTransactionManager().isActualTransactionActive();
            if (!springActualTransactionActive) {
                asyncEntityManager.getTransaction().begin();
                asyncEntityManager.remove(asyncEntityManager.contains(entity) ? entity : asyncEntityManager.merge(entity));
                asyncEntityManager.getTransaction().commit();
                return entity;
            }
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            return entity;
        }, entity, domainClass, TriggerExecution.ON_DELETE);
    }

    @Override
    @Transactional
    public void deleteAllById(@NonNull Iterable<? extends U> ids) {
        for (U id : ids) {
            deleteById(id);
        }
    }

    @Override
    @Transactional
    public void deleteAllByIdInBatch(@NonNull Iterable<U> ids) {

        Assert.notNull(ids, "Ids must not be null");

        if (!ids.iterator().hasNext()) {
            return;
        }

        if (entityInformation.hasCompositeId()) {

            List<T> entities = new ArrayList<>();
            // generate entity (proxies) without accessing the database.
            ids.forEach(id -> entities.add(getReferenceById(id)));
            deleteAllInBatch(entities);
        } else {

            String queryString = String.format(DELETE_ALL_QUERY_BY_ID_STRING, entityInformation.getEntityName(), entityInformation.getIdAttribute().getName());

            Query query = em.createQuery(queryString);
            if (ids instanceof Collection<U>) {
                query.setParameter("ids", ids);
            } else {
                Collection<U> idsCollection = StreamSupport.stream(ids.spliterator(), false).collect(Collectors.toCollection(ArrayList::new));
                query.setParameter("ids", idsCollection);
            }

            applyQueryHints(query);
            query.executeUpdate();
        }
    }

    @Override
    @Transactional
    public void deleteAll(@NonNull Iterable<? extends T> entities) {

        for (T entity : entities) {
            delete(entity);
        }
    }

    @Override
    @Transactional
    public void deleteAllInBatch(@NonNull Iterable<T> entities) {

        if (!entities.iterator().hasNext()) {
            return;
        }

        applyAndBind(getQueryString(DELETE_ALL_QUERY_STRING, entityInformation.getEntityName()), entities, em).executeUpdate();
    }

    @Override
    @Transactional
    public void deleteAll() {

        for (T element : findAll()) {
            delete(element);
        }
    }

    @Override
    @Transactional
    public void deleteAllInBatch() {

        Query query = em.createQuery(getDeleteAllQueryString());

        applyQueryHints(query);

        query.executeUpdate();
    }

    @NonNull
    @Override
    public Optional<T> findById(@NonNull U id) {

        Class<T> domainType = getDomainClass();

        if (metadata == null) {
            return Optional.ofNullable(em.find(domainType, id));
        }

        LockModeType type = metadata.getLockModeType();

        Map<String, Object> hints = new HashMap<>();

        getQueryHints().withFetchGraphs(em).forEach(hints::put);

        metadata.getComment();
        if (provider.getCommentHintKey() != null) {
            hints.put(provider.getCommentHintKey(), provider.getCommentHintValue(metadata.getComment()));
        }

        return Optional.ofNullable(type == null ? em.find(domainType, id, hints) : em.find(domainType, id, type, hints));
    }

    /**
     * Returns {@link QueryHints} with the query hints based on the current {@link CrudMethodMetadata} and potential
     * {@link EntityGraph} information.
     */
    protected QueryHints getQueryHints() {
        return metadata == null ? QueryHints.NoHints.INSTANCE : DefaultQueryHints.of(entityInformation, metadata);
    }

    /**
     * Returns {@link QueryHints} with the query hints on the current {@link CrudMethodMetadata} for count queries.
     */
    protected QueryHints getQueryHintsForCount() {
        return metadata == null ? QueryHints.NoHints.INSTANCE : DefaultQueryHints.of(entityInformation, metadata).forCounts();
    }

    @NonNull
    @Override
    public T getReferenceById(@NonNull U id) {
        return em.getReference(getDomainClass(), id);
    }

    @Override
    public boolean existsById(@NonNull U id) {

        if (entityInformation.getIdAttribute() == null) {
            return findById(id).isPresent();
        }

        String placeholder = provider.getCountQueryPlaceholder();
        String entityName = entityInformation.getEntityName();
        Iterable<String> idAttributeNames = entityInformation.getIdAttributeNames();
        String existsQuery = QueryUtils.getExistsQueryString(entityName, placeholder, idAttributeNames);

        TypedQuery<Long> query = em.createQuery(existsQuery, Long.class);

        Map<String, Object> hints = new HashMap<>();
        getQueryHints().withFetchGraphs(em).forEach(hints::put);

        if (metadata != null) {
            String comment = metadata.getComment();
            if (provider.getCommentHintKey() != null) {
                hints.put(provider.getCommentHintKey(), provider.getCommentHintValue(comment));
            }
        }

        hints.forEach(query::setHint);

        if (!entityInformation.hasCompositeId()) {
            query.setParameter(idAttributeNames.iterator().next(), id);
            return query.getSingleResult() == 1L;
        }

        for (String idAttributeName : idAttributeNames) {

            Object idAttributeValue = entityInformation.getCompositeIdAttributeValue(id, idAttributeName);

            boolean complexIdParameterValueDiscovered = idAttributeValue != null && !query.getParameter(idAttributeName).getParameterType().isAssignableFrom(idAttributeValue.getClass());

            if (complexIdParameterValueDiscovered) {

                // fall-back to findById(id) which does the proper mapping for the parameter.
                return findById(id).isPresent();
            }

            query.setParameter(idAttributeName, idAttributeValue);
        }

        return query.getSingleResult() == 1L;
    }

    @NonNull
    @Override
    public List<T> findAll() {
        return getQuery(null, Sort.unsorted()).getResultList();
    }

    @NonNull
    @Override
    public List<T> findAllById(@NonNull Iterable<U> ids) {

        Assert.notNull(ids, "Ids must not be null");

        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        if (entityInformation.hasCompositeId()) {

            List<T> results = new ArrayList<>();

            for (U U : ids) {
                findById(U).ifPresent(results::add);
            }

            return results;
        }

        Collection<U> idsCollection = Streamable.of(ids).toList();

        ByIdsSpecification<T> specification = new ByIdsSpecification<>(entityInformation);
        TypedQuery<T> query = getQuery(specification, Sort.unsorted());

        return query.setParameter(specification.parameter, idsCollection).getResultList();
    }

    @NonNull
    @Override
    public List<T> findAll(@NonNull Sort sort) {
        return getQuery(null, sort).getResultList();
    }

    @NonNull
    @Override
    public Page<T> findAll(@NonNull Pageable pageable) {

        if (isUnpaged(pageable)) {
            return new PageImpl<>(findAll());
        }

        return findAll((Specification<T>) null, pageable);
    }

    @NonNull
    @Override
    public Optional<T> findOne(@Nullable Specification<T> spec) {

        try {
            return Optional.of(getQuery(spec, Sort.unsorted()).setMaxResults(2).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @NonNull
    @Override
    public List<T> findAll(@Nullable Specification<T> spec) {
        return getQuery(spec, Sort.unsorted()).getResultList();
    }

    @NonNull
    @Override
    public Page<T> findAll(@Nullable Specification<T> spec, @NonNull Pageable pageable) {

        TypedQuery<T> query = getQuery(spec, pageable);
        return isUnpaged(pageable) ? new PageImpl<>(query.getResultList()) : readPage(query, getDomainClass(), pageable, spec);
    }

    @NonNull
    @Override
    public List<T> findAll(@Nullable Specification<T> spec, @NonNull Sort sort) {
        return getQuery(spec, sort).getResultList();
    }

    @NonNull
    @Override
    public <S extends T> Optional<S> findOne(@NonNull Example<S> example) {

        try {
            return Optional.of(getQuery(new ExampleSpecification<>(example, escapeCharacter), example.getProbeType(), Sort.unsorted()).setMaxResults(2).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public <S extends T> long count(@NonNull Example<S> example) {
        return executeCountQuery(getCountQuery(new ExampleSpecification<>(example, escapeCharacter), example.getProbeType()));
    }

    @Override
    public <S extends T> boolean exists(@NonNull Example<S> example) {

        Specification<S> spec = new ExampleSpecification<>(example, this.escapeCharacter);
        CriteriaQuery<Integer> cq = this.em.getCriteriaBuilder().createQuery(Integer.class);
        cq.select(this.em.getCriteriaBuilder().literal(1));
        applySpecificationToCriteria(spec, example.getProbeType(), cq);
        TypedQuery<Integer> query = applyRepositoryMethodMetadata(this.em.createQuery(cq));
        return query.setMaxResults(1).getResultList().size() == 1;
    }

    @Override
    public boolean exists(@NonNull Specification<T> spec) {

        CriteriaQuery<Integer> cq = this.em.getCriteriaBuilder().createQuery(Integer.class);
        cq.select(this.em.getCriteriaBuilder().literal(1));
        applySpecificationToCriteria(spec, getDomainClass(), cq);
        TypedQuery<Integer> query = applyRepositoryMethodMetadata(this.em.createQuery(cq));
        return query.setMaxResults(1).getResultList().size() == 1;
    }

    @Override
    public long delete(Specification<T> spec) {

        CriteriaBuilder builder = this.em.getCriteriaBuilder();
        CriteriaDelete<T> delete = builder.createCriteriaDelete(getDomainClass());

        Predicate predicate = spec.toPredicate(delete.from(getDomainClass()), null, builder);

        if (predicate != null) {
            delete.where(predicate);
        }

        return this.em.createQuery(delete).executeUpdate();
    }

    @NonNull
    @Override
    public <S extends T> List<S> findAll(@NonNull Example<S> example) {
        return getQuery(new ExampleSpecification<>(example, escapeCharacter), example.getProbeType(), Sort.unsorted()).getResultList();
    }

    @NonNull
    @Override
    public <S extends T> List<S> findAll(@NonNull Example<S> example, @NonNull Sort sort) {
        return getQuery(new ExampleSpecification<>(example, escapeCharacter), example.getProbeType(), sort).getResultList();
    }

    @NonNull
    @Override
    public <S extends T> Page<S> findAll(@NonNull Example<S> example, @NonNull Pageable pageable) {

        ExampleSpecification<S> spec = new ExampleSpecification<>(example, escapeCharacter);
        Class<S> probeType = example.getProbeType();
        TypedQuery<S> query = getQuery(new ExampleSpecification<>(example, escapeCharacter), probeType, pageable);

        return isUnpaged(pageable) ? new PageImpl<>(query.getResultList()) : readPage(query, probeType, pageable, spec);
    }

    @NonNull
    @Override
    public <S extends T, R> R findBy(@NonNull Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {

        Function<Sort, TypedQuery<S>> finder = sort -> {

            ExampleSpecification<S> spec = new ExampleSpecification<>(example, escapeCharacter);
            Class<S> probeType = example.getProbeType();

            return getQuery(spec, probeType, sort);
        };

        FluentQuery.FetchableFluentQuery<S> fluentQuery = new FetchableFluentQueryByExample<>(example, finder, this::count, this::exists, this.em, this.escapeCharacter);

        return queryFunction.apply(fluentQuery);
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <S extends T, R> R findBy(@NonNull Specification<T> spec, @NonNull Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        Function<Sort, TypedQuery<T>> finder = sort -> getQuery(spec, getDomainClass(), sort);

        FluentQuery.FetchableFluentQuery<R> fluentQuery = new FetchableFluentQueryBySpecification<>(spec, getDomainClass(), Sort.unsorted(), null, finder, this::count, this::exists, this.em);

        return queryFunction.apply((FluentQuery.FetchableFluentQuery<S>) fluentQuery);
    }

    @Override
    public long count() {

        TypedQuery<Long> query = em.createQuery(getCountQueryString(), Long.class);

        applyQueryHintsForCount(query);

        return query.getSingleResult();
    }

    @Override
    public long count(@Nullable Specification<T> spec) {
        return executeCountQuery(getCountQuery(spec, getDomainClass()));
    }

    @NonNull
    @Transactional
    @Override
    public <S extends T> S save(@NonNull S entity) {
        if (log.isDebugEnabled()) {
            log.debug("START SAVE entity [{}]", entity.getClass().getName());
        }

        UnaryOperator<S> saveCallback = getSaveCallback();

        try {
            Assert.notNull(entity, ENTITY_NULL_MESSAGE);
            if (entity.getId() == null) {
                return triggerExecution.trigger(saveCallback, entity, domainClass, TriggerExecution.ON_INSERT);
            } else {
                return triggerExecution.trigger(saveCallback, entity, domainClass, TriggerExecution.ON_UPDATE);
            }
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("END SAVE entity [{}]", entity.getClass().getName());
            }
        }
    }

    @NonNull
    @Transactional
    @Override
    public <S extends T> S saveAndFlush(@NonNull S entity) {

        Assert.notNull(entity, ENTITY_NULL_MESSAGE);

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return saveAsync(entity);
        } else {
            S result = saveSync(entity);
            flush();
            return result;
        }
    }

    @NonNull
    @Transactional
    @Override
    public <S extends T> List<S> saveAll(@NonNull Iterable<S> entities) {
        List<S> result = new ArrayList<>();

        for (S entity : entities) {
            result.add(save(entity));
        }

        return result;
    }

    @NonNull
    @Transactional
    @Override
    public <S extends T> List<S> saveAllAndFlush(@NonNull Iterable<S> entities) {

        List<S> result = saveAll(entities);
        flush();

        return result;
    }

    @Transactional
    @Override
    public void flush() {
        em.flush();
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    protected <S extends T> Page<S> readPage(TypedQuery<S> query, final Class<S> domainClass, Pageable pageable, @Nullable Specification<S> spec) {

        if (pageable.isPaged()) {
            query.setFirstResult(PageableUtils.getOffsetAsInteger(pageable));
            query.setMaxResults(pageable.getPageSize());
        }

        return PageableExecutionUtils.getPage(query.getResultList(), pageable, () -> executeCountQuery(getCountQuery(spec, domainClass)));
    }

    protected TypedQuery<T> getQuery(@Nullable Specification<T> spec, Pageable pageable) {

        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        return getQuery(spec, getDomainClass(), sort);
    }

    protected <S extends T> TypedQuery<S> getQuery(@Nullable Specification<S> spec, Class<S> domainClass, Pageable pageable) {

        Sort sort = pageable.isPaged() ? pageable.getSort() : Sort.unsorted();
        return getQuery(spec, domainClass, sort);
    }

    protected TypedQuery<T> getQuery(@Nullable Specification<T> spec, Sort sort) {
        return getQuery(spec, getDomainClass(), sort);
    }

    protected <S extends T> TypedQuery<S> getQuery(@Nullable Specification<S> spec, Class<S> domainClass, Sort sort) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<S> query = builder.createQuery(domainClass);

        Root<S> root = applySpecificationToCriteria(spec, domainClass, query);
        query.select(root);

        if (sort.isSorted()) {
            query.orderBy(toOrders(sort, root, builder));
        }

        return applyRepositoryMethodMetadata(em.createQuery(query));
    }

    protected <S extends T> TypedQuery<Long> getCountQuery(@Nullable Specification<S> spec, Class<S> domainClass) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<S> root = applySpecificationToCriteria(spec, domainClass, query);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        // Remove all Orders the Specifications might have applied
        query.orderBy(Collections.emptyList());

        return applyRepositoryMethodMetadataForCount(em.createQuery(query));
    }

    private <S extends T> S saveAsync(@NonNull S entity) {
        asyncEntityManager.getTransaction().begin();
        if (entityInformation.isNew(entity)) {
            asyncEntityManager.persist(entity);
            asyncEntityManager.getTransaction().commit();
            return entity;
        }
        sessionFactory.getCache().evict(entity.getClass(), entity.getId());
        //FIXME use native query to update for improved performance
        S result = asyncEntityManager.merge(entity);
        asyncEntityManager.getTransaction().commit();
        return result;
    }

    private <S extends T> S saveSync(@NonNull S entity) {
        if (entityInformation.isNew(entity)) {
            em.persist(entity);
            return entity;
        }
        sessionFactory.getCache().evict(entity.getClass(), entity.getId());
        //FIXME use native query to update for improved performance
        return em.merge(entity);
    }

    private <S, X extends T> Root<X> applySpecificationToCriteria(@Nullable Specification<X> spec, Class<X> domainClass, CriteriaQuery<S> query) {

        Assert.notNull(domainClass, "Domain class must not be null");
        Assert.notNull(query, "CriteriaQuery must not be null");

        Root<X> root = query.from(domainClass);

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = em.getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        return root;
    }

    private <S> TypedQuery<S> applyRepositoryMethodMetadata(TypedQuery<S> query) {

        if (metadata == null) {
            return query;
        }

        LockModeType type = metadata.getLockModeType();
        TypedQuery<S> toReturn = type == null ? query : query.setLockMode(type);

        applyQueryHints(toReturn);
        applyCacheQueryHints(query, getEntityFactory().getEntityContext(domainClass).getCacheRegion());

        return toReturn;
    }

    private void applyQueryHints(Query query) {

        if (metadata == null) {
            return;
        }

        getQueryHints().withFetchGraphs(em).forEach(query::setHint);

        metadata.getComment();
        if (provider.getCommentHintKey() != null) {
            query.setHint(provider.getCommentHintKey(), provider.getCommentHintValue(metadata.getComment()));
        }
    }

    private <S> TypedQuery<S> applyRepositoryMethodMetadataForCount(TypedQuery<S> query) {

        if (metadata == null) {
            return query;
        }

        applyQueryHintsForCount(query);

        return query;
    }

    private void applyQueryHintsForCount(Query query) {

        if (metadata == null) {
            return;
        }

        getQueryHintsForCount().forEach(query::setHint);

        metadata.getComment();
        if (provider.getCommentHintKey() != null) {
            query.setHint(provider.getCommentHintKey(), provider.getCommentHintValue(metadata.getComment()));
        }
    }

    private <S extends T> UnaryOperator<S> getSaveCallback() {
        return t -> {
            if (!TransactionSynchronizationManager.isSynchronizationActive()) {
                return saveAsync(t);
            } else {
                return saveSync(t);
            }
        };
    }

    private static long executeCountQuery(TypedQuery<Long> query) {

        Assert.notNull(query, "TypedQuery must not be null");

        List<Long> totals = query.getResultList();
        long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    private static boolean isUnpaged(Pageable pageable) {
        return pageable.isUnpaged();
    }

    @SuppressWarnings("rawtypes")
    private static final class ByIdsSpecification<T> implements Specification<T> {

        private final transient JpaEntityInformation<T, ?> entityInformation;

        @Nullable
        transient ParameterExpression<Collection<?>> parameter;

        ByIdsSpecification(JpaEntityInformation<T, ?> entityInformation) {
            this.entityInformation = entityInformation;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Predicate toPredicate(Root<T> root, @NonNull CriteriaQuery<?> query, CriteriaBuilder cb) {

            Path<?> path = root.get(entityInformation.getIdAttribute());
            parameter = (ParameterExpression) cb.parameter(Collection.class);
            return path.in(parameter);
        }
    }

    private record ExampleSpecification<T>(Example<T> example,
                                           EscapeCharacter escapeCharacter) implements Specification<T> {

        private ExampleSpecification {

            Assert.notNull(example, "Example must not be null");
            Assert.notNull(escapeCharacter, "EscapeCharacter must not be null");

        }

        @Override
        public Predicate toPredicate(@NonNull Root<T> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder cb) {
            return QueryByExamplePredicateBuilder.getPredicate(root, cb, example, escapeCharacter);
        }
    }

    private class InternalTriggerExecution {

        private final EntityFactory entityFactory;

        InternalTriggerExecution(EntityFactory entityFactory) {
            this.entityFactory = entityFactory;
        }

        @SuppressWarnings("unchecked")
        <S extends T> S trigger(UnaryOperator<S> function, S entity, Class<T> domainClass, TriggerExecution execution) {
            Collection<EntityTrigger<S>> triggers = entityFactory.getEntityContext(domainClass)
                    .getTrigger()
                    .stream()
                    .map(entityTrigger -> (EntityTrigger<S>) entityTrigger)
                    .toList();
            return runTrigger(triggers, entity, execution, function);
        }

        private <S extends T> S runTrigger(Collection<EntityTrigger<S>> bases, S entity, TriggerExecution execution, UnaryOperator<S> function) {
            Collection<EntityTrigger<S>> triggers = extractTriggers(bases, execution);
            IdContext idContext = entityFactory.getEntityContext(entity.getClass());
            idContext.beginTransaction();
            try {
                executeBeforeAction(triggers, entity);
                validateBeforeSave(entity);
                S result = function.apply(entity);
                executeAfterAction(triggers, entity);
                idContext.commit();
                return result;
            } catch (HttpException e) {
                idContext.rollback();
                throw e;
            } catch (Exception e) {
                idContext.rollback();
                throw new CriticalException(String.format("Fail to save entity [%s].", entity.getClass().getName()), e);
            }
        }

        private <S extends T> Collection<EntityTrigger<S>> extractTriggers(Collection<EntityTrigger<S>> triggers, TriggerExecution triggerExecution) {
            return triggers.stream()
                    .filter(trigger -> Arrays.asList(trigger.getExecuteOn()).contains(triggerExecution))
                    .toList();
        }

        private <S extends T> void executeBeforeAction(Collection<EntityTrigger<S>> triggers, S entity) {
            Collection<EntityTrigger<S>> beforeAction = triggers.stream()
                    .filter(trigger -> trigger.getMode() == TriggerMode.BEFORE)
                    .toList();
            beforeAction.forEach(trigger -> {
                if (log.isDebugEnabled()) {
                    log.debug("Execute trigger [{}] with type [BEFORE] on entity [{}]", trigger.getTriggerName(), entity.getClass().getName());
                }
                trigger.execute(entity);
            });
        }

        private <S extends T> void executeAfterAction(Collection<EntityTrigger<S>> triggers, S entity) {
            Collection<EntityTrigger<S>> beforeAction = triggers.stream()
                    .filter(trigger -> trigger.getMode() == TriggerMode.AFTER)
                    .toList();
            beforeAction.forEach(trigger -> {
                if (log.isDebugEnabled()) {
                    log.debug("Execute trigger [{}] with type [AFTER] on entity [{}]", trigger.getTriggerName(), entity.getClass().getName());
                }
                trigger.execute(entity);
            });
        }

        @SuppressWarnings("unchecked")
        private void validateBeforeSave(T entity) {
            Collection<EntityValidator<T>> validators = new ArrayList<>();
            getEntityFactory().getEntityContext(entity.getClass()).getValidators().forEach(validator -> validators.add((EntityValidator<T>) validator));
            if (validators.isEmpty()) {
                log.info("Skip validate on entity [{}]", entity.getClass().getName());
                return;
            }
            log.info("BEGIN validate entity [{}]", entity.getClass().getName());
            doValidate(validators, entity);
            log.info("END validate entity [{}]", entity.getClass().getName());
        }

        private void doValidate(Collection<EntityValidator<T>> validators, T entity) {
            Set<String> errors = new HashSet<>();
            validators.forEach(entityValidator -> entityValidatorCallback(entityValidator, errors, entity));
            if (!errors.isEmpty()) {
                throw new ValidationFailureException(errors, String.format("Fail when validate entity [%s]", entity.getClass()));
            }
        }

        private void entityValidatorCallback(EntityValidator<T> entityValidator, Set<String> errors, T entity) {
            ValidateResult result = entityValidator.validate(entity);
            if (result.hasError()) {
                errors.addAll(result.getMessages());
            }
        }
    }
}
