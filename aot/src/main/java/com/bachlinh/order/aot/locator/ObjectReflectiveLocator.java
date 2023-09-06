package com.bachlinh.order.aot.locator;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Ignore;
import com.bachlinh.order.annotation.Native;
import com.bachlinh.order.annotation.Reachable;
import com.bachlinh.order.aot.metadata.ClassMetadata;
import com.bachlinh.order.aot.metadata.ConstructorMetadata;
import com.bachlinh.order.aot.metadata.FieldMetadata;
import com.bachlinh.order.aot.metadata.MethodMetadata;
import com.bachlinh.order.aot.metadata.Modifier;
import com.bachlinh.order.aot.metadata.RefectionType;
import com.bachlinh.order.aot.metadata.ServiceLoader;
import com.bachlinh.order.utils.UnsafeUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class ObjectReflectiveLocator implements ServiceLoader {

    private final Scanner<Collection<Class<?>>> classScanner;

    public ObjectReflectiveLocator() {
        this.classScanner = new ClasspathClassScanner();
    }

    @Override
    public Collection<ClassMetadata> loadClass(String basePackage) {
        ScanResult<Collection<Class<?>>> result = classScanner.scan(Collections.singleton(basePackage));
        if (result != null && result.isReady()) {
            return Objects.requireNonNull(result.result())
                    .stream()
                    .map(InternalClassMetadata::new)
                    .map(ClassMetadata.class::cast)
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<ConstructorMetadata> loadConstructors(ClassMetadata classMetadata) {
        Set<ConstructorMetadata> constructorMetadata = new LinkedHashSet<>();
        Class<?> parent = classMetadata.getTarget();
        for (Constructor<?> constructor : parent.getDeclaredConstructors()) {
            if (!constructor.isAnnotationPresent(Ignore.class)) {
                constructorMetadata.add(new InternalConstructorMetadata(classMetadata, constructor));
            }
        }
        return constructorMetadata;
    }

    @Override
    public Collection<FieldMetadata> loadFields(ClassMetadata classMetadata) {
        Set<FieldMetadata> fieldMetadata = new LinkedHashSet<>();
        Class<?> parent = classMetadata.getTarget();
        for (Field field : parent.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Ignore.class)) {
                fieldMetadata.add(new InternalFieldMetadata(field, classMetadata));
            }
        }
        return fieldMetadata;
    }

    @Override
    public Collection<MethodMetadata> loadMethods(ClassMetadata classMetadata) {
        Set<MethodMetadata> methodMetadata = new LinkedHashSet<>();
        Class<?> parent = classMetadata.getTarget();
        for (Method method : parent.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Ignore.class)) {
                methodMetadata.add(new InternalMethodMetadata(classMetadata, method));
            }
        }
        return methodMetadata;
    }

    private static class InternalClassMetadata implements ClassMetadata {

        private final Class<?> wrappedTarget;
        private Constructor<?> defaultConstructor;
        private boolean hasDefaultConstructor;
        private final boolean needRegisterToReflectionHint;
        private final boolean isReachable;
        private final boolean isNative;

        InternalClassMetadata(Class<?> target) {
            this.wrappedTarget = target;
            this.needRegisterToReflectionHint = target.isAnnotationPresent(ActiveReflection.class);
            if (java.lang.reflect.Modifier.isPublic(target.getModifiers())) {
                try {
                    this.defaultConstructor = target.getConstructor();
                    this.hasDefaultConstructor = true;
                } catch (NoSuchMethodException e) {
                    this.defaultConstructor = null;
                    this.hasDefaultConstructor = false;
                }
            } else {
                this.defaultConstructor = null;
                this.hasDefaultConstructor = false;
            }
            this.isReachable = target.isAnnotationPresent(Reachable.class);
            this.isNative = target.isAnnotationPresent(Native.class);
        }

        @Override
        public Class<?> getTarget() {
            return this.wrappedTarget;
        }

        @Override
        public Class<?> getSuperClass() {
            return this.wrappedTarget.getSuperclass();
        }

        @Override
        public Class<?>[] getInterfaces() {
            return this.wrappedTarget.getInterfaces();
        }

        @Override
        public boolean isInterface() {
            return this.wrappedTarget.isInterface();
        }

        @Override
        public boolean isAbstract() {
            return isInterface() || java.lang.reflect.Modifier.isAbstract(this.wrappedTarget.getModifiers());
        }

        @Override
        public boolean isSerializable() {
            return Serializable.class.isAssignableFrom(this.wrappedTarget);
        }

        @Override
        public boolean isReachable() {
            return this.isReachable;
        }

        @Override
        public boolean hasDefaultConstructor() {
            return this.hasDefaultConstructor;
        }

        @Override
        public Constructor<?> getDefaultConstructor() {
            return this.defaultConstructor;
        }

        @Override
        public boolean isAccessible() {
            return true;
        }

        @Override
        public void makeAccessible() {
            // do nothing
        }

        @Override
        public Class<?> getParent() {
            return this.wrappedTarget.getEnclosingClass();
        }

        @Override
        public RefectionType getType() {
            return RefectionType.CLASS;
        }

        @Override
        public Modifier getModifier() {
            if (java.lang.reflect.Modifier.isPublic(this.wrappedTarget.getModifiers())) {
                return Modifier.PUBLIC;
            }
            if (java.lang.reflect.Modifier.isProtected(this.wrappedTarget.getModifiers())) {
                return Modifier.PROTECTED;
            }
            if (java.lang.reflect.Modifier.isPrivate(this.wrappedTarget.getModifiers())) {
                return Modifier.PRIVATE;
            }
            return Modifier.DEFAULT;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<? extends Annotation>[] getAnnotationTypes() {
            Collection<Class<? extends Annotation>> annotationTypes = new LinkedHashSet<>();
            for (Annotation annotation : this.wrappedTarget.getAnnotations()) {
                annotationTypes.add(annotation.annotationType());
            }
            return annotationTypes.toArray(new Class[0]);
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
            return this.wrappedTarget.getAnnotation(annotationType);
        }

        @Override
        public String getPackage() {
            return this.wrappedTarget.getPackageName();
        }

        @Override
        public String getName() {
            return this.wrappedTarget.getName();
        }

        @Override
        public boolean needRegisterToRuntimeHints() {
            return needRegisterToReflectionHint;
        }

        @Override
        public boolean isNative() {
            return this.isNative;
        }
    }

    private static class InternalMethodMetadata implements MethodMetadata {
        private Object parentObject;
        private final ClassMetadata parent;
        private final Method wrappedTarget;
        private final boolean needRegisterToReflectionHint;
        private final boolean isNative;

        InternalMethodMetadata(ClassMetadata parent, Method target) {
            this.parent = parent;
            this.wrappedTarget = target;
            this.needRegisterToReflectionHint = target.isAnnotationPresent(ActiveReflection.class);
            try {
                this.parentObject = UnsafeUtils.getUnsafe().allocateInstance(parent.getTarget());
            } catch (InstantiationException e) {
                this.parentObject = null;
            }
            this.isNative = target.isAnnotationPresent(Native.class);
        }

        @Override
        public boolean isAccessible() {
            if (parentObject == null) {
                return false;
            }
            return this.wrappedTarget.canAccess(parentObject);
        }

        @Override
        public void makeAccessible() {
            this.wrappedTarget.setAccessible(true);
        }

        @Override
        public Class<?> getParent() {
            return parent.getTarget();
        }

        @Override
        public RefectionType getType() {
            return RefectionType.METHOD;
        }

        @Override
        public Modifier getModifier() {
            if (java.lang.reflect.Modifier.isPublic(this.wrappedTarget.getModifiers())) {
                return Modifier.PUBLIC;
            }
            if (java.lang.reflect.Modifier.isProtected(this.wrappedTarget.getModifiers())) {
                return Modifier.PROTECTED;
            }
            if (java.lang.reflect.Modifier.isPrivate(this.wrappedTarget.getModifiers())) {
                return Modifier.PRIVATE;
            }
            return Modifier.DEFAULT;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<? extends Annotation>[] getAnnotationTypes() {
            Collection<Class<? extends Annotation>> annotationTypes = new LinkedHashSet<>();
            for (Annotation annotation : this.wrappedTarget.getAnnotations()) {
                annotationTypes.add(annotation.annotationType());
            }
            return annotationTypes.toArray(new Class[0]);
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
            return this.wrappedTarget.getAnnotation(annotationType);
        }

        @Override
        public String getPackage() {
            return parent.getPackage();
        }

        @Override
        public String getName() {
            return this.wrappedTarget.getName();
        }

        @Override
        public boolean needRegisterToRuntimeHints() {
            return this.needRegisterToReflectionHint;
        }

        @Override
        public boolean isNative() {
            return this.isNative;
        }

        @Override
        public Class<?>[] getParameterTypes() {
            return this.wrappedTarget.getParameterTypes();
        }

        @Override
        public Class<?> getReturnType() {
            return this.wrappedTarget.getReturnType();
        }

        @Override
        public Method getTarget() {
            return this.wrappedTarget;
        }
    }

    private static class InternalFieldMetadata implements FieldMetadata {
        private Object parentObject;
        private final Field wrappedTarget;
        private final ClassMetadata parent;
        private final boolean needToRegisterReflectionHint;
        private final Class<?> type;
        private final boolean isNative;

        InternalFieldMetadata(Field field, ClassMetadata parent) {
            this.wrappedTarget = field;
            this.parent = parent;
            this.needToRegisterReflectionHint = field.isAnnotationPresent(ActiveReflection.class);
            try {
                this.parentObject = UnsafeUtils.getUnsafe().allocateInstance(parent.getTarget());
            } catch (InstantiationException e) {
                this.parentObject = null;
            }
            this.type = field.getType();
            this.isNative = field.isAnnotationPresent(Native.class);
        }

        @Override
        public Class<?> getFieldType() {
            return type;
        }

        @Override
        public Field getTarget() {
            return this.wrappedTarget;
        }

        @Override
        public boolean isAccessible() {
            if (parentObject == null) {
                return false;
            }
            return this.wrappedTarget.canAccess(parentObject);
        }

        @Override
        public void makeAccessible() {
            this.wrappedTarget.setAccessible(true);
        }

        @Override
        public Class<?> getParent() {
            return this.parent.getTarget();
        }

        @Override
        public RefectionType getType() {
            return RefectionType.FIELD;
        }

        @Override
        public Modifier getModifier() {
            if (java.lang.reflect.Modifier.isPublic(this.wrappedTarget.getModifiers())) {
                return Modifier.PUBLIC;
            }
            if (java.lang.reflect.Modifier.isProtected(this.wrappedTarget.getModifiers())) {
                return Modifier.PROTECTED;
            }
            if (java.lang.reflect.Modifier.isPrivate(this.wrappedTarget.getModifiers())) {
                return Modifier.PRIVATE;
            }
            return Modifier.DEFAULT;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<? extends Annotation>[] getAnnotationTypes() {
            Collection<Class<? extends Annotation>> annotationTypes = new LinkedHashSet<>();
            for (Annotation annotation : this.wrappedTarget.getAnnotations()) {
                annotationTypes.add(annotation.annotationType());
            }
            return annotationTypes.toArray(new Class[0]);
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
            return this.wrappedTarget.getAnnotation(annotationType);
        }

        @Override
        public String getPackage() {
            return this.parent.getPackage();
        }

        @Override
        public String getName() {
            return this.wrappedTarget.getName();
        }

        @Override
        public boolean needRegisterToRuntimeHints() {
            return this.needToRegisterReflectionHint;
        }

        @Override
        public boolean isNative() {
            return this.isNative;
        }
    }

    private static class InternalConstructorMetadata implements ConstructorMetadata {
        private final Constructor<?> wrappedTarget;
        private final ClassMetadata parent;
        private final boolean needToRegisterReflectionHint;
        private final boolean isNative;

        InternalConstructorMetadata(ClassMetadata parent, Constructor<?> target) {
            this.wrappedTarget = target;
            this.parent = parent;
            this.needToRegisterReflectionHint = target.isAnnotationPresent(ActiveReflection.class);
            this.isNative = target.isAnnotationPresent(Native.class);
        }

        @Override
        public Class<?>[] getParameterTypes() {
            return this.wrappedTarget.getParameterTypes();
        }

        @Override
        public Constructor<?> getTarget() {
            return this.wrappedTarget;
        }

        @Override
        public boolean isAccessible() {
            return this.wrappedTarget.canAccess(null);
        }

        @Override
        public void makeAccessible() {
            this.wrappedTarget.setAccessible(true);
        }

        @Override
        public Class<?> getParent() {
            return parent.getTarget();
        }

        @Override
        public RefectionType getType() {
            return RefectionType.CONSTRUCTOR;
        }

        @Override
        public Modifier getModifier() {
            if (java.lang.reflect.Modifier.isPublic(this.wrappedTarget.getModifiers())) {
                return Modifier.PUBLIC;
            }
            if (java.lang.reflect.Modifier.isProtected(this.wrappedTarget.getModifiers())) {
                return Modifier.PROTECTED;
            }
            if (java.lang.reflect.Modifier.isPrivate(this.wrappedTarget.getModifiers())) {
                return Modifier.PRIVATE;
            }
            return Modifier.DEFAULT;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Class<? extends Annotation>[] getAnnotationTypes() {
            Collection<Class<? extends Annotation>> annotationTypes = new LinkedHashSet<>();
            for (Annotation annotation : this.wrappedTarget.getAnnotations()) {
                annotationTypes.add(annotation.annotationType());
            }
            return annotationTypes.toArray(new Class[0]);
        }

        @Override
        public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
            return this.wrappedTarget.getAnnotation(annotationType);
        }

        @Override
        public String getPackage() {
            return this.parent.getPackage();
        }

        @Override
        public String getName() {
            return this.wrappedTarget.getName();
        }

        @Override
        public boolean needRegisterToRuntimeHints() {
            return this.needToRegisterReflectionHint;
        }

        @Override
        public boolean isNative() {
            return this.isNative;
        }
    }
}

