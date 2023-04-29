package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Province;

import java.util.Collection;

public interface ProvinceRepository {

    boolean saveAllProvinces(Collection<Province> provinces);

    long countProvince();

    Collection<Province> getAllProvinces();

    void remove(Province province);
}
