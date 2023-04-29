package com.bachlinh.order.entity.index.spi;

import java.util.Collection;

/**
 * Field descriptor getting field information consist of name of the field and
 * the parent of its.
 *
 * @author Hoang Minh Quan
 */
public interface FieldDescriptor {

    /**
     * Return name of all storeable field in object.
     *
     * @return All field names.
     */
    Collection<String> storeableFields();

    /**
     * Return class parent that fields are in.
     *
     * @return The parent of field.
     */
    Class<?> parent();
}
