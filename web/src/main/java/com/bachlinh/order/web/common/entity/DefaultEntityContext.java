package com.bachlinh.order.web.common.entity;

import com.bachlinh.order.annotation.ApplyOn;
import com.bachlinh.order.annotation.Formula;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.entity.EntityTrigger;
import com.bachlinh.order.entity.EntityValidator;
import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.context.EntityContext;
import com.bachlinh.order.entity.enums.FormulaApplyOn;
import com.bachlinh.order.entity.formula.FormulaProcessor;
import com.bachlinh.order.entity.formula.JoinFormulaProcessor;
import com.bachlinh.order.entity.formula.SelectFormulaProcessor;
import com.bachlinh.order.entity.formula.WhereFormulaProcessor;
import com.bachlinh.order.entity.index.spi.SearchManager;
import com.bachlinh.order.entity.model.AbstractEntity;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.exception.system.common.NoTransactionException;
import com.bachlinh.order.repository.adapter.AbstractRepository;
import com.bachlinh.order.repository.query.JoinMetadata;
import com.bachlinh.order.repository.query.JoinMetadataHolder;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.trigger.spi.AbstractTrigger;
import com.bachlinh.order.utils.UnsafeUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.SneakyThrows;
import org.apache.lucene.store.Directory;
import org.hibernate.annotations.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

public class DefaultEntityContext implements EntityContext, FormulaMetadata, JoinMetadataHolder {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Class<?> idType;
    private final BaseEntity<?> baseEntity;
    private final String prefix;
    private final String cacheRegion;
    private final List<EntityValidator<? extends BaseEntity<?>>> validators;
    private final List<EntityTrigger<? extends BaseEntity<?>>> triggers;
    private final SearchManager searchManager;
    private volatile Integer previousId;
    private volatile int createIdTime = -1;
    private final Class<?> entityType;
    private final DependenciesResolver dependenciesResolver;
    private final String tableName;
    private final Map<String, String> mappedFieldColumns;
    private final Map<String, JoinMetadata> joinMetadataMap;
    private final Collection<FormulaHolder> formulas;

