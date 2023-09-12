package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.District_;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.entity.model.Province_;
import com.bachlinh.order.entity.model.Ward;
import com.bachlinh.order.entity.model.Ward_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.ProvinceRepository;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlJoin;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class ProvinceRepositoryImpl extends AbstractRepository<Integer, Province> implements ProvinceRepository {

    @DependenciesInitialize
    @ActiveReflection
    public ProvinceRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Province.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public Province getProvinceById(String provinceId) {
        Where idWhere = Where.builder().attribute(Province_.ID).operator(Operator.EQ).value(provinceId).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Province.class);
        SqlWhere sqlWhere = sqlSelect.where(idWhere);
        return executeWhere(sqlWhere);
    }

    @Override
    public Province getAddress(String provinceId, String districtId, String wardId) {
        Select provinceIdSelect = Select.builder().column(Province_.ID).build();
        Select provinceNameSelect = Select.builder().column(Province_.NAME).build();
        Select districtIdSelect = Select.builder().column(District_.ID).build();
        Select districtNameSelect = Select.builder().column(District_.NAME).build();
        Select wardIdSelect = Select.builder().column(Ward_.ID).build();
        Select wardNameSelect = Select.builder().column(Ward_.NAME).build();
        Join districtJoin = Join.builder().attribute(Province_.DISTRICTS).type(JoinType.INNER).build();
        Join wardJoin = Join.builder().attribute(District_.WARDS).type(JoinType.INNER).build();
        Where provinceIdWhere = Where.builder().attribute(Province_.ID).value(Integer.parseInt(provinceId)).operator(Operator.EQ).build();
        Where districtIdWhere = Where.builder().attribute(District_.ID).value(Integer.parseInt(districtId)).operator(Operator.EQ).build();
        Where wardIdWhere = Where.builder().attribute(Ward_.ID).value(Integer.parseInt(wardId)).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Province.class);
        sqlSelect.select(provinceIdSelect)
                .select(provinceNameSelect)
                .select(districtIdSelect, District.class)
                .select(districtNameSelect, District.class)
                .select(wardIdSelect, Ward.class)
                .select(wardNameSelect, Ward.class);
        SqlJoin sqlJoin = sqlSelect.join(districtJoin)
                .join(wardJoin, District.class);
        SqlWhere sqlWhere = sqlJoin.where(provinceIdWhere)
                .and(districtIdWhere, District.class)
                .and(wardIdWhere, Ward.class);
        return executeWhere(sqlWhere);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public boolean saveAllProvinces(Collection<Province> provinces) {
        return !saveAll(provinces).isEmpty();
    }

    @Override
    public long countProvince() {
        return count();
    }

    @Override
    public Collection<Province> getAllProvinces() {
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Province.class);
        String query = sqlSelect.getNativeQuery();
        return this.getResultList(query, Collections.emptyMap(), Province.class);
    }

    @Override
    public Collection<Province> getProvincesById(Collection<String> ids) {
        Where idsWhere = Where.builder().attribute(Province_.ID).value(ids).operator(Operator.IN).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Province.class);
        SqlWhere sqlWhere = sqlSelect.where(idsWhere);
        return getResultList(sqlWhere.getNativeQuery(), QueryUtils.parse(sqlWhere.getQueryBindings()), getDomainClass());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void remove(Province province) {
        deleteById(province.getId());
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }

    @Nullable
    private Province executeWhere(SqlWhere sqlWhere) {
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> params = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(sql, params, Province.class);
    }
}
