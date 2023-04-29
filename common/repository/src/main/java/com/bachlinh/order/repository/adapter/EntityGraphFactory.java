package com.bachlinh.order.repository.adapter;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Subgraph;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.mapping.PropertyPath;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.NONE)
abstract class EntityGraphFactory {
    public static final String HINT = "jakarta.persistence.fetchgraph";

    public static <T> EntityGraph<T> create(EntityManager entityManager, Class<T> domainType, Set<String> properties) {

        EntityGraph<T> entityGraph = entityManager.createEntityGraph(domainType);
        Map<String, Subgraph<Object>> existingSubgraphs = new HashMap<>();

        for (String property : properties) {

            Subgraph<Object> current = null;
            String currentFullPath = "";

            for (PropertyPath path : PropertyPath.from(property, domainType)) {

                currentFullPath += path.getSegment() + ".";

                if (path.hasNext()) {
                    final Subgraph<Object> finalCurrent = current;
                    current = current == null
                            ? existingSubgraphs.computeIfAbsent(currentFullPath, k -> entityGraph.addSubgraph(path.getSegment()))
                            : existingSubgraphs.computeIfAbsent(currentFullPath, k -> finalCurrent.addSubgraph(path.getSegment()));
                    continue;
                }

                if (current == null) {
                    entityGraph.addAttributeNodes(path.getSegment());
                } else {
                    current.addAttributeNodes(path.getSegment());

                }
            }
        }

        return entityGraph;
    }

}
