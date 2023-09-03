package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.AbstractEntity_;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.model.EmailTrash;
import com.bachlinh.order.entity.model.EmailTrash_;
import com.bachlinh.order.entity.model.Email_;
import com.bachlinh.order.repository.EmailTrashRepository;
import com.bachlinh.order.repository.adapter.AbstractRepository;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlJoin;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.service.container.DependenciesResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@ActiveReflection
@RepositoryComponent
public class EmailTrashRepositoryImpl extends AbstractRepository<EmailTrash, Integer> implements EmailTrashRepository {

    @ActiveReflection
    @DependenciesInitialize
    public EmailTrashRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(EmailTrash.class, dependenciesResolver);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    public void saveEmailTrash(EmailTrash emailTrash) {
        save(emailTrash);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    public EmailTrash updateTrash(EmailTrash emailTrash) {
        return save(emailTrash);
    }

    @Override
    public EmailTrash getTrashOfCustomer(Customer customer) {
        Select idSelect = Select.builder().column(EmailTrash_.ID).build();
        Select emailIdSelect = Select.builder().column(Email_.ID).build();
        Select emailContentSelect = Select.builder().column(Email_.CONTENT).build();
        Select emailTitleSelect = Select.builder().column(Email_.TITLE).build();
        Join emailsJoin = Join.builder().attribute(EmailTrash_.EMAILS).type(JoinType.LEFT).build();
        Join ownerJoin = Join.builder().attribute(EmailTrash_.CUSTOMER).type(JoinType.INNER).build();
        Where ownerWhere = Where.builder().attribute(Customer_.ID).value(customer.getId()).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTrash.class);
        sqlSelect.select(idSelect)
                .select(emailIdSelect, Email.class)
                .select(emailContentSelect, Email.class)
                .select(emailTitleSelect, Email.class);
        SqlJoin sqlJoin = sqlSelect.join(emailsJoin).join(ownerJoin, Customer.class);
        SqlWhere sqlWhere = sqlJoin.where(ownerWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        var results = executeNativeQuery(sql, attributes, EmailTrash.class);
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    public void updateTrashes(Collection<EmailTrash> trashes) {
        saveAll(trashes);
    }

    @Override
    public Collection<EmailTrash> getTrashNeedClearing() {
        Select idSelect = Select.builder().column(EmailTrash_.ID).build();
        Timestamp removeTime = Timestamp.valueOf(LocalDateTime.now().plusMonths(-3));
        Where modifiedDateWhere = Where.builder().attribute(AbstractEntity_.MODIFIED_BY).value(removeTime).operator(Operator.GE).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTrash.class);
        sqlSelect.select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(modifiedDateWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return executeNativeQuery(sql, attributes, EmailTrash.class);
    }

    @Override
    @ActiveReflection
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
