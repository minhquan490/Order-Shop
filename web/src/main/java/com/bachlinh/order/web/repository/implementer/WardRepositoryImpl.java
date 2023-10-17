package com.bachlinh.order.web.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.Ward;
import com.bachlinh.order.entity.model.Ward_;
import com.bachlinh.order.web.repository.spi.AbstractRepository;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.repository.query.Operation;
import com.bachlinh.order.repository.query.OrderBy;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.web.repository.spi.WardRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class WardRepositoryImpl extends AbstractRepository<Integer, Ward> implements WardRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public WardRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Ward.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public boolean saveAllWard(Collection<Ward> wards) {
        return !saveAll(wards).isEmpty();
    }

    @Override
    public long countAllWards() {
        return count();
    }

    @Override
    public Collection<Ward> getAllWards() {
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Ward.class);
        return getResultList(sqlSelect.getNativeQuery(), Collections.emptyMap(), getDomainClass());
    }

    @Override
    public Collection<Ward> getWards(Collection<Integer> ids) {
        Where idsWhere = Where.builder().attribute(Ward_.ID).value(ids).operation(Operation.IN).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Ward.class);
        SqlWhere sqlWhere = sqlSelect.where(idsWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, Ward.class);
    }

    @Override
    public Collection<Ward> getWardsByDistrict(District district) {
        Select idSelect = Select.builder().column(Ward_.ID).build();
        Select nameSelect = Select.builder().column(Ward_.NAME).build();
        Where districtWhere = Where.builder().attribute(Ward_.DISTRICT).value(district).operation(Operation.EQ).build();
        OrderBy nameOrderBy = OrderBy.builder().column(Ward_.NAME).type(OrderBy.Type.ASC).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Ward.class);
        sqlSelect.select(idSelect).select(nameSelect);
        SqlWhere sqlWhere = sqlSelect.where(districtWhere);
        sqlWhere.orderBy(nameOrderBy);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, Ward.class);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new WardRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{WardRepository.class};
    }
}
