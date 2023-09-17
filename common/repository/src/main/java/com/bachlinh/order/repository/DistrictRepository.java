package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.repository.NativeQueryRepository;

import java.util.Collection;

public interface DistrictRepository extends NativeQueryRepository {

    District getDistrictById(String districtId);

    boolean saveAllDistrict(Collection<District> districts);

    long countDistrict();

    Collection<District> getAllDistrict();

    Collection<District> getDistricts(Collection<String> ids);

    Collection<District> getDistrictsByProvince(String provinceId);
}