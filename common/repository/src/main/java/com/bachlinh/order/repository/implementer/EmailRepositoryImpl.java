package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.entity.model.EmailFolders_;
import com.bachlinh.order.entity.model.EmailTrash_;
import com.bachlinh.order.entity.model.Email_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryBase;
import com.bachlinh.order.entity.repository.query.Join;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.OrderBy;
import com.bachlinh.order.entity.repository.query.Select;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlJoin;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.repository.utils.QueryUtils;
import com.bachlinh.order.repository.EmailRepository;
import jakarta.persistence.criteria.JoinType;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class EmailRepositoryImpl extends AbstractRepository<String, Email> implements EmailRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public EmailRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Email.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public Email saveEmail(Email email) {
        return save(email);
    }

    @Override
    public Email getEmailOfCustomerById(String id, Customer owner) {
        Select idSelect = Select.builder().column(Email_.ID).build();
        Select contentSelect = Select.builder().column(Email_.CONTENT).build();
        Select receivedTimeSelect = Select.builder().column(Email_.RECEIVED_TIME).build();
        Select titleSelect = Select.builder().column(Email_.TITLE).build();
        Select readSelect = Select.builder().column(Email_.READ).build();
        Select toCustomerEmailSelect = Select.builder().column(Customer_.EMAIL).alias("TO_CUSTOMER.EMAIL").build();
        Select folderNameSelect = Select.builder().column(EmailFolders_.NAME).build();
        Join toCustomerJoin = Join.builder().attribute(Email_.TO_CUSTOMER).type(JoinType.INNER).build();
        Join emailFolderJoin = Join.builder().attribute(Email_.FOLDER).type(JoinType.LEFT).build();
        Where idWhere = Where.builder().attribute(Email_.ID).value(id).operation(Operation.EQ).build();
        Where ownerWhere = Where.builder().attribute(Email_.TO_CUSTOMER).value(owner).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Email.class);
        sqlSelect.select(idSelect)
                .select(contentSelect)
                .select(receivedTimeSelect)
                .select(titleSelect)
                .select(readSelect)
                .select(toCustomerEmailSelect, Customer.class)
                .select(folderNameSelect, EmailFolders.class);
        SqlJoin sqlJoin = sqlSelect.join(toCustomerJoin).join(emailFolderJoin);
        SqlWhere sqlWhere = sqlJoin.where(idWhere).and(ownerWhere);
        return getEmail(sqlWhere);
    }

    @Override
    public Email getEmailForAddToTrash(String id, Customer owner) {
        Select idSelect = Select.builder().column(Email_.ID).build();
        Where idWhere = Where.builder().attribute(Email_.ID).value(id).operation(Operation.EQ).build();
        Where ownerWhere = Where.builder().attribute(Email_.TO_CUSTOMER).value(owner).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Email.class);
        sqlSelect.select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(idWhere).and(ownerWhere);
        return getEmail(sqlWhere);
    }

    @Override
    public Collection<Email> getAllEmailByIds(Iterable<String> ids) {
        Select idSelect = Select.builder().column(Email_.ID).build();
        Where idsWhere = Where.builder().attribute(Email_.ID).value(((Collection<String>) ids).toArray(new String[0])).operation(Operation.IN).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Email.class);
        sqlSelect.select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(idsWhere);
        return getEmails(sqlWhere);
    }

    @Override
    public Collection<Email> getEmailsForAddToTrash(Iterable<String> ids, Customer owner) {
        Select idSelect = Select.builder().column(Email_.ID).build();
        Select contentSelect = Select.builder().column(Email_.CONTENT).build();
        Select titleSelect = Select.builder().column(Email_.TITLE).build();
        Where idsWhere = Where.builder().attribute(Email_.ID).value(((Collection<String>) ids).toArray(new String[0])).operation(Operation.IN).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Email.class);
        sqlSelect.select(idSelect).select(contentSelect).select(titleSelect);
        SqlWhere sqlWhere = sqlSelect.where(idsWhere);
        return getEmails(sqlWhere);
    }

    @Override
    public List<Email> getEmailsByFolderId(String folderId, Customer owner) {
        OrderBy receivedTimeOrderBy = OrderBy.builder().column(Email_.RECEIVED_TIME).type(OrderBy.Type.DESC).build();
        Join folderJoin = Join.builder().attribute(Email_.FOLDER).type(JoinType.INNER).build();
        Join fromCustomerJoin = Join.builder().attribute(Email_.FROM_CUSTOMER).type(JoinType.INNER).build();
        Where toCustomerWhere = Where.builder().attribute(Email_.TO_CUSTOMER).value(owner).operation(Operation.EQ).build();
        Where emailFoldersWhere = Where.builder().attribute(EmailFolders_.ID).value(folderId).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Email.class);
        SqlJoin sqlJoin = sqlSelect.join(folderJoin).join(fromCustomerJoin);
        SqlWhere sqlWhere = sqlJoin.where(toCustomerWhere).and(emailFoldersWhere);
        sqlWhere.orderBy(receivedTimeOrderBy);
        return getEmails(sqlWhere);
    }

    @Override
    public Collection<Email> getResultSearch(Iterable<String> ids, Customer owner) {
        Select idSelect = Select.builder().column(Email_.ID).build();
        Select contentSelect = Select.builder().column(Email_.CONTENT).build();
        Select receivedTimeSelect = Select.builder().column(Email_.RECEIVED_TIME).build();
        Select titleSelect = Select.builder().column(Email_.TITLE).build();
        Select readSelect = Select.builder().column(Email_.READ).build();
        Select mediaTypeSelect = Select.builder().column(Email_.MEDIA_TYPE).build();
        Select toCustomerEmailSelect = Select.builder().column(Customer_.EMAIL).alias("TO_CUSTOMER.EMAIL").build();
        Select folderNameSelect = Select.builder().column(EmailFolders_.NAME).build();
        Join toCustomerJoin = Join.builder().attribute(Email_.TO_CUSTOMER).type(JoinType.LEFT).build();
        Join emailFolderJoin = Join.builder().attribute(Email_.FOLDER).type(JoinType.LEFT).build();
        Where idsWhere = Where.builder().attribute(Email_.ID).value(ids).operation(Operation.IN).build();
        Where ownerWhere = Where.builder().attribute(Email_.TO_CUSTOMER).value(owner).operation(Operation.IN).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Email.class);
        sqlSelect.select(idSelect)
                .select(contentSelect)
                .select(receivedTimeSelect)
                .select(titleSelect)
                .select(readSelect)
                .select(mediaTypeSelect)
                .select(toCustomerEmailSelect, Customer.class)
                .select(folderNameSelect, EmailFolders.class);
        SqlJoin sqlJoin = sqlSelect.join(toCustomerJoin).join(emailFolderJoin);
        SqlWhere sqlWhere = sqlJoin.where(idsWhere).and(ownerWhere);
        return getEmails(sqlWhere);
    }

    @Override
    public Collection<Email> getEmailForRestore(Integer emailTrashId, Iterable<String> ids) {
        Select idSelect = Select.builder().column(Email_.ID).build();
        Join emailTrashJoin = Join.builder().attribute(Email_.EMAIL_TRASH).type(JoinType.RIGHT).build();
        Where idWhere = Where.builder().attribute(Email_.ID).value(ids).operation(Operation.IN).build();
        Where emailTrashIdWhere = Where.builder().attribute(EmailTrash_.ID).value(emailTrashId).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Email.class);
        sqlSelect.select(idSelect);
        SqlJoin sqlJoin = sqlSelect.join(emailTrashJoin);
        SqlWhere sqlWhere = sqlJoin.where(idWhere).and(emailTrashIdWhere);
        return getEmails(sqlWhere);
    }

    @Override
    public Collection<Email> getEmailsOfCustomer(Customer owner) {
        Where toCustomerWhere = Where.builder().attribute(Email_.TO_CUSTOMER).value(owner).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        SqlWhere sqlWhere = sqlSelect.where(toCustomerWhere);

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getResultList(query, attributes, getDomainClass());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void deleteEmails(Collection<String> ids) {
        deleteAllById(ids);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void deleteEmail(Collection<Email> emails) {
        deleteAll(emails);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new EmailRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{EmailRepository.class};
    }

    private List<Email> getEmails(SqlWhere sqlWhere) {
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, Email.class);
    }

    @Nullable
    private Email getEmail(SqlWhere sqlWhere) {
        var results = getEmails(sqlWhere);
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }
}
