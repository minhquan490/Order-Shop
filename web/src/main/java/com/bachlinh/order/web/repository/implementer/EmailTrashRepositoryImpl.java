package com.bachlinh.order.web.repository.implementer;

import jakarta.persistence.criteria.JoinType;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.model.EmailTrash;
import com.bachlinh.order.entity.model.EmailTrash_;
import com.bachlinh.order.entity.model.Email_;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operation;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlJoin;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.web.repository.spi.AbstractRepository;
import com.bachlinh.order.web.repository.spi.EmailTrashRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@ActiveReflection
@RepositoryComponent
public class EmailTrashRepositoryImpl extends AbstractRepository<Integer, EmailTrash> implements EmailTrashRepository, RepositoryBase {

    @ActiveReflection
    @DependenciesInitialize
    public EmailTrashRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(EmailTrash.class, dependenciesResolver);
    }

    @Override
    public void saveEmailTrash(EmailTrash emailTrash) {
        save(emailTrash);
    }

    @Override
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
        Where ownerWhere = Where.builder().attribute(Customer_.ID).value(customer.getId()).operation(Operation.EQ).build();
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
        return getSingleResult(sql, attributes, EmailTrash.class);
    }

    @Override
    public void updateTrashes(Collection<EmailTrash> trashes) {
        saveAll(trashes);
    }

    @Override
    public void deleteTrash(EmailTrash trash) {
        if (trash == null) {
            return;
        }
        delete(trash);
    }

    @Override
    public Collection<EmailTrash> getTrashNeedClearing() {
        Select idSelect = Select.builder().column(EmailTrash_.ID).build();
        Timestamp removeTime = Timestamp.valueOf(LocalDateTime.now().plusMonths(-3));
        Where modifiedDateWhere = Where.builder().attribute(EmailTrash_.MODIFIED_BY).value(removeTime).operation(Operation.GE).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTrash.class);
        sqlSelect.select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(modifiedDateWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, EmailTrash.class);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new EmailTrashRepositoryImpl(containerResolver.getDependenciesResolver());
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{EmailTrashRepository.class};
    }
}
