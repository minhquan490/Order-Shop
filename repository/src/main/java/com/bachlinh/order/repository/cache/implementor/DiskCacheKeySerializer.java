package com.bachlinh.order.repository.cache.implementor;

import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DiskCacheKeySerializer implements Serializer<Object> {

    @Override
    public ByteBuffer serialize(Object object) throws SerializerException {
        if (object instanceof String casted) {
            return ByteBuffer.wrap(casted.getBytes(StandardCharsets.UTF_8));
        } else {
            onCaseIsNotString();
            return null;
        }
    }

    @Override
    public Object read(ByteBuffer binary) throws ClassNotFoundException, SerializerException {
        return new String(binary.array(), StandardCharsets.UTF_8);
    }

    @Override
    public boolean equals(Object object, ByteBuffer binary) throws ClassNotFoundException, SerializerException {
        if (object instanceof String casted) {
            byte[] stringData = casted.getBytes(StandardCharsets.UTF_8);
            byte[] binaryData = binary.array();
            return Arrays.equals(stringData, binaryData);
        } else {
            onCaseIsNotString();
            return false;
        }
    }

    private void onCaseIsNotString() {
        throw new SerializerException("Only string is permitted");
    }
}
