package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.entity.model.Province_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.ProvinceRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelection;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.query.WhereOperation;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class ProvinceRepositoryImpl extends AbstractRepository<Province, Integer> implements ProvinceRepository {

    @DependenciesInitialize
    @ActiveReflection
    public ProvinceRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Province.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public Province getProvinceById(String provinceId) {
        Where idWhere = Where.builder().attribute(Province_.ID).operator(Operator.EQ).value(provinceId).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelection sqlSelection = sqlBuilder.from(Province.class);
        WhereOperation whereOperation = sqlSelection.where(idWhere);
        String sql = whereOperation.getNativeQuery();
        Map<String, Object> attributes = new HashMap<>(1);
        whereOperation.getQueryBindings().forEach(queryBinding -> attributes.put(queryBinding.attribute(), queryBinding.value()));
        List<Province> results = executeNativeQuery(sql, attributes, Province.class);
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
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
        SqlSelection sqlSelection = sqlBuilder.from(Province.class);
        String query = sqlSelection.getNativeQuery();
        return executeNativeQuery(query, Collections.emptyMap(), Province.class);
    }

    @Override
    public Collection<Province> getProvincesById(Collection<String> ids) {
        return findAllById(ids.stream().map(Integer::parseInt).toList());
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
}
