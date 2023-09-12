package com.bachlinh.order.repository.cache.implementor;

import org.ehcache.spi.serialization.Serializer;
import org.ehcache.spi.serialization.SerializerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DiskCacheValueSerializer implements Serializer<Object> {
    private final Logger logger;

    public DiskCacheValueSerializer() {
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public ByteBuffer serialize(Object object) throws SerializerException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
            byte[] data = byteArrayOutputStream.toByteArray();
            return ByteBuffer.wrap(data);
        } catch (IOException e) {
            logger.error("Serialize cache value failure", e);
            return null;
        }
    }

    @Override
    public Object read(ByteBuffer binary) throws ClassNotFoundException, SerializerException {
        byte[] data = binary.array();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return ois.readObject();
        } catch (IOException e) {
            logger.error("Read cache value failure", e);
            return null;
        }
    }

    @Override
    public boolean equals(Object object, ByteBuffer binary) throws ClassNotFoundException, SerializerException {
        byte[] bytes = serialize(object).array();
        byte[] binaryArr = binary.array();
        return Arrays.equals(bytes, binaryArr);
    }
}
