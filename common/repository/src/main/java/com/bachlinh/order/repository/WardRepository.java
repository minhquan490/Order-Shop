package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Ward;

import java.util.Collection;

public interface WardRepository {

    boolean saveAllWard(Collection<Ward> wards);

    long countAllWards();

    Collection<Ward> getAllWards();

    Collection<Ward> getWards(Collection<Integer> ids);
}
