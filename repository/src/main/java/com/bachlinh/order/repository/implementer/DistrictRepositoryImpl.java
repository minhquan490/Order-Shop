package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.District_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryBase;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.OrderBy;
import com.bachlinh.order.entity.repository.query.Select;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.utils.QueryUtils;
import com.bachlinh.order.repository.DistrictRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class DistrictRepositoryImpl extends AbstractRepository<Integer, District> implements DistrictRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public DistrictRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(District.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public District getDistrictById(String districtId) {
        Where districtIdWhere = Where.builder().attribute(District_.ID).value(districtId).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(District.class);
        SqlWhere sqlWhere = sqlSelect.where(districtIdWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(sql, attributes, District.class);
    }

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
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(District.class);
        String query = sqlSelect.getNativeQuery();
        return getResultList(query, Collections.emptyMap(), getDomainClass());
    }

    @Override
    public Collection<District> getDistricts(Collection<String> ids) {
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(District.class);
        Where idsWhere = Where.builder().attribute(District_.ID).value(ids).operation(Operation.IN).build();
        SqlWhere sqlWhere = sqlSelect.where(idsWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getResultList(sql, attributes, getDomainClass());
    }

    @Override
    public Collection<District> getDistrictsByProvince(String provinceId) {
        Where provinceWhere = Where.builder().attribute(District_.PROVINCE).value(provinceId).operation(Operation.EQ).build();
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
        return this.getResultList(query, params, District.class);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new DistrictRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{DistrictRepository.class};
    }
}
