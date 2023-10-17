package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.repository.NativeQueryRepository;

import java.util.Collection;

public interface ProvinceRepository extends NativeQueryRepository {

    Province getProvinceById(String provinceId);

    Province getAddress(String provinceId, String districtId, String wardId);

    boolean saveAllProvinces(Collection<Province> provinces);

    long countProvince();

    void remove(Province province);

    Collection<Province> getAllProvinces();

    Collection<Province> getProvincesById(Collection<String> ids);
}
