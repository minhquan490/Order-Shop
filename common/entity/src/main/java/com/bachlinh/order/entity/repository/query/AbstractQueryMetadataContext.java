package com.bachlinh.order.entity.repository.query;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.core.enums.FormulaApplyOn;
import com.bachlinh.order.entity.FormulaMetadata;
import com.bachlinh.order.entity.TableMetadataHolder;
import com.bachlinh.order.entity.context.AbstractEntityContext;
import com.bachlinh.order.entity.formula.processor.FormulaProcessor;
import com.bachlinh.order.entity.formula.processor.JoinFormulaProcessor;
import com.bachlinh.order.entity.formula.processor.SelectFormulaProcessor;
import com.bachlinh.order.entity.formula.processor.WhereFormulaProcessor;
import com.bachlinh.order.entity.index.spi.SearchManager;
import com.bachlinh.order.entity.model.AbstractEntity;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractQueryMetadataContext extends AbstractEntityContext implements FormulaMetadata, JoinMetadataHolder {

    private final Collection<FormulaHolder> formulas;
    private final Map<String, JoinMetadata> joinMetadataMap;

    protected AbstractQueryMetadataContext(Class<?> entity, SearchManager searchManager) {
        super(entity, searchManager);
        this.formulas = resolveFormulas(entity);
        this.joinMetadataMap = resolveJoinMetadata(entity);
    }

    @Override
    public Collection<FormulaProcessor> getNativeQueryProcessor(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return this.formulas.stream()
                .filter(formulaHolder -> formulaHolder.applyOn().equals(FormulaApplyOn.TABLE))
                .map(FormulaHolder::formulaProcessor)
                .filter(formulaProcessor -> !SelectFormulaProcessor.class.isAssignableFrom(formulaProcessor.getClass()) &&
                        !JoinFormulaProcessor.class.isAssignableFrom(formulaProcessor.getClass()) &&
                        !WhereFormulaProcessor.class.isAssignableFrom(formulaProcessor.getClass()))
                .map(formulaProcessor -> formulaProcessor.getInstance(getResolver(), targetTable, tables))
                .toList();
    }

    @Override
    public Collection<SelectFormulaProcessor> getTableSelectProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return resolveFormulaProcessors(targetTable, tables, SelectFormulaProcessor.class, FormulaApplyOn.TABLE);
    }

    @Override
    public Collection<JoinFormulaProcessor> getTableJoinProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return resolveFormulaProcessors(targetTable, tables, JoinFormulaProcessor.class, FormulaApplyOn.TABLE);
    }

    @Override
    public Collection<WhereFormulaProcessor> getTableWhereProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return resolveFormulaProcessors(targetTable, tables, WhereFormulaProcessor.class, FormulaApplyOn.TABLE);
    }

    @Override
    public Collection<SelectFormulaProcessor> getColumnSelectProcessors(String fieldName, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return resolveFormulaProcessors(targetTable, tables, SelectFormulaProcessor.class, FormulaApplyOn.COLUMN);
    }

    @Override
    public Collection<JoinFormulaProcessor> getColumnJoinProcessors(String fieldName, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return resolveFormulaProcessors(targetTable, tables, JoinFormulaProcessor.class, FormulaApplyOn.COLUMN);
    }

    @Override
    public Collection<WhereFormulaProcessor> getColumnWhereProcessors(String fieldName, TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables) {
        return resolveFormulaProcessors(targetTable, tables, WhereFormulaProcessor.class, FormulaApplyOn.COLUMN);
    }

    @Override
    public JoinMetadata getJoin(String attribute) {
        return Optional.ofNullable(joinMetadataMap.get(attribute)).orElseThrow(() -> new PersistenceException(String.format("No join for attribute %s", attribute)));
    }

    protected abstract Initializer<FormulaProcessor> getFormulaInitializer();

    private <T extends FormulaProcessor> Collection<T> resolveFormulaProcessors(TableMetadataHolder targetTable, Map<Class<? extends AbstractEntity<?>>, TableMetadataHolder> tables, Class<T> processorType, FormulaApplyOn applyOn) {
        return this.formulas.stream()
                .filter(formulaHolder -> formulaHolder.applyOn().equals(applyOn))
                .map(FormulaHolder::formulaProcessor)
                .filter(formulaProcessor -> processorType.isAssignableFrom(formulaProcessor.getClass()))
                .map(formulaProcessor -> formulaProcessor.getInstance(getResolver(), targetTable, tables))
                .map(processorType::cast)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private Collection<FormulaHolder> resolveFormulas(Class<?> entity) {
        Collection<FormulaHolder> results = new LinkedList<>();
        createFormulaTable(entity, results);
        Class<?> abstractEntityType = entity.getSuperclass();
        createFormulaTable(abstractEntityType, results);
        List<Field> formulaFields = Stream.of(entity.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Formula.class)).toList();
        for (var field : formulaFields) {
            Formula formula = field.getAnnotation(Formula.class);
            Class<?>[] processors = formula.processors();
            for (var processor : processors) {
                resolveFormula(FormulaApplyOn.COLUMN, field.getName(), (Class<? extends FormulaProcessor>) processor, results);
            }
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private void createFormulaTable(Class<?> entity, Collection<FormulaHolder> formulaHolders) {
        if (entity.isAnnotationPresent(Formula.class)) {
            Formula formula = entity.getAnnotation(Formula.class);
            Class<?>[] processors = formula.processors();
            for (var processor : processors) {
                resolveFormula(FormulaApplyOn.TABLE, "", (Class<? extends FormulaProcessor>) processor, formulaHolders);
            }
        }
    }

    private void resolveFormula(FormulaApplyOn applyOn, String fieldName, Class<? extends FormulaProcessor> type, Collection<FormulaHolder> holders) {

        FormulaProcessor formulaProcessor = getFormulaInitializer().getObject(type);

        FormulaHolder holder = new FormulaHolder(applyOn, formulaProcessor, fieldName);
        holders.add(holder);
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
                Field referenceField = Stream.of(field.getType().getDeclaredFields()).filter(f -> f.getType().equals(getEntityType())).toList().get(0);
                rightOnJoin = MessageFormat.format(tableAttributeTemplate, table.name(), referenceField.getAnnotation(JoinColumn.class).name());
                getMappedFieldColumns().put(field.getName(), rightOnJoin);
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
            getMappedFieldColumns().put(field.getName(), fourthParam);
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
                getMappedFieldColumns().put(referenceField.getName(), rightOnJoin);
            } catch (NoSuchFieldException e) {
                throw new PersistenceException(String.format("Can not process ManyToOne for type [%s]", entity.getName()), e);
            }
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

    private Class<?> getCollectionFieldType(Field field) {
        try {
            return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        } catch (Exception e) {
            throw new PersistenceException("Can not extract parametrized type", e);
        }
    }

    private record FormulaHolder(FormulaApplyOn applyOn, FormulaProcessor formulaProcessor, String fieldName) {
    }

    private record InternalJoinMetadata(String attribute, String joinStatement) implements JoinMetadata {
    }
}
