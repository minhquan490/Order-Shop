package com.bachlinh.order.entity;

public interface TableMetadataHolder {
    String getTableName();

    String getColumn(String entityFieldName);
}