    @SuppressWarnings("unchecked")
    public DefaultEntityContext(Class<?> entity, DependenciesResolver dependenciesResolver, SearchManager searchManager, Environment environment) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Init entity context for entity {}", entity.getSimpleName());
            }
            this.idType = queryIdType(entity);
            this.baseEntity = getBaseEntityInstance((Class<? extends BaseEntity<?>>) entity);
            this.validators = getValidators(entity, dependenciesResolver);
            this.triggers = getTriggers(entity, dependenciesResolver, environment);
            this.prefix = createIdPrefix(entity);
            this.cacheRegion = createCacheRegion(entity);
            this.searchManager = searchManager;
            this.entityType = entity;
            this.dependenciesResolver = dependenciesResolver;
            this.tableName = resolveTableName(entity);
            this.mappedFieldColumns = resolveMappedFieldColumn(entity);
            this.joinMetadataMap = resolveJoinMetadata(entity);
            this.formulas = resolveFormulas(entity);
        } catch (Exception e) {
            throw new PersistenceException("Can not instance entity with type [" + entity.getSimpleName() + "]", e);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("Init complete");
            }
        }
    }

    @Override
    public BaseEntity<?> getEntity() {
        return (BaseEntity<?>) ((AbstractEntity<?>) baseEntity).clone();
    }

    @Override
    public Collection<EntityValidator<?>> getValidators() {
        return new ArrayList<>(validators);
    }

    @Override
    public Collection<EntityTrigger<?>> getTrigger() {
        return new ArrayList<>(triggers);
    }

    @Override
    public Collection<String> search(String keyword) {
        return searchManager.search(getEntity().getClass(), keyword);
    }

    @Override
    public String getCacheRegion() {
        return cacheRegion;
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
    public Collection<FormulaProcessor> getTableProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return this.formulas.stream()
                .filter(formulaHolder -> formulaHolder.applyOn().equals(FormulaApplyOn.TABLE))
                .map(FormulaHolder::formulaProcessor)
                .filter(formulaProcessor -> !SelectFormulaProcessor.class.isAssignableFrom(formulaProcessor.getClass()) &&
                        !JoinFormulaProcessor.class.isAssignableFrom(formulaProcessor.getClass()) &&
                        !WhereFormulaProcessor.class.isAssignableFrom(formulaProcessor.getClass()))
                .map(formulaProcessor -> formulaProcessor.getInstance(this.dependenciesResolver, targetTable, tables))
                .toList();
    }

    @Override
    public Collection<SelectFormulaProcessor> getTableSelectProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return this.formulas.stream()
                .filter(formulaHolder -> formulaHolder.applyOn().equals(FormulaApplyOn.TABLE))
                .map(FormulaHolder::formulaProcessor)
                .filter(formulaProcessor -> SelectFormulaProcessor.class.isAssignableFrom(formulaProcessor.getClass()))
                .map(formulaProcessor -> formulaProcessor.getInstance(this.dependenciesResolver, targetTable, tables))
                .map(SelectFormulaProcessor.class::cast)
                .toList();
    }

    @Override
    public Collection<JoinFormulaProcessor> getTableJoinProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return this.formulas.stream()
                .filter(formulaHolder -> formulaHolder.applyOn().equals(FormulaApplyOn.TABLE))
                .map(FormulaHolder::formulaProcessor)
                .filter(formulaProcessor -> JoinFormulaProcessor.class.isAssignableFrom(formulaProcessor.getClass()))
                .map(formulaProcessor -> formulaProcessor.getInstance(this.dependenciesResolver, targetTable, tables))
                .map(JoinFormulaProcessor.class::cast)
                .toList();
    }

    @Override
    public Collection<WhereFormulaProcessor> getTableWhereProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return this.formulas.stream()
                .filter(formulaHolder -> formulaHolder.applyOn().equals(FormulaApplyOn.TABLE))
                .map(FormulaHolder::formulaProcessor)
                .filter(formulaProcessor -> WhereFormulaProcessor.class.isAssignableFrom(formulaProcessor.getClass()))
                .map(formulaProcessor -> formulaProcessor.getInstance(this.dependenciesResolver, targetTable, tables))
                .map(WhereFormulaProcessor.class::cast)
                .toList();
    }

    @Override
    public Collection<SelectFormulaProcessor> getColumnSelectProcessors(String fieldName, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return this.formulas.stream()
                .filter(formulaHolder -> formulaHolder.applyOn().equals(FormulaApplyOn.COLUMN))
                .filter(formulaHolder -> formulaHolder.fieldName().equals(fieldName))
                .map(FormulaHolder::formulaProcessor)
                .filter(formulaProcessor -> SelectFormulaProcessor.class.isAssignableFrom(formulaProcessor.getClass()))
                .map(formulaProcessor -> formulaProcessor.getInstance(this.dependenciesResolver, targetTable, tables))
                .map(SelectFormulaProcessor.class::cast)
                .toList();
    }

    @Override
    public Collection<JoinFormulaProcessor> getColumnJoinProcessors(String fieldName, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return this.formulas.stream()
                .filter(formulaHolder -> formulaHolder.applyOn().equals(FormulaApplyOn.COLUMN))
                .filter(formulaHolder -> formulaHolder.fieldName().equals(fieldName))
                .map(FormulaHolder::formulaProcessor)
                .filter(formulaProcessor -> JoinFormulaProcessor.class.isAssignableFrom(formulaProcessor.getClass()))
                .map(formulaProcessor -> formulaProcessor.getInstance(this.dependenciesResolver, targetTable, tables))
                .map(JoinFormulaProcessor.class::cast)
                .toList();
    }

    @Override
    public Collection<WhereFormulaProcessor> getColumnWhereProcessors(String fieldName, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return this.formulas.stream()
                .filter(formulaHolder -> formulaHolder.applyOn().equals(FormulaApplyOn.TABLE))
                .filter(formulaHolder -> formulaHolder.fieldName().equals(fieldName))
                .map(FormulaHolder::formulaProcessor)
                .filter(formulaProcessor -> WhereFormulaProcessor.class.isAssignableFrom(formulaProcessor.getClass()))
                .map(formulaProcessor -> formulaProcessor.getInstance(this.dependenciesResolver, targetTable, tables))
                .map(WhereFormulaProcessor.class::cast)
                .toList();
    }

    @Override
    public JoinMetadata getJoin(String attribute) {
        return Optional.ofNullable(joinMetadataMap.get(attribute)).orElseThrow(() -> new PersistenceException(String.format("No join for attribute %s", attribute)));
    }

    @SuppressWarnings("unchecked")
    private <T extends EntityValidator<? extends BaseEntity<?>>> List<T> getValidators(Class<?> entity, DependenciesResolver resolver) {
        ApplicationScanner scanner = new ApplicationScanner();
        return scanner.findComponents()
                .stream()
                .filter(this::isValidator)
                .filter(validatorClass -> {
                    ApplyOn applyOn = validatorClass.getAnnotation(ApplyOn.class);
                    return applyOn.entity().equals(entity);
                })
                .map(validator -> {
                    Object returnValidator = newInstance(validator);
                    EntityValidator<?> entityValidator = (EntityValidator<?>) returnValidator;
                    entityValidator.setResolver(resolver);
                    if (log.isDebugEnabled()) {
                        log.debug("Init validator [{}] for entity [{}]", validator.getName(), baseEntity.getClass().getName());
                    }
                    return entityValidator;
                })
                .map(returnObject -> (T) returnObject)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private <T extends EntityTrigger<? extends BaseEntity<?>>> List<T> getTriggers(Class<?> entity, DependenciesResolver dependenciesResolver, Environment environment) {
        ApplicationScanner scanner = new ApplicationScanner();
        return scanner.findComponents()
                .stream()
                .filter(this::isTrigger)
                .filter(triggerClass -> {
                    ApplyOn applyOn = triggerClass.getAnnotation(ApplyOn.class);
                    return applyOn.entity().equals(entity) || applyOn.type().equals(ApplyOn.ApplyType.ALL);
                })
                .map(clazz -> (Class<T>) clazz)
                .map(triggerClass -> initTrigger(triggerClass, dependenciesResolver, environment))
                .sorted(Comparator.comparing(entityTrigger -> {
                    ApplyOn applyOn = entityTrigger.getClass().getAnnotation(ApplyOn.class);
                    return applyOn.order();
                }))
                .toList();
    }

    private Class<?> queryIdType(Class<?> entityType) {
        for (Field field : entityType.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field.getType();
            }
        }
        throw new PersistenceException("Can not find type of entity id");
    }

    @SneakyThrows
    private Object newInstance(Class<?> initiator) {
        return UnsafeUtils.allocateInstance(initiator);
    }

    @SneakyThrows
    private BaseEntity<?> getBaseEntityInstance(Class<? extends BaseEntity<?>> baseEntityType) {
        return UnsafeUtils.allocateInstance(baseEntityType);
    }

    @SneakyThrows
    private <T extends EntityTrigger<? extends BaseEntity<?>>> T initTrigger(Class<T> triggerClass, DependenciesResolver dependenciesResolver, Environment environment) {
        AbstractTrigger<? extends BaseEntity<?>> trigger = (AbstractTrigger<? extends BaseEntity<?>>) UnsafeUtils.allocateInstance(triggerClass);
        trigger.setEnvironment(environment);
        trigger.setResolver(dependenciesResolver);

        return triggerClass.cast(trigger);
    }

    private int configLastId() throws ClassNotFoundException {
        String repositoryPattern = "com.bachlinh.order.repository.{0}Repository";
        String repositoryName = MessageFormat.format(repositoryPattern, entityType.getSimpleName());
        Class<?> repositoryClass = Class.forName(repositoryName);
        AbstractRepository<?, ?> repository = (AbstractRepository<?, ?>) dependenciesResolver.resolveDependencies(repositoryClass);
        String sql = MessageFormat.format("SELECT MAX(ID) FROM {0}", entityType.getAnnotation(Table.class).name());
        List<?> result = repository.executeNativeQuery(sql, Collections.emptyMap(), idType);
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

    private boolean isTrigger(Class<?> clazz) {
        return clazz.isAnnotationPresent(ApplyOn.class) &&
                EntityTrigger.class.isAssignableFrom(clazz);
    }

    private boolean isValidator(Class<?> clazz) {
        return clazz.isAnnotationPresent(ApplyOn.class) &&
                EntityValidator.class.isAssignableFrom(clazz);
    }

    private String createIdPrefix(Class<?> entityType) {
        Label label = entityType.getAnnotation(Label.class);
        if (label != null) {
            return entityType.getAnnotation(Label.class).value();
        } else {
            return null;
        }
    }

    private String createCacheRegion(Class<?> entityType) {
        Cache cache = entityType.getAnnotation(Cache.class);
        if (cache != null) {
            return cache.region();
        } else {
            return null;
        }
    }

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

    private Map<String, JoinMetadata> resolveJoinMetadata(Class<?> entity) {
        String tableAttributeTemplate = "{0}.{1}";
        String joinTemplate = " {0} ON {1} = {2} ";
        Map<String, JoinMetadata> results = new HashMap<>();
        results.putAll(resolveOneToOne(entity, tableAttributeTemplate, joinTemplate));
        results.putAll(resolveManyToMany(entity, tableAttributeTemplate));
        results.putAll(resolveOneToMany(entity, tableAttributeTemplate, joinTemplate));
        results.putAll(resolveManyToOne(entity, tableAttributeTemplate, joinTemplate));
        return results;
    }

    private Map<String, JoinMetadata> resolveOneToOne(Class<?> entity, String tableAttributeTemplate, String joinTemplate) {
        Map<String, JoinMetadata> results = new HashMap<>();
        List<Field> oneToOneFields = Stream.of(entity.getDeclaredFields()).filter(field -> field.isAnnotationPresent(OneToOne.class)).toList();
        for (var field : oneToOneFields) {
            JoinMetadata metadata;
            String leftOnJoin;
            String rightOnJoin;
            String joinStatement;
            Table table = field.getType().getAnnotation(Table.class);
            if (field.isAnnotationPresent(JoinColumn.class)) {
                JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                leftOnJoin = MessageFormat.format(tableAttributeTemplate, getTableName(), joinColumn.name());
                rightOnJoin = MessageFormat.format(tableAttributeTemplate, table.name(), "ID");
            } else {
                leftOnJoin = MessageFormat.format(tableAttributeTemplate, getTableName(), "ID");
                Field referenceField = Stream.of(field.getType().getDeclaredFields()).filter(f -> f.getType().equals(entityType)).toList().get(0);
                rightOnJoin = MessageFormat.format(tableAttributeTemplate, table.name(), referenceField.getAnnotation(JoinColumn.class).name());
                this.mappedFieldColumns.put(field.getName(), rightOnJoin);
            }
            joinStatement = MessageFormat.format(joinTemplate, table.name(), leftOnJoin, rightOnJoin);
            metadata = new InternalJoinMetadata(field.getName(), joinStatement);
            results.put(field.getName(), metadata);
        }
        return results;
    }

    private Map<String, JoinMetadata> resolveManyToMany(Class<?> entity, String tableAttributeTemplate) {
        String joinTemplate = "LEFT JOIN %s ON %s = %s {0} %s ON %s = %s";
        Map<String, JoinMetadata> results = new HashMap<>();
        List<Field> manyToManyFields = Stream.of(entity.getDeclaredFields()).filter(field -> field.isAnnotationPresent(ManyToMany.class)).toList();
        for (var field : manyToManyFields) {
            JoinTable joinTable = getJoinTable(entity, field);
            JoinColumn joinColumn = joinTable.joinColumns()[0];
            JoinColumn inverseJoinColumns = joinTable.inverseJoinColumns()[0];
            String manyToManyTableName = joinTable.name();
            String firstParam = MessageFormat.format(tableAttributeTemplate, getTableName(), inverseJoinColumns.referencedColumnName());
            String secondParam = MessageFormat.format(tableAttributeTemplate, manyToManyTableName, inverseJoinColumns.name());
            String thirdParam = getCollectionFieldType(field).getAnnotation(Table.class).name();
            String fourthParam = MessageFormat.format(tableAttributeTemplate, thirdParam, joinColumn.referencedColumnName());
            String fifthParam = MessageFormat.format(tableAttributeTemplate, manyToManyTableName, joinColumn.name());
            String joinStatement = String.format(joinTemplate, manyToManyTableName, firstParam, secondParam, thirdParam, fourthParam, fifthParam);
            JoinMetadata joinMetadata = new InternalJoinMetadata(field.getName(), joinStatement);
            results.put(field.getName(), joinMetadata);
            mappedFieldColumns.put(field.getName(), fourthParam);
        }
        return results;
    }

    private Map<String, JoinMetadata> resolveOneToMany(Class<?> entity, String tableAttributeTemplate, String joinTemplate) {
        Map<String, JoinMetadata> results = new HashMap<>();
        List<Field> oneToManyFields = Stream.of(entity.getDeclaredFields()).filter(field -> field.isAnnotationPresent(OneToMany.class)).toList();
        for (var field : oneToManyFields) {
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            Field referenceField;
            try {
                Class<?> referenceType = getCollectionFieldType(field);
                referenceField = referenceType.getDeclaredField(oneToMany.mappedBy());
                Table table = referenceType.getAnnotation(Table.class);
                JoinColumn joinColumn = referenceField.getAnnotation(JoinColumn.class);
                String leftOnJoin = MessageFormat.format(tableAttributeTemplate, getTableName(), "ID");
                String rightOnJoin = MessageFormat.format(tableAttributeTemplate, table.name(), joinColumn.name());
                String joinStatement = MessageFormat.format(joinTemplate, table.name(), leftOnJoin, rightOnJoin);
                results.put(field.getName(), new InternalJoinMetadata(field.getName(), joinStatement));
                this.mappedFieldColumns.put(referenceField.getName(), rightOnJoin);
            } catch (NoSuchFieldException e) {
                throw new PersistenceException(String.format("Can not process ManyToOne for type [%s]", entity.getName()), e);
            }
        }
        return results;
    }

    private Class<?> getCollectionFieldType(Field field) {
        try {
            return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        } catch (Exception e) {
            throw new PersistenceException("Can not extract parametrized type", e);
        }
    }

    private Map<String, JoinMetadata> resolveManyToOne(Class<?> entity, String tableAttributeTemplate, String joinTemplate) {
        Map<String, JoinMetadata> results = new HashMap<>();
        List<Field> manyToOneFields = Stream.of(entity.getDeclaredFields()).filter(field -> field.isAnnotationPresent(ManyToOne.class)).toList();
        for (var field : manyToOneFields) {
            Table referenceTable = field.getType().getAnnotation(Table.class);
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            String leftOnJoin = MessageFormat.format(tableAttributeTemplate, getTableName(), joinColumn.name());
            String rightOnJoin = MessageFormat.format(tableAttributeTemplate, referenceTable.name(), "ID");
            String joinStatement = MessageFormat.format(joinTemplate, referenceTable.name(), leftOnJoin, rightOnJoin);
            results.put(field.getName(), new InternalJoinMetadata(field.getName(), joinStatement));
        }
        return results;
    }

    private JoinTable getJoinTable(Class<?> entity, Field field) {
        JoinTable joinTable;
        if (field.isAnnotationPresent(JoinTable.class)) {
            joinTable = field.getAnnotation(JoinTable.class);
        } else {
            ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
            Class<?> referenceType = getCollectionFieldType(field);
            Field referenceField;
            try {
                referenceField = referenceType.getDeclaredField(manyToMany.mappedBy());
            } catch (NoSuchFieldException e) {
                throw new PersistenceException(String.format("Can not parse ManyToMany on type [%s]", entity.getName()));
            }
            joinTable = referenceField.getAnnotation(JoinTable.class);
        }
        return joinTable;
    }

    private Collection<FormulaHolder> resolveFormulas(Class<?> entity) {
        Collection<FormulaHolder> results = new LinkedList<>();
        createFormulaTable(entity, results);
        Class<?> abstractEntityType = entity.getSuperclass();
        createFormulaTable(abstractEntityType, results);
        List<Field> formulaFields = Stream.of(entity.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Formula.class)).toList();
        for (var field : formulaFields) {
            Formula formula = field.getAnnotation(Formula.class);
            Class<?> processor = formula.processor();
            if (Void.class.isAssignableFrom(processor)) {
                continue;
            }
            try {
                FormulaProcessor formulaProcessor = (FormulaProcessor) UnsafeUtils.allocateInstance(processor);
                FormulaHolder formulaHolder = new FormulaHolder(FormulaApplyOn.COLUMN, formulaProcessor, field.getName());
                results.add(formulaHolder);
            } catch (InstantiationException e) {
                throw new PersistenceException(String.format("Can not create formula [%s]", processor.getName()), e);
            }
        }
        return results;
    }

    private void createFormulaTable(Class<?> entity, Collection<FormulaHolder> formulaHolders) {
        if (entity.isAnnotationPresent(Formula.class)) {
            Formula formula = entity.getAnnotation(Formula.class);
            Class<?> processor = formula.processor();
            if (!Void.class.isAssignableFrom(processor)) {
                try {
                    FormulaProcessor formulaProcessor = (FormulaProcessor) UnsafeUtils.allocateInstance(processor);
                    FormulaHolder holder = new FormulaHolder(FormulaApplyOn.TABLE, formulaProcessor, "");
                    formulaHolders.add(holder);
                } catch (InstantiationException e) {
                    throw new PersistenceException(String.format("Can not create formula [%s]", processor.getName()), e);
                }
            }
        }
    }

    private record InternalJoinMetadata(String attribute, String joinStatement) implements JoinMetadata {
    }

    private record FormulaHolder(FormulaApplyOn applyOn, FormulaProcessor formulaProcessor, String fieldName) {
    }
}
