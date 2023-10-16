package com.bachlinh.order.http.converter.spi;

import com.bachlinh.order.http.NativeRequest;

public interface RequestConverter<T> extends Converter<NativeRequest<?>, T> {
}
