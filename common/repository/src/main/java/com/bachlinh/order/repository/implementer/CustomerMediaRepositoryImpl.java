package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerMedia;
import com.bachlinh.order.entity.model.CustomerMedia_;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.query.Join;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlJoin;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.repository.utils.QueryUtils;
import com.bachlinh.order.repository.CustomerMediaRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import jakarta.persistence.criteria.JoinType;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class CustomerMediaRepositoryImpl extends AbstractRepository<String, CustomerMedia> implements CustomerMediaRepository {

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
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public void deleteCustomerMedia(CustomerMedia customerMedia) {
        if (customerMedia == null) {
            return;
        }
        delete(customerMedia);
    }
}
