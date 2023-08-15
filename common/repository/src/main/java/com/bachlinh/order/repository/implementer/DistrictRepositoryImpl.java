package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.District_;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.DistrictRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.OrderBy;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelection;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.query.WhereOperation;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class DistrictRepositoryImpl extends AbstractRepository<District, Integer> implements DistrictRepository {

    @DependenciesInitialize
    @ActiveReflection
    public DistrictRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(District.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public District getDistrictById(String districtId) {
        return findById(Integer.parseInt(districtId)).orElse(null);
    }

    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    @Override
    public boolean saveAllDistrict(Collection<District> districts) {
        return !saveAll(districts).isEmpty();
    }

    @Override
    public long countDistrict() {
        return count();
    }

    @Override
    public Collection<District> getAllDistrict() {
        return findAll();
    }

    @Override
    public Collection<District> getDistricts(Collection<String> ids) {
        return findAllById(ids.stream().map(Integer::parseInt).toList());
    }

    @Override
    public Collection<District> getDistrictsByProvince(Province province) {
        Where provinceWhere = Where.builder().attribute(District_.PROVINCE).value(province).operator(Operator.EQ).build();
        Select idSelect = Select.builder().column(District_.ID).build();
        Select nameSelect = Select.builder().column(District_.NAME).build();
        OrderBy nameOrderBy = OrderBy.builder().column(District_.NAME).type(OrderBy.Type.ASC).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelection sqlSelection = sqlBuilder.from(District.class);
        sqlSelection.select(idSelect).select(nameSelect);
        WhereOperation whereOperation = sqlSelection.where(provinceWhere);
        whereOperation.orderBy(nameOrderBy);
        String query = whereOperation.getNativeQuery();
        Map<String, Object> params = new HashMap<>();
        whereOperation.getQueryBindings().forEach(queryBinding -> params.put(queryBinding.attribute(), queryBinding.value()));
        return executeNativeQuery(query, params, District.class);
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
