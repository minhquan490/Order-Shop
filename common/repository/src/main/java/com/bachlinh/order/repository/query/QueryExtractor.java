package com.bachlinh.order.repository.query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class QueryExtractor {
    private final CriteriaBuilder builder;
    private final CriteriaQuery<?> query;
    private final Root<?> root;

    private final List<Predicate> whereList = new ArrayList<>();

    private Predicate where(Root<?> root, Where where) {
        return switch (where.getOperator()) {
            case GE -> builder.ge(root.get(where.getAttribute()), (Number) where.getValue());
            case GT -> builder.gt(root.get(where.getAttribute()), (Number) where.getValue());
            case EQ -> builder.equal(root.get(where.getAttribute()), where.getValue());
            case LE -> builder.le(root.get(where.getAttribute()), (Number) where.getValue());
            case LT -> builder.lt(root.get(where.getAttribute()), (Number) where.getValue());
            case NEQ -> builder.notEqual(root.get(where.getAttribute()), where.getValue());
            case NULL -> builder.isNull(root.get(where.getAttribute()));
            case NON_NULL -> builder.isNotNull(root.get(where.getAttribute()));
            case IN -> root.get(where.getAttribute()).in(where.getValue());
        };
    }

    public QueryExtractor select(Select... select) {
        for (var s : select) {
            query.select(root.get(s.getColumn()));
        }
        return this;
    }

    public QueryExtractor where(Where... where) {
        for (var w : where) {
            whereList.add(where(this.root, w));
        }
        return this;
    }

    public QueryExtractor orderBy(OrderBy... order) {
        for (var o : order) {
            switch (o.getType()) {
                case ASC -> query.orderBy(builder.asc(root.get(o.getColumn())));
                case DESC -> query.orderBy(builder.desc(root.get(o.getColumn())));
            }
        }
        return this;
    }

    public QueryExtractor join(Join... join) {
        for (var j : join) {
            root.join(j.getAttribute(), j.getType());
        }
        return this;
    }

    public Predicate extract() {
        return builder.and(whereList.toArray(new Predicate[0]));
    }
}
