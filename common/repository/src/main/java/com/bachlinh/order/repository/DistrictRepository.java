package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.District;

import java.util.Collection;

public interface DistrictRepository {

    boolean saveAllDistrict(Collection<District> districts);

    long countDistrict();

    Collection<District> getAllDistrict();
}