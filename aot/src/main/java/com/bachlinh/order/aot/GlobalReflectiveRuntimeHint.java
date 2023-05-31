package com.bachlinh.order.aot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;
import org.springframework.aot.hint.TypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import static org.springframework.aot.hint.ExecutableMode.INVOKE;
import static org.springframework.aot.hint.MemberCategory.DECLARED_FIELDS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_METHODS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS;
import static org.springframework.aot.hint.MemberCategory.PUBLIC_FIELDS;
import com.bachlinh.order.annotation.Native;
import com.bachlinh.order.annotation.Reachable;
import com.bachlinh.order.aot.locator.ObjectReflectiveLocator;
import com.bachlinh.order.aot.metadata.ClassMetadata;
import com.bachlinh.order.aot.metadata.ConstructorMetadata;
import com.bachlinh.order.aot.metadata.FieldMetadata;
import com.bachlinh.order.aot.metadata.Metadata;
import com.bachlinh.order.aot.metadata.MethodMetadata;
import com.bachlinh.order.aot.metadata.ServiceLoader;
import com.bachlinh.order.core.scanner.ApplicationScanner;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;


public class GlobalReflectiveRuntimeHint implements RuntimeHintsRegistrar {
    private static final Logger log = LogManager.getLogger(GlobalReflectiveRuntimeHint.class);

    private final ServiceLoader serviceLoader = new ObjectReflectiveLocator();

    public GlobalReflectiveRuntimeHint() {
        new ApplicationScanner().findComponents();
    }

