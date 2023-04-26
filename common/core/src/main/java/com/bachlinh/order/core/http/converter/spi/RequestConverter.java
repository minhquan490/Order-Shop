package com.bachlinh.order.core.http.converter.spi;

import com.bachlinh.order.core.http.NativeRequest;

public interface RequestConverter<T> extends Converter<NativeRequest<?>, T> {
}
