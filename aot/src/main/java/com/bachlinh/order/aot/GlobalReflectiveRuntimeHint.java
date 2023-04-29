package com.bachlinh.order.aot;

import com.bachlinh.order.annotation.Reachable;
import com.bachlinh.order.aot.locator.ObjectReflectiveLocator;
import com.bachlinh.order.aot.metadata.ClassMetadata;
import com.bachlinh.order.aot.metadata.ConstructorMetadata;
import com.bachlinh.order.aot.metadata.FieldMetadata;
import com.bachlinh.order.aot.metadata.Metadata;
import com.bachlinh.order.aot.metadata.MethodMetadata;
import com.bachlinh.order.aot.metadata.ServiceLoader;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;
import org.springframework.aot.hint.TypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;

import static org.springframework.aot.hint.ExecutableMode.INVOKE;
import static org.springframework.aot.hint.MemberCategory.DECLARED_FIELDS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_DECLARED_METHODS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS;
import static org.springframework.aot.hint.MemberCategory.INVOKE_PUBLIC_METHODS;
import static org.springframework.aot.hint.MemberCategory.PUBLIC_FIELDS;

public class GlobalReflectiveRuntimeHint implements RuntimeHintsRegistrar {
    private static final Pattern classPattern = Pattern.compile("^\\S+(.class)$");

    private final ServiceLoader serviceLoader = new ObjectReflectiveLocator();

    @Override
    public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader) {
        Collection<ClassMetadata> classMetadata = new LinkedHashSet<>(serviceLoader.loadClass("com.bachlinh.order"));
        List<ClassMetadata> filteredClassMetadata = classMetadata.stream().filter(this::filtered).toList();
        registerReflection(filteredClassMetadata, hints);
        List<ClassMetadata> serializableMetadata = classMetadata.stream().filter(ClassMetadata::isSerializable).toList();
        registerSerializable(serializableMetadata, hints);
        registerReachable(filteredClassMetadata, hints);
        registerResource(hints);
    }

    private boolean filtered(ClassMetadata classMetadata) {
        return classMetadata.needRegisterToRuntimeHints() || classMetadata.isReachable() || classMetadata.isNative();
    }

    private void registerReflection(Collection<ClassMetadata> classMetadata, RuntimeHints runtimeHints) {
        classMetadata.stream().filter(metadata -> metadata.needRegisterToRuntimeHints() || metadata.isNative()).toList().forEach(metadata -> {
            Collection<MethodMetadata> methodMetadata = serviceLoader.loadMethods(metadata).stream().filter(this::needRegisterToRuntime).toList();
            Collection<FieldMetadata> fieldMetadata = serviceLoader.loadFields(metadata).stream().filter(this::needRegisterToRuntime).toList();
            Collection<ConstructorMetadata> constructorMetadata = serviceLoader.loadConstructors(metadata).stream().filter(this::needRegisterToRuntime).toList();

            runtimeHints.reflection().registerType(metadata.getTarget(), builder -> {

                methodMetadata.forEach(method -> registerMethod(metadata, method, runtimeHints, builder));

                fieldMetadata.forEach(field -> registerField(metadata, field, runtimeHints, builder));

                constructorMetadata.forEach(constructor -> registerConstructor(metadata, constructor, runtimeHints, builder));

                if (metadata.hasDefaultConstructor()) {
                    builder.withConstructor(Arrays.stream(metadata.getDefaultConstructor().getParameterTypes()).map(TypeReference::of).toList(), INVOKE);
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
        List<TypeReference> params = Arrays.stream(method.getParameterTypes()).map(TypeReference::of).toList();

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
        List<TypeReference> params = Arrays.stream(constructor.getParameterTypes()).map(TypeReference::of).toList();

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
        classMetadata.forEach(metadata -> runtimeHints.serialization().registerType((Class<? extends Serializable>) metadata.getTarget()));
    }

    private void registerReachable(Collection<ClassMetadata> classMetadata, RuntimeHints runtimeHints) {
        classMetadata.stream().filter(ClassMetadata::isReachable).toList().forEach(metadata -> {
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

    private Collection<String> fileNames(File[] files, Collection<String> target) {
        for (File file : files) {
            if (file.isDirectory()) {
                File[] f = file.listFiles();
                return fileNames(f == null ? new File[0] : f, target);
            } else {
                String path = file.getPath();
                if (!classPattern.matcher(path).matches()) {
                    target.add(path.split("classes")[1]);
                }
            }
        }
        return target;
    }

    private Collection<String> getResources() {
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            URL url = classLoader.getResource("");
            if (url == null) {
                return Collections.emptyList();
            }
            File compiledSourceFile = new File(url.toURI());
            File[] files = compiledSourceFile.listFiles();
            if (files == null) {
                return Collections.emptyList();
            }
            return fileNames(files, new ArrayList<>());
        } catch (URISyntaxException e) {
            return Collections.emptyList();
        }
    }
}