    @Override
    public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader) {
        Collection<ClassMetadata> classMetadata = new LinkedHashSet<>(serviceLoader.loadClass("com.bachlinh.order"));
        log.debug("Total class should register [{}]", classMetadata.size());
        List<ClassMetadata> filteredClassMetadata = classMetadata.stream().filter(this::filtered).toList();
        registerReflection(filteredClassMetadata, hints);
        List<ClassMetadata> serializableMetadata = classMetadata.stream().filter(ClassMetadata::isSerializable).toList();
        registerSerializable(serializableMetadata, hints);
        registerReachable(filteredClassMetadata, hints);
        registerResource(hints);
        registerSpecial(hints);
    }

    private boolean filtered(ClassMetadata classMetadata) {
        return classMetadata.needRegisterToRuntimeHints() || classMetadata.isReachable() || classMetadata.isNative();
    }

    private void registerReflection(Collection<ClassMetadata> classMetadata, RuntimeHints runtimeHints) {
        classMetadata.stream().filter(metadata -> metadata.needRegisterToRuntimeHints() || metadata.isNative()).toList().forEach(metadata -> {
            log.debug("Register reflection for class [{}]", metadata.getName());

            Collection<MethodMetadata> methodMetadata = serviceLoader.loadMethods(metadata).stream().filter(this::needRegisterToRuntime).toList();
            Collection<FieldMetadata> fieldMetadata = serviceLoader.loadFields(metadata).stream().filter(this::needRegisterToRuntime).toList();
            Collection<ConstructorMetadata> constructorMetadata = serviceLoader.loadConstructors(metadata).stream().filter(this::needRegisterToRuntime).toList();

            runtimeHints.reflection().registerType(metadata.getTarget(), builder -> {

                methodMetadata.forEach(method -> registerMethod(metadata, method, runtimeHints, builder));

                fieldMetadata.forEach(field -> registerField(metadata, field, runtimeHints, builder));

                constructorMetadata.forEach(constructor -> registerConstructor(metadata, constructor, runtimeHints, builder));

                if (metadata.hasDefaultConstructor()) {
                    builder.withConstructor(TypeReference.listOf(metadata.getDefaultConstructor().getParameterTypes()), INVOKE);
                    builder.withMembers(INVOKE_PUBLIC_CONSTRUCTORS, INVOKE_DECLARED_CONSTRUCTORS);
                }
            });
        });

        classMetadata.stream().filter(Metadata::isNative).toList().forEach(metadata -> runtimeHints.jni().registerType(metadata.getTarget()));
    }

    private boolean needRegisterToRuntime(Metadata metadata) {
        return metadata.needRegisterToRuntimeHints() || metadata.isNative();
    }

    private void registerMethod(ClassMetadata metadata, MethodMetadata method, RuntimeHints runtimeHints, TypeHint.Builder builder) {
        String methodName = method.getName();
        List<TypeReference> params = TypeReference.listOf(method.getParameterTypes());

        if (method.needRegisterToRuntimeHints() && method.isNative()) {

            builder.withMethod(methodName, params, INVOKE);
            builder.withMembers(INVOKE_DECLARED_METHODS, INVOKE_PUBLIC_METHODS);

            runtimeHints.jni().registerType(metadata.getTarget(), builder1 -> builder1.withMethod(methodName, params, INVOKE));
        }

        if (!method.needRegisterToRuntimeHints() && method.isNative()) {
            runtimeHints.jni().registerType(metadata.getTarget(), builder1 -> builder1.withMethod(methodName, params, INVOKE));
        }

        if (method.needRegisterToRuntimeHints() && !method.isNative()) {
            builder.withMethod(methodName, params, INVOKE);
            builder.withMembers(INVOKE_DECLARED_METHODS, INVOKE_PUBLIC_METHODS);
        }
    }

    private void registerField(ClassMetadata metadata, FieldMetadata field, RuntimeHints runtimeHints, TypeHint.Builder builder) {
        String fieldName = field.getName();

        if (field.needRegisterToRuntimeHints() && field.isNative()) {
            builder.withField(fieldName);
            builder.withMembers(DECLARED_FIELDS, PUBLIC_FIELDS);

            runtimeHints.jni().registerType(metadata.getTarget(), builder1 -> builder1.withField(fieldName));
        }

        if (!field.needRegisterToRuntimeHints() && field.isNative()) {
            runtimeHints.jni().registerType(metadata.getTarget(), builder1 -> builder1.withField(fieldName));
        }

        if (field.needRegisterToRuntimeHints() && !field.isNative()) {
            builder.withField(fieldName);
            builder.withMembers(DECLARED_FIELDS, PUBLIC_FIELDS);
        }
    }

    private void registerConstructor(ClassMetadata metadata, ConstructorMetadata constructor, RuntimeHints runtimeHints, TypeHint.Builder builder) {
        List<TypeReference> params = TypeReference.listOf(constructor.getParameterTypes());

        if (constructor.needRegisterToRuntimeHints() && constructor.isNative()) {
            builder.withConstructor(params, INVOKE);
            builder.withMembers(INVOKE_PUBLIC_CONSTRUCTORS, INVOKE_DECLARED_CONSTRUCTORS);

            runtimeHints.jni().registerType(metadata.getTarget(), builder1 -> builder1.withConstructor(params, INVOKE));
        }

        if (!constructor.needRegisterToRuntimeHints() && constructor.isNative()) {
            runtimeHints.jni().registerType(metadata.getTarget(), builder1 -> builder1.withConstructor(params, INVOKE));
        }

        if (constructor.needRegisterToRuntimeHints() && !constructor.isNative()) {
            builder.withConstructor(params, INVOKE);
            builder.withMembers(INVOKE_PUBLIC_CONSTRUCTORS, INVOKE_DECLARED_CONSTRUCTORS);
        }
    }

    @SuppressWarnings("unchecked")
    private void registerSerializable(Collection<ClassMetadata> classMetadata, RuntimeHints runtimeHints) {
        classMetadata.forEach(metadata -> {
            log.debug("Register serializable for class [{}]", metadata.getName());
            runtimeHints.serialization().registerType((Class<? extends Serializable>) metadata.getTarget());
        });
    }

    private void registerReachable(Collection<ClassMetadata> classMetadata, RuntimeHints runtimeHints) {
        classMetadata.stream().filter(ClassMetadata::isReachable).toList().forEach(metadata -> {
            log.debug("Register reachable for class [{}]", metadata.getName());

            Reachable reachable = metadata.getAnnotation(Reachable.class);
            for (Class<?> clazz : reachable.onClasses()) {
                runtimeHints.reflection().registerType(clazz, builder -> {
                    builder.onReachableType(metadata.getTarget());

                    if (reachable.fieldNames().length != 0) {
                        for (String fieldName : reachable.fieldNames()) {
                            builder.withField(fieldName);
                        }
                        builder.withMembers(DECLARED_FIELDS, PUBLIC_FIELDS);
                    }

                });
            }
        });
    }

    private void registerResource(RuntimeHints runtimeHints) {
        Collection<String> systemResources = getResources();
        systemResources.forEach(resource -> runtimeHints.resources().registerResource(new ClassPathResource(resource)));
    }

    private Collection<String> fileNames(Collection<File> files, Collection<String> target) {
        List<File> folders = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                File[] f = file.listFiles();
                folders.addAll(f == null ? Collections.emptyList() : Arrays.asList(f));
            } else {
                String path = file.getPath();
                log.debug("Register resource [{}]", path);
                target.add(path.split("main")[1]);
            }
        }
        if (!folders.isEmpty()) {
            return fileNames(folders, target);
        }
        return target;
    }

    private Collection<String> getResources() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL url = classLoader.getResource("application.properties");
        if (url == null) {
            return Collections.emptyList();
        }
        File compiledSourceFile = new File(url.getPath().replace("/application.properties", ""));
        File[] files = compiledSourceFile.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return fileNames(Arrays.asList(files), new ArrayList<>());
    }

    private void registerSpecial(RuntimeHints hints) {
        try {
            Class<?> coccocClass = Class.forName("com.coccoc.Tokenizer");
            hints.jni().registerType(coccocClass, builder -> {
                Constructor<?>[] constructors = coccocClass.getDeclaredConstructors();
                for (Constructor<?> constructor : constructors) {
                    if (constructor.isAnnotationPresent(Native.class)) {
                        builder.withConstructor(TypeReference.listOf(constructor.getParameterTypes()), INVOKE);
                        builder.withMembers(INVOKE_PUBLIC_CONSTRUCTORS, INVOKE_DECLARED_CONSTRUCTORS);
                    }
                }
                Method[] methods = coccocClass.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(Native.class)) {
                        builder.withMethod(method.getName(), TypeReference.listOf(method.getParameterTypes()), INVOKE);
                        builder.withMembers(INVOKE_PUBLIC_METHODS, INVOKE_DECLARED_METHODS);
                    }
                }
                Field[] fields = coccocClass.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(Native.class)) {
                        builder.withField(field.getName());
                        builder.withMembers(DECLARED_FIELDS, PUBLIC_FIELDS);
                    }
                }
            });
        } catch (ClassNotFoundException e) {
            log.warn("Coc coc class not found");
        }
    }
}

