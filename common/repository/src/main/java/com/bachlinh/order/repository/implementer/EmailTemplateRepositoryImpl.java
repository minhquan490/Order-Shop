package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.EmailTemplateFolder_;
import com.bachlinh.order.entity.model.EmailTemplate_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryBase;
import com.bachlinh.order.entity.repository.query.Join;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.Select;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlJoin;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.utils.QueryUtils;
import com.bachlinh.order.repository.EmailTemplateRepository;
import jakarta.persistence.criteria.JoinType;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class EmailTemplateRepositoryImpl extends AbstractRepository<String, EmailTemplate> implements EmailTemplateRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public EmailTemplateRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(EmailTemplate.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public EmailTemplate saveEmailTemplate(EmailTemplate emailTemplate) {
        return save(emailTemplate);
    }

    @Override
    public EmailTemplate updateEmailTemplate(EmailTemplate emailTemplate) {
        return save(emailTemplate);
    }

    @Override
    public EmailTemplate getEmailTemplateById(String id, Customer owner) {
        Select idSelect = Select.builder().column(EmailTemplate_.ID).build();
        Select nameSelect = Select.builder().column(EmailTemplate_.NAME).build();
        Select titleSelect = Select.builder().column(EmailTemplate_.TITLE).build();
        Select contentSelect = Select.builder().column(EmailTemplate_.CONTENT).build();
        Select expiryPolicySelect = Select.builder().column(EmailTemplate_.EXPIRY_POLICY).build();
        Select totalArgumentSelect = Select.builder().column(EmailTemplate_.TOTAL_ARGUMENT).build();
        Select paramsSelect = Select.builder().column(EmailTemplate_.PARAMS).build();
        Join ownerJoin = Join.builder().attribute(EmailTemplate_.OWNER).type(JoinType.INNER).build();
        var idWhere = Where.builder().attribute(EmailTemplate_.ID).value(id).operation(Operation.EQ).build();
        var ownerWhere = Where.builder().attribute(Customer_.ID).value(owner.getId()).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTemplate.class);
        sqlSelect.select(idSelect)
                .select(nameSelect)
                .select(titleSelect)
                .select(contentSelect)
                .select(expiryPolicySelect)
                .select(totalArgumentSelect)
                .select(paramsSelect);
        SqlJoin sqlJoin = sqlSelect.join(ownerJoin);
        SqlWhere sqlWhere = sqlJoin.where(idWhere).and(ownerWhere, Customer.class);
        return getEmailTemplate(sqlWhere);
    }

    @Override
    public EmailTemplate getDefaultEmailTemplate(String name) {
        Select idSelect = Select.builder().column(EmailTemplate_.ID).build();
        Select nameSelect = Select.builder().column(EmailTemplate_.NAME).build();
        Select titleSelect = Select.builder().column(EmailTemplate_.TITLE).build();
        Select contentSelect = Select.builder().column(EmailTemplate_.CONTENT).build();
        Select expiryPolicySelect = Select.builder().column(EmailTemplate_.EXPIRY_POLICY).build();
        Select totalArgumentSelect = Select.builder().column(EmailTemplate_.TOTAL_ARGUMENT).build();
        Select paramsSelect = Select.builder().column(EmailTemplate_.PARAMS).build();
        var idWhere = Where.builder().attribute(EmailTemplate_.NAME).value(name).operation(Operation.EQ).build();
        var ownerNull = Where.builder().attribute(EmailTemplate_.OWNER).operation(Operation.NULL).build();
        var folderNull = Where.builder().attribute(EmailTemplate_.FOLDER).operation(Operation.NULL).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTemplate.class);
        sqlSelect.select(idSelect)
                .select(nameSelect)
                .select(titleSelect)
                .select(contentSelect)
                .select(expiryPolicySelect)
                .select(totalArgumentSelect)
                .select(paramsSelect);
        SqlWhere sqlWhere = sqlSelect.where(idWhere).and(ownerNull).and(folderNull);
        return getEmailTemplate(sqlWhere);
    }

    @Override
    public EmailTemplate getEmailTemplateForUpdate(String id, Customer owner) {
        Select idSelect = Select.builder().column(EmailTemplate_.ID).build();
        Select nameSelect = Select.builder().column(EmailTemplate_.NAME).build();
        Select titleSelect = Select.builder().column(EmailTemplate_.TITLE).build();
        Select contentSelect = Select.builder().column(EmailTemplate_.CONTENT).build();
        Select expiryPolicySelect = Select.builder().column(EmailTemplate_.EXPIRY_POLICY).build();
        Select totalArgumentSelect = Select.builder().column(EmailTemplate_.TOTAL_ARGUMENT).build();
        Select paramsSelect = Select.builder().column(EmailTemplate_.PARAMS).build();
        Select folderIdSelect = Select.builder().column(EmailTemplateFolder_.ID).build();
        Join folderJoin = Join.builder().attribute(EmailTemplate_.FOLDER).type(JoinType.INNER).build();
        Where idWhere = Where.builder().attribute(EmailTemplate_.ID).value(id).operation(Operation.EQ).build();
        Where ownerWhere = Where.builder().attribute(EmailTemplate_.OWNER).value(owner.getId()).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTemplate.class);
        sqlSelect.select(idSelect)
                .select(nameSelect)
                .select(titleSelect)
                .select(contentSelect)
                .select(expiryPolicySelect)
                .select(totalArgumentSelect)
                .select(paramsSelect)
                .select(folderIdSelect);
        SqlJoin sqlJoin = sqlSelect.join(folderJoin);
        SqlWhere sqlWhere = sqlJoin.where(idWhere).where(ownerWhere);
        return getEmailTemplate(sqlWhere);
    }

    @Override
    public Collection<EmailTemplate> getEmailTemplates(Customer owner) {
        Select idSelect = Select.builder().column(EmailTemplate_.ID).build();
        Select nameSelect = Select.builder().column(EmailTemplate_.NAME).build();
        Select titleSelect = Select.builder().column(EmailTemplate_.TITLE).build();
        Select contentSelect = Select.builder().column(EmailTemplate_.CONTENT).build();
        Select expiryPolicySelect = Select.builder().column(EmailTemplate_.EXPIRY_POLICY).build();
        Select totalArgumentSelect = Select.builder().column(EmailTemplate_.TOTAL_ARGUMENT).build();
        Select paramsSelect = Select.builder().column(EmailTemplate_.PARAMS).build();
        Join ownerJoin = Join.builder().attribute(EmailTemplate_.OWNER).type(JoinType.INNER).build();
        var ownerWhere = Where.builder().attribute(Customer_.ID).operation(Operation.EQ).value(owner.getId()).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTemplate.class);
        sqlSelect.select(idSelect)
                .select(nameSelect)
                .select(titleSelect)
                .select(contentSelect)
                .select(expiryPolicySelect)
                .select(totalArgumentSelect)
                .select(paramsSelect);
        SqlJoin sqlJoin = sqlSelect.join(ownerJoin);
        SqlWhere sqlWhere = sqlJoin.where(ownerWhere, Customer.class);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, EmailTemplate.class);
    }

    @Override
    public Collection<EmailTemplate> getEmailTemplates(Collection<String> ids, Customer owner) {
        Select idSelect = Select.builder().column(EmailTemplate_.ID).build();
        Select nameSelect = Select.builder().column(EmailTemplate_.NAME).build();
        Select titleSelect = Select.builder().column(EmailTemplate_.TITLE).build();
        Select contentSelect = Select.builder().column(EmailTemplate_.CONTENT).build();
        Select expiryPolicySelect = Select.builder().column(EmailTemplate_.EXPIRY_POLICY).build();
        Select totalArgumentSelect = Select.builder().column(EmailTemplate_.TOTAL_ARGUMENT).build();
        Select paramsSelect = Select.builder().column(EmailTemplate_.PARAMS).build();
        Join ownerJoin = Join.builder().attribute(EmailTemplate_.OWNER).type(JoinType.INNER).build();
        var idWhere = Where.builder().attribute(EmailTemplate_.ID).value(ids.toArray()).operation(Operation.IN).build();
        var ownerWhere = Where.builder().attribute(Customer_.ID).value(owner.getId()).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTemplate.class);
        sqlSelect.select(idSelect)
                .select(nameSelect)
                .select(titleSelect)
                .select(contentSelect)
                .select(expiryPolicySelect)
                .select(totalArgumentSelect)
                .select(paramsSelect);
        SqlJoin sqlJoin = sqlSelect.join(ownerJoin);
        SqlWhere sqlWhere = sqlJoin.where(idWhere).and(ownerWhere, Customer.class);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, EmailTemplate.class);
    }

    @Override
    public boolean isEmailTemplateExisted(String id, Customer owner) {
        return getEmailTemplateById(id, owner) != null;
    }

    @Override
    public void deleteEmailTemplate(EmailTemplate emailTemplate) {
        delete(emailTemplate);
    }

    @Nullable
    private EmailTemplate getEmailTemplate(SqlWhere sqlWhere) {
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(sql, attributes, EmailTemplate.class);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new EmailTemplateRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{EmailTemplateRepository.class};
    }
}
