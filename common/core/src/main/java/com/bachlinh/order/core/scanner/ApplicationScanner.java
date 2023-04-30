package com.bachlinh.order.core.scanner;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

public final class ApplicationScanner extends ClassPathScanningCandidateComponentProvider {
    private static final String APPLICATION_PACKAGE = "com.bachlinh.order";
    private static final Collection<Class<?>> CACHE_SCANNING_RESULT = new HashSet<>();

    public ApplicationScanner() {
        addIncludeFilter(new PackageTypeFilter(APPLICATION_PACKAGE));
    }

    public Collection<Class<?>> findComponents() {
        if (CACHE_SCANNING_RESULT.isEmpty()) {
            for (BeanDefinition beanDefinition : findCandidateComponents(APPLICATION_PACKAGE)) {
                try {
                    CACHE_SCANNING_RESULT.add(Class.forName(beanDefinition.getBeanClassName()));
                } catch (ClassNotFoundException e) {
                    // Ignore
                }
            }
        }
        return CACHE_SCANNING_RESULT;
    }

    public static void clean() {
        CACHE_SCANNING_RESULT.clear();
    }

    public static class PackageTypeFilter implements TypeFilter {
        private final String matchedPackage;

        public PackageTypeFilter(@NonNull String matchedPackage) {
            this.matchedPackage = matchedPackage;
        }

        @Override
        public boolean match(@NonNull MetadataReader metadataReader, @NonNull MetadataReaderFactory metadataReaderFactory) throws IOException {
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            return classMetadata.getClassName().startsWith(matchedPackage) ||
                    !classMetadata.isAbstract() ||
                    !classMetadata.isInterface() ||
                    !classMetadata.isAnnotation();
        }
    }
}
