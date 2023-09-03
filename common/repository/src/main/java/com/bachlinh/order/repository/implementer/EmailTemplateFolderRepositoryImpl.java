package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import com.bachlinh.order.entity.model.EmailTemplateFolder_;
import com.bachlinh.order.entity.model.EmailTemplate_;
import com.bachlinh.order.repository.EmailTemplateFolderRepository;
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
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class EmailTemplateFolderRepositoryImpl extends AbstractRepository<EmailTemplateFolder, String> implements EmailTemplateFolderRepository {

    @DependenciesInitialize
    @ActiveReflection
    public EmailTemplateFolderRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(EmailTemplateFolder.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public EmailTemplateFolder saveTemplateFolder(EmailTemplateFolder folder) {
        return save(folder);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public EmailTemplateFolder updateTemplateFolder(EmailTemplateFolder folder) {
        return save(folder);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    public void deleteTemplateFolder(EmailTemplateFolder folder) {
        delete(folder);
    }

    @Override
    public boolean isEmailTemplateFolderNameExisted(String emailTemplateFolderName) {
        Select idSelect = Select.builder().column(EmailTemplateFolder_.ID).build();
        Where emailTemplateFolderNameWhere = Where.builder().attribute(EmailTemplateFolder_.NAME).value(emailTemplateFolderName).operator(Operator.EQ).build();
        Where ownerWhere = Where.builder().attribute(EmailTemplateFolder_.OWNER).value(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTemplateFolder.class);
        sqlSelect.select(idSelect);
        SqlWhere sqlWhere = sqlSelect.where(emailTemplateFolderNameWhere).where(ownerWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return !executeNativeQuery(sql, attributes, EmailTemplateFolder.class).isEmpty();
    }

    @Override
    public boolean isEmailTemplateFolderIdExisted(String id) {
        return existsById(id);
    }

    @Override
    public EmailTemplateFolder getEmailTemplateFolderById(String id, Customer owner) {
        Select idSelect = Select.builder().column(EmailTemplateFolder_.ID).build();
        Select nameSelect = Select.builder().column(EmailTemplateFolder_.NAME).build();
        Select timeTemplateClearedSelect = Select.builder().column(EmailTemplateFolder_.CLEAR_TEMPLATE_POLICY).build();
        Select emailTemplateIdSelect = Select.builder().column(EmailTemplate_.ID).build();
        Select emailTemplateNameSelect = Select.builder().column(EmailTemplate_.NAME).build();
        Select emailTemplateTitleSelect = Select.builder().column(EmailTemplate_.TITLE).build();
        Join emailTemplatesJoin = Join.builder().attribute(EmailTemplateFolder_.EMAIL_TEMPLATES).type(JoinType.LEFT).build();
        Where idWhere = Where.builder().attribute(EmailTemplateFolder_.ID).value(id).operator(Operator.EQ).build();
        Where ownerWhere = Where.builder().attribute(EmailTemplateFolder_.OWNER).value(owner).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTemplateFolder.class);
        sqlSelect.select(idSelect)
                .select(nameSelect)
                .select(timeTemplateClearedSelect)
                .select(emailTemplateIdSelect, EmailTemplate.class)
                .select(emailTemplateNameSelect, EmailTemplate.class)
                .select(emailTemplateTitleSelect, EmailTemplate.class);
        SqlJoin sqlJoin = sqlSelect.join(emailTemplatesJoin);
        return getEmailTemplateFolder(ownerWhere, sqlJoin.where(idWhere));
    }


    @Override
    public EmailTemplateFolder getEmailTemplateFolderByName(String name, Customer owner) {
        Select idSelect = Select.builder().column(EmailTemplateFolder_.ID).build();
        Select nameSelect = Select.builder().column(EmailTemplateFolder_.NAME).build();
        Select clearTemplatePolicySelect = Select.builder().column(EmailTemplateFolder_.CLEAR_TEMPLATE_POLICY).build();
        Where emailTemplateFolderNameWhere = Where.builder().attribute(EmailTemplateFolder_.NAME).value(name).operator(Operator.EQ).build();
        Where ownerWhere = Where.builder().attribute(EmailTemplateFolder_.OWNER).value(owner).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTemplateFolder.class);
        sqlSelect.select(idSelect).select(nameSelect).select(clearTemplatePolicySelect);
        return getEmailTemplateFolder(ownerWhere, sqlSelect.where(emailTemplateFolderNameWhere));
    }

    @Override
    public EmailTemplateFolder getEmailTemplateFolderHasCustomer(String id) {
        Select idSelect = Select.builder().column(EmailTemplateFolder_.ID).build();
        Select nameSelect = Select.builder().column(EmailTemplateFolder_.NAME).build();
        Select clearTemplatePolicySelect = Select.builder().column(EmailTemplateFolder_.CLEAR_TEMPLATE_POLICY).build();
        Join ownerJoin = Join.builder().attribute(EmailTemplateFolder_.OWNER).type(JoinType.INNER).build();
        Where idWhere = Where.builder().attribute(EmailTemplateFolder_.ID).value(id).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTemplateFolder.class);
        sqlSelect.select(idSelect).select(nameSelect).select(clearTemplatePolicySelect);
        SqlJoin sqlJoin = sqlSelect.join(ownerJoin);
        SqlWhere sqlWhere = sqlJoin.where(idWhere);
        return processNativeQuery(sqlWhere);
    }

    @Override
    public EmailTemplateFolder getEmailTemplateFolderForUpdate(String id) {
        Select idSelect = Select.builder().column(EmailTemplateFolder_.ID).build();
        Select nameSelect = Select.builder().column(EmailTemplateFolder_.NAME).build();
        Where idWhere = Where.builder().attribute(EmailTemplateFolder_.ID).value(id).operator(Operator.EQ).build();
        Where ownerWhere = Where.builder().attribute(EmailTemplateFolder_.OWNER).value(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTemplateFolder.class);
        sqlSelect.select(idSelect).select(nameSelect);
        SqlWhere sqlWhere = sqlSelect.where(idWhere).where(ownerWhere);
        return processNativeQuery(sqlWhere);
    }

    @Override
    public Collection<EmailTemplateFolder> getEmailTemplateFolders(String customerId) {
        Select idSelect = Select.builder().column(EmailTemplateFolder_.ID).build();
        Select nameSelect = Select.builder().column(EmailTemplateFolder_.NAME).build();
        Select clearTemplatePolicySelect = Select.builder().column(EmailTemplateFolder_.CLEAR_TEMPLATE_POLICY).build();
        Select emailTemplateIdSelect = Select.builder().column(EmailTemplate_.ID).build();
        Join ownerJoin = Join.builder().attribute(EmailTemplateFolder_.OWNER).type(JoinType.INNER).build();
        Join emailTemplatesJoin = Join.builder().attribute(EmailTemplateFolder_.EMAIL_TEMPLATES).type(JoinType.LEFT).build();
        Where ownerIdWhere = Where.builder().attribute(EmailTemplateFolder_.OWNER).value(customerId).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(EmailTemplateFolder.class);
        sqlSelect.select(idSelect)
                .select(nameSelect)
                .select(clearTemplatePolicySelect)
                .select(emailTemplateIdSelect, EmailTemplate.class);
        SqlJoin sqlJoin = sqlSelect.join(ownerJoin).join(emailTemplatesJoin);
        SqlWhere sqlWhere = sqlJoin.where(ownerIdWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return executeNativeQuery(sql, attributes, EmailTemplateFolder.class);
    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }

    @Nullable
    private EmailTemplateFolder getEmailTemplateFolder(Where ownerWhere, SqlWhere where) {
        SqlWhere sqlWhere = where.and(ownerWhere);
        return processNativeQuery(sqlWhere);
    }

    @Nullable
    private EmailTemplateFolder processNativeQuery(SqlWhere sqlWhere) {
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        var results = executeNativeQuery(sql, attributes, EmailTemplateFolder.class);
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }
}
