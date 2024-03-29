package com.bachlinh.order.web.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerMedia;
import com.bachlinh.order.entity.model.CustomerMedia_;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.web.repository.spi.AbstractRepository;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operation;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlJoin;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.web.repository.spi.CustomerMediaRepository;

import jakarta.persistence.criteria.JoinType;

import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class CustomerMediaRepositoryImpl extends AbstractRepository<String, CustomerMedia> implements CustomerMediaRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public CustomerMediaRepositoryImpl(DependenciesResolver resolver) {
        super(CustomerMedia.class, resolver);
    }

    @Override
    public CustomerMedia getCustomerMedia(Customer owner) {
        Join customerJoin = Join.builder().attribute(CustomerMedia_.CUSTOMER).type(JoinType.INNER).build();
        Where ownerWhere = Where.builder().attribute(Customer_.ID).value(owner).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(CustomerMedia.class);
        SqlJoin sqlJoin = sqlSelect.join(customerJoin);
        SqlWhere sqlWhere = sqlJoin.where(ownerWhere, Customer.class);

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getSingleResult(query, attributes, getDomainClass());
    }

    @Override
    public void deleteCustomerMedia(CustomerMedia customerMedia) {
        if (customerMedia == null) {
            return;
        }
        delete(customerMedia);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new CustomerMediaRepositoryImpl(containerResolver.getDependenciesResolver());
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{CustomerMediaRepository.class};
    }
}
