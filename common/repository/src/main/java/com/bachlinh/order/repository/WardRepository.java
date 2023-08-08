package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.Ward;

import java.util.Collection;

public interface WardRepository extends NativeQueryRepository {

    boolean saveAllWard(Collection<Ward> wards);

    long countAllWards();

    Collection<Ward> getAllWards();

    Collection<Ward> getWards(Collection<Integer> ids);

    Collection<Ward> getWardsByDistrict(District district);
}
