package com.bachlinh.order.aot.locator;

import org.springframework.lang.NonNull;

import java.util.Collection;

public interface Scanner<T> {
    
    @NonNull
    ScanResult<T> scan(Collection<String> packages);
}

