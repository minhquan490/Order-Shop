package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.entity.model.EmailFolders_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryBase;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.Select;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.utils.QueryUtils;
import com.bachlinh.order.repository.EmailFoldersRepository;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class EmailFoldersRepositoryImpl extends AbstractRepository<String, EmailFolders> implements EmailFoldersRepository, RepositoryBase {

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
    public EmailFolders saveEmailFolder(EmailFolders emailFolders) {
        return save(emailFolders);
    }

    @Override
    public EmailFolders getEmailFolderByName(String name, Customer owner) {
        Where folderNameWhere = Where.builder().attribute(EmailFolders_.NAME).value(name).operation(Operation.EQ).build();
        return getEmailFolders(owner, folderNameWhere);
    }

    @Override
    public EmailFolders getEmailFolderById(String id, Customer owner) {
        Where folderIdWhere = Where.builder().attribute(EmailFolders_.ID).value(id).operation(Operation.EQ).build();
        return getEmailFolders(owner, folderIdWhere);
    }

    @Override
    public EmailFolders updateEmailFolder(EmailFolders emailFolders) {
        return save(emailFolders);
    }

    @Override
    public void bulkSave(Collection<EmailFolders> folders) {
        saveAll(folders);
    }

    @Override
    public void delete(String id) {
        deleteById(id);
    }

    @Override
    public Collection<EmailFolders> getEmailFoldersOfCustomer(Customer owner) {
        Where ownerWhere = Where.builder().attribute(EmailFolders_.OWNER).value(owner).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailFolders.class);
        SqlWhere sqlWhere = sqlSelect.where(ownerWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, EmailFolders.class);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new EmailFoldersRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{EmailFoldersRepository.class};
    }

    @Nullable
    private EmailFolders getEmailFolders(Customer owner, Where folderIdWhere) {
        Select idSelect = Select.builder().column(EmailFolders_.ID).build();
        Select nameSelect = Select.builder().column(EmailFolders_.NAME).build();
        Select timeCreatedSelect = Select.builder().column(EmailFolders_.TIME_CREATED).build();
        Select emailClearPolicySelect = Select.builder().column(EmailFolders_.EMAIL_CLEAR_POLICY).build();
        Where ownerWhere = Where.builder().attribute(EmailFolders_.OWNER).value(owner).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailFolders.class);
        sqlSelect.select(idSelect).select(nameSelect).select(timeCreatedSelect).select(emailClearPolicySelect);
        SqlWhere sqlWhere = sqlSelect.where(folderIdWhere).and(ownerWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(sql, attributes, EmailFolders.class);
    }
}
