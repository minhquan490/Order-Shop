package com.bachlinh.order.core.scanner;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Log4j2
public final class ApplicationScanner extends ClassPathScanningCandidateComponentProvider {
    private static final String APPLICATION_PACKAGE = "com.bachlinh.order";
    private static final String COCOC_PACKAGE = "com.coccoc";
    private static final Collection<Class<?>> CACHE_SCANNING_RESULT = new HashSet<>();

    public ApplicationScanner() {
        addIncludeFilter(new PackageTypeFilter(APPLICATION_PACKAGE));
    }

    public Collection<Class<?>> findComponents() {
        if (CACHE_SCANNING_RESULT.isEmpty()) {
            for (BeanDefinition beanDefinition : findCandidateComponents(APPLICATION_PACKAGE)) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Found class [{}]", beanDefinition.getBeanClassName());
                    }
                    CACHE_SCANNING_RESULT.add(Class.forName(beanDefinition.getBeanClassName()));
                } catch (ClassNotFoundException e) {
                    log.warn("Class [{}] not found", beanDefinition.getBeanClassName());
                }
            }
        }
        return CACHE_SCANNING_RESULT;
    }

    public static void clean() {
        CACHE_SCANNING_RESULT.clear();
    }

    public static class PackageTypeFilter implements TypeFilter {
        private final List<String> matchedPackages;

        public PackageTypeFilter(@NonNull String... matchedPackages) {
            this.matchedPackages = Arrays.asList(matchedPackages);
        }

        @Override
        public boolean match(@NonNull MetadataReader metadataReader, @NonNull MetadataReaderFactory metadataReaderFactory) throws IOException {
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            return matchedPackages.stream().anyMatch(s -> classMetadata.getClassName().startsWith(s)) ||
                    !classMetadata.isAbstract() ||
                    !classMetadata.isInterface() ||
                    !classMetadata.isAnnotation();
        }
    }
}
