package com.bachlinh.order.entity.context;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import org.apache.lucene.store.Directory;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.annotation.Label;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.exception.system.common.NoTransactionException;
import com.bachlinh.order.entity.index.spi.SearchManager;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryManager;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.springframework.util.StringUtils;

public abstract class AbstractEntityContext implements EntityContext {

    private final SearchManager searchManager;
    private final String tableName;
    private final Map<String, String> mappedFieldColumns;
    private final Class<?> entityType;
    private final Class<?> idType;
    private final BaseEntity<?> baseEntity;
    private volatile Integer previousId;
    private volatile int createIdTime = -1;
    private final String prefix;

    protected AbstractEntityContext(Class<?> entity, SearchManager searchManager) {
        this.entityType = entity;
        this.searchManager = searchManager;
        this.tableName = resolveTableName(entity);
        this.mappedFieldColumns = resolveMappedFieldColumn(entity);
        this.baseEntity = createEntity(entity);
        this.idType = queryIdType(entityType);
        this.prefix = createIdPrefix(entityType);
    }

    @Override
    public String getTableName() {
        return this.tableName;
    }

    @Override
    public String getColumn(String entityFieldName) {
        String template = "%s.%s AS '%s.%s'";
        if (entityFieldName.equals("*")) {
            var fields = this.mappedFieldColumns
                    .values()
                    .stream()
                    .filter(s -> !s.contains("."))
                    .map(s -> String.format(template, getTableName(), s, getTableName(), s))
                    .toList()
                    .toArray(new String[0]);
            return String.join(", ", fields);
        }
        String databaseColumnName = this.mappedFieldColumns.get(entityFieldName);
        if (!StringUtils.hasText(databaseColumnName)) {
            throw new PersistenceException(String.format("No mapped column for field [%s] in table [%s]", entityFieldName, tableName));
        }
        return databaseColumnName;
    }

    @Override
    public BaseEntity<?> getEntity() {
        return (BaseEntity<?>) ((AbstractEntity<?>) baseEntity).clone();
    }

    @Override
    public Collection<String> search(String keyword) {
        return searchManager.search(getEntity().getClass(), keyword);
    }

    @Override
    public synchronized Object getNextId() {
        if (this.previousId == null) {
            try {
                this.previousId = configLastId();
            } catch (Exception e) {
                throw new PersistenceException(String.format("Can not get next id of entity [%s]", entityType.getName()), e);
            }
        }
        if (this.createIdTime < 0) {
            throw new NoTransactionException("You must call beginTransaction() before this method");
        }
        this.previousId += 1;
        this.createIdTime += 1;
        if (prefix == null && (idType.equals(int.class) || idType.equals(Integer.class))) {
            return previousId;
        }
        return prefix + String.format("%06d", previousId);
    }

    @Override
    public void beginTransaction() {
        this.createIdTime = 0;
    }

    @Override
    public void commit() {
        this.createIdTime = -1;
    }

    @Override
    public synchronized void rollback() {
        if (this.createIdTime > 0) {
            this.previousId -= createIdTime;
            this.createIdTime = -1;
        }
    }

    @Override
    public Directory getDirectory(Class<?> entity) {
        return searchManager.getDirectory(entity);
    }

    @Override
    public void analyze(Object entity) {
        searchManager.analyze(entity);
    }

    @Override
    public void analyze(Collection<Object> entities) {
        searchManager.analyze(entities);
    }

    protected Class<?> getEntityType() {
        return entityType;
    }

    protected Map<String, String> getMappedFieldColumns() {
        return mappedFieldColumns;
    }

    protected abstract DependenciesResolver getResolver();

    protected abstract Initializer<BaseEntity<?>> getEntityInitializer();

    private String resolveTableName(Class<?> entityType) {
        if (entityType.isAnnotationPresent(Entity.class)) {
            Table table = entityType.getAnnotation(Table.class);
            if (table == null) {
                throw new CriticalException(String.format("Missing @Table on entity, invalid type [%s]", entityType.getName()));
            }
            String name = table.name();
            if (!StringUtils.hasText(name)) {
                throw new CriticalException(String.format("Table name is missing on type [%s]", entityType.getName()));
            }
            return name;
        } else {
            throw new PersistenceException(String.format("Only entity is allowed, invalid type detected [%s]", entityType.getName()));
        }
    }

    private Map<String, String> resolveMappedFieldColumn(Class<?> entityType) {
        List<Field> mappedColumnField = Stream.of(entityType.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(JoinColumn.class))
                .toList();
        Map<String, String> resolvedMappedColumnField = new TreeMap<>();
        resolveFields(resolvedMappedColumnField, mappedColumnField);
        Class<?> abstractEntity = entityType.getSuperclass();
        List<Field> commonFields = Stream.of(abstractEntity.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(JoinColumn.class))
                .toList();
        resolveFields(resolvedMappedColumnField, commonFields);
        return resolvedMappedColumnField;
    }

    private void resolveFields(Map<String, String> resolvedMappedColumnField, List<Field> fields) {
        fields.forEach(field -> {
            if (field.isAnnotationPresent(Column.class)) {
                resolvedMappedColumnField.put(field.getName(), field.getAnnotation(Column.class).name());
            }
            if (field.isAnnotationPresent(JoinColumn.class)) {
                resolvedMappedColumnField.put(field.getName(), field.getAnnotation(JoinColumn.class).name());
            }
        });
    }

    private BaseEntity<?> createEntity(Class<?> entityType) {
        return getEntityInitializer().getObject(entityType);
    }

    private int configLastId() throws ClassNotFoundException {
        String repositoryPattern = "com.bachlinh.order.repository.{0}Repository";
        String repositoryName = MessageFormat.format(repositoryPattern, entityType.getSimpleName());
        Class<?> repositoryClass = Class.forName(repositoryName);
        RepositoryManager repositoryManager = getResolver().resolveDependencies(RepositoryManager.class);
        AbstractRepository<?, ?> repository = (AbstractRepository<?, ?>) repositoryManager.getRepository(repositoryClass);
        String sql = MessageFormat.format("SELECT MAX(ID) FROM {0}", entityType.getAnnotation(Table.class).name());
        List<?> result = repository.getResultList(sql, Collections.emptyMap(), idType);
        if (result.isEmpty()) {
            return 0;
        }
        if (result.get(0) instanceof String idString) {
            String suffixId = idString.split("-")[1];
            return Integer.parseInt(suffixId);
        }
        if (result.get(0) instanceof Integer idInt) {
            return idInt;
        }
        return 0;
    }

    private Class<?> queryIdType(Class<?> entityType) {
        for (Field field : entityType.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field.getType();
            }
        }
        throw new PersistenceException("Can not find type of entity id");
    }

    private String createIdPrefix(Class<?> entityType) {
        Label label = entityType.getAnnotation(Label.class);
        if (label != null) {
            return entityType.getAnnotation(Label.class).value();
        } else {
            return null;
        }
    }
}
