package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.District_;
import com.bachlinh.order.repository.DistrictRepository;
import com.bachlinh.order.repository.adapter.AbstractRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.OrderBy;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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
        Where districtIdWhere = Where.builder().attribute(District_.ID).value(districtId).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(District.class);
        SqlWhere sqlWhere = sqlSelect.where(districtIdWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        var results = executeNativeQuery(sql, attributes, District.class);
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
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
    public Collection<District> getDistrictsByProvince(String provinceId) {
        Where provinceWhere = Where.builder().attribute(District_.PROVINCE).value(provinceId).operator(Operator.EQ).build();
        Select idSelect = Select.builder().column(District_.ID).build();
        Select nameSelect = Select.builder().column(District_.NAME).build();
        OrderBy nameOrderBy = OrderBy.builder().column(District_.NAME).type(OrderBy.Type.ASC).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(District.class);
        sqlSelect.select(idSelect).select(nameSelect);
        SqlWhere sqlWhere = sqlSelect.where(provinceWhere);
        sqlWhere.orderBy(nameOrderBy);
        String query = sqlWhere.getNativeQuery();
        Map<String, Object> params = QueryUtils.parse(sqlWhere.getQueryBindings());
        return executeNativeQuery(query, params, District.class);
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
