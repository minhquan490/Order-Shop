package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Province;

import java.util.Collection;

public interface ProvinceRepository {

    boolean saveAllProvinces(Collection<Province> provinces);

    long countProvince();

    void remove(Province province);

    Collection<Province> getAllProvinces();

    Collection<Province> getProvincesById(Collection<String> ids);
}
