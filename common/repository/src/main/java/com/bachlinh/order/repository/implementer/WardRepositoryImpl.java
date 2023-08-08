package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.Ward;
import com.bachlinh.order.entity.model.Ward_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.WardRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.OrderBy;
import com.bachlinh.order.repository.query.QueryExtractor;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class WardRepositoryImpl extends AbstractRepository<Ward, Integer> implements WardRepository {

    @DependenciesInitialize
    @ActiveReflection
    public WardRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Ward.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public boolean saveAllWard(Collection<Ward> wards) {
        return !saveAll(wards).isEmpty();
    }

    @Override
    public long countAllWards() {
        return count();
    }

    @Override
    public Collection<Ward> getAllWards() {
        return findAll();
    }

    @Override
    public Collection<Ward> getWards(Collection<Integer> ids) {
        return findAllById(ids);
    }

    @Override
    public Collection<Ward> getWardsByDistrict(District district) {
        Select idSelect = Select.builder().column(Ward_.ID).build();
        Select nameSelect = Select.builder().column(Ward_.NAME).build();
        Where districtWhere = Where.builder().attribute(Ward_.DISTRICT).value(district).operator(Operator.EQ).build();
        OrderBy nameOrderBy = OrderBy.builder().column(Ward_.NAME).type(OrderBy.Type.ASC).build();
        Specification<Ward> spec = Specification.where((root, query, criteriaBuilder) -> {
            var queryExtractor = new QueryExtractor(criteriaBuilder, query, root);
            queryExtractor.select(idSelect, nameSelect);
            queryExtractor.where(districtWhere);
            queryExtractor.orderBy(nameOrderBy);
            return queryExtractor.extract();
        });
        return findAll(spec);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
