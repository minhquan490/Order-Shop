package com.bachlinh.order.repository.adapter;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.query.Jpa21Utils;
import org.springframework.data.jpa.repository.query.JpaEntityGraph;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.MutableQueryHints;
import org.springframework.data.jpa.repository.support.QueryHints;
import org.springframework.data.util.Optionals;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.function.BiConsumer;

public class DefaultQueryHints implements QueryHints {

    private final JpaEntityInformation<?, ?> information;
    private final CrudMethodMetadata metadata;
    private final Optional<EntityManager> entityManager;
    private final boolean forCounts;

    private DefaultQueryHints(JpaEntityInformation<?, ?> information, CrudMethodMetadata metadata,
                              Optional<EntityManager> entityManager, boolean forCounts) {

        this.information = information;
        this.metadata = metadata;
        this.entityManager = entityManager;
        this.forCounts = forCounts;
    }

    public static QueryHints of(JpaEntityInformation<?, ?> information, CrudMethodMetadata metadata) {

        Assert.notNull(information, "JpaEntityInformation must not be null");
        Assert.notNull(metadata, "CrudMethodMetadata must not be null");

        return new DefaultQueryHints(information, metadata, Optional.empty(), false);
    }

    @NonNull
    @Override
    public QueryHints withFetchGraphs(EntityManager em) {
        return new DefaultQueryHints(this.information, this.metadata, Optional.of(em), this.forCounts);
    }

    @NonNull
    @Override
    public QueryHints forCounts() {
        return new DefaultQueryHints(this.information, this.metadata, this.entityManager, true);
    }

    @Override
    public void forEach(BiConsumer<String, Object> action) {
        combineHints().forEach(action);
    }

    private QueryHints combineHints() {
        return QueryHints.from(forCounts ? metadata.getQueryHintsForCount() : metadata.getQueryHints(), getFetchGraphs());
    }

    private QueryHints getFetchGraphs() {

        return Optionals
                .mapIfAllPresent(entityManager, metadata.getEntityGraph(),
                        (em, graph) -> Jpa21Utils.getFetchGraphHint(em, getEntityGraph(graph), information.getJavaType()))
                .orElse(new MutableQueryHints());
    }

    private JpaEntityGraph getEntityGraph(EntityGraph entityGraph) {

        String fallbackName = information.getEntityName() + "." + metadata.getMethod().getName();
        return new JpaEntityGraph(entityGraph, fallbackName);
    }
}
