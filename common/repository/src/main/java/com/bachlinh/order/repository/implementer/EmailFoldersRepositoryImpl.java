package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.entity.model.EmailFolders_;
import com.bachlinh.order.repository.EmailFoldersRepository;
import com.bachlinh.order.repository.adapter.AbstractRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class EmailFoldersRepositoryImpl extends AbstractRepository<EmailFolders, String> implements EmailFoldersRepository {

    @DependenciesInitialize
    @ActiveReflection
    public EmailFoldersRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(EmailFolders.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public boolean isFolderExisted(String folderName, Customer owner) {
        return getEmailFolderByName(folderName, owner) != null;
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public EmailFolders saveEmailFolder(EmailFolders emailFolders) {
        return save(emailFolders);
    }

    @Override
    public EmailFolders getEmailFolderByName(String name, Customer owner) {
        Where folderNameWhere = Where.builder().attribute(EmailFolders_.NAME).value(name).operator(Operator.EQ).build();
        return getEmailFolders(owner, folderNameWhere);
    }

    @Override
    public EmailFolders getEmailFolderById(String id, Customer owner) {
        Where folderIdWhere = Where.builder().attribute(EmailFolders_.ID).value(id).operator(Operator.EQ).build();
        return getEmailFolders(owner, folderIdWhere);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public EmailFolders updateEmailFolder(EmailFolders emailFolders) {
        return save(emailFolders);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void bulkSave(Collection<EmailFolders> folders) {
        saveAll(folders);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void delete(String id) {
        deleteById(id);
    }

    @Override
    public Collection<EmailFolders> getEmailFoldersOfCustomer(Customer owner) {
        Where ownerWhere = Where.builder().attribute(EmailFolders_.OWNER).value(owner).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailFolders.class);
        SqlWhere sqlWhere = sqlSelect.where(ownerWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return executeNativeQuery(sql, attributes, EmailFolders.class);
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }

    @Nullable
    private EmailFolders getEmailFolders(Customer owner, Where folderIdWhere) {
        Select idSelect = Select.builder().column(EmailFolders_.ID).build();
        Select nameSelect = Select.builder().column(EmailFolders_.NAME).build();
        Select timeCreatedSelect = Select.builder().column(EmailFolders_.TIME_CREATED).build();
        Select emailClearPolicySelect = Select.builder().column(EmailFolders_.EMAIL_CLEAR_POLICY).build();
        Where ownerWhere = Where.builder().attribute(EmailFolders_.OWNER).value(owner).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailFolders.class);
        sqlSelect.select(idSelect).select(nameSelect).select(timeCreatedSelect).select(emailClearPolicySelect);
        SqlWhere sqlWhere = sqlSelect.where(folderIdWhere).and(ownerWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        var results = executeNativeQuery(sql, attributes, EmailFolders.class);
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }
}
