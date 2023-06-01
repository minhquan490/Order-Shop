package com.bachlinh.order.setup.execution;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.core.http.template.spi.RestTemplate;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.entity.model.Ward;
import com.bachlinh.order.repository.DistrictRepository;
import com.bachlinh.order.repository.ProvinceRepository;
import com.bachlinh.order.repository.WardRepository;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.setup.spi.AbstractSetup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Log4j2
@ActiveReflection
public class VnAddressSetupExecution extends AbstractSetup {
    private static final String DIVISION_TYPE = "division_type";
    private static final String CODE_NAME = "codename";
    private static final String NAME_FIELD = "name";
    private static final String CODE_FIELD = "code";

    private String provinceApi;
    private String districtApi;
    private String wardApi;

    private EntityFactory entityFactory;
    private RestTemplate restTemplate;
    private ProvinceRepository provinceRepository;
    private DistrictRepository districtRepository;
    private WardRepository wardRepository;

    @ActiveReflection
    public VnAddressSetupExecution(ContainerWrapper wrapper, String profile) {
        super(wrapper, profile);
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            entityFactory = getDependenciesResolver().resolveDependencies(EntityFactory.class);
        }
        if (restTemplate == null) {
            restTemplate = getDependenciesResolver().resolveDependencies(RestTemplate.class);
        }
        if (provinceRepository == null) {
            provinceRepository = getDependenciesResolver().resolveDependencies(ProvinceRepository.class);
        }
        if (districtRepository == null) {
            districtRepository = getDependenciesResolver().resolveDependencies(DistrictRepository.class);
        }
        if (wardRepository == null) {
            wardRepository = getDependenciesResolver().resolveDependencies(WardRepository.class);
        }
        if (provinceApi == null) {
            provinceApi = getEnvironment().getProperty("address.province.vn.api.url");
        }
        if (districtApi == null) {
            districtApi = getEnvironment().getProperty("address.district.vn.api.url");
        }
        if (wardApi == null) {
            wardApi = getEnvironment().getProperty("address.ward.vn.api.url");
        }
    }

    @Override
    protected void doBefore() {
        log.info("Begin index all address in Viet Nam");
    }

    @Override
    protected void doExecute() {
        indexProvince();
        indexDistrict();
        indexWard();
    }

    @Override
    protected void doAfter() {
        log.info("Index all address in Viet Nam complete");
    }

    private void indexProvince() {
        if (provinceRepository.countProvince() == 63) {
            return;
        } else {
            Collection<Province> provinces = provinceRepository.getAllProvinces();
            provinces.forEach(province -> {
                if (log.isDebugEnabled()) {
                    log.debug("Remove province [{}]", province.getId());
                }
                provinceRepository.remove(province);
            });
        }
        JsonNode resp;
        try {
            resp = restTemplate.get(provinceApi, null, null, Collections.emptyMap());
        } catch (IOException e) {
            log.warn("Call province api failure");
            return;
        }
        Collection<Province> provinces = new ArrayList<>();
        resp.forEach(province -> provinces.add(parseProvince(province)));
        boolean result = provinceRepository.saveAllProvinces(provinces);
        if (!result) {
            log.warn("Save provinces into database failure");
        }
    }

    private Province parseProvince(JsonNode jsonSource) {
        Province province = entityFactory.getEntity(Province.class);
        province.setName(jsonSource.get(NAME_FIELD).asText());
        province.setCode(jsonSource.get(CODE_FIELD).asInt());
        province.setDivisionType(jsonSource.get(DIVISION_TYPE).asText());
        province.setCodeName(jsonSource.get(CODE_NAME).asText());
        province.setPhoneCode(jsonSource.get("phone_code").asInt());
        return province;
    }

    private void indexDistrict() {
        if (districtRepository.countDistrict() != 0) {
            return;
        }
        JsonNode resp;
        try {
            resp = restTemplate.get(districtApi, null, null, Collections.emptyMap());
        } catch (IOException e) {
            log.warn("Failure when process district response");
            return;
        }
        List<Province> provinces = provinceRepository.getAllProvinces()
                .stream()
                .sorted(Comparator.comparingInt(Province::getCode))
                .toList();
        Collection<District> districts = new ArrayList<>();
        resp.forEach(district -> {
            int provinceCode = district.get("province_code").asInt();
            Province province = findProvince(provinces, provinceCode);
            if (province == null) {
                throw new IllegalArgumentException("Can not find province in database");
            }
            districts.add(parseDistrict(district, province));
        });
        boolean result = districtRepository.saveAllDistrict(districts);
        if (!result) {
            log.warn("Saving district to database failure");
        }
    }

    private Province findProvince(List<Province> provinces, int code) {
        int low = 0;
        int high = provinces.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            Province province = provinces.get(mid);
            int cmp = province.getCode() - code;
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return provinces.get(mid);
            }
        }
        return null;
    }

    private District parseDistrict(JsonNode jsonSource, Province province) {
        District district = entityFactory.getEntity(District.class);
        district.setName(jsonSource.get(NAME_FIELD).asText());
        district.setCode(jsonSource.get(CODE_FIELD).asInt());
        district.setDivisionType(jsonSource.get(DIVISION_TYPE).asText());
        district.setCodeName(jsonSource.get(CODE_NAME).asText());
        district.setProvince(province);
        return district;
    }

    private void indexWard() {
        if (wardRepository.countAllWards() != 0) {
            return;
        }
        JsonNode resp;
        try {
            resp = restTemplate.get(wardApi, null, null, Collections.emptyMap());
        } catch (IOException e) {
            log.warn("Failure when process district response");
            return;
        }
        List<District> districts = districtRepository.getAllDistrict()
                .stream()
                .sorted(Comparator.comparingInt(District::getCode))
                .toList();
        Collection<Ward> wards = new ArrayList<>();
        resp.forEach(ward -> {
            int districtCode = ward.get("district_code").asInt();
            District district = findDistrict(districts, districtCode);
            if (district == null) {
                throw new IllegalArgumentException("Can not find district");
            }
            wards.add(parseWard(ward, district));
        });
        boolean result = wardRepository.saveAllWard(wards);
        if (!result) {
            log.warn("Saving district to database failure");
        }
    }

    private Ward parseWard(JsonNode jsonSource, District district) {
        Ward ward = entityFactory.getEntity(Ward.class);
        ward.setName(jsonSource.get(NAME_FIELD).asText());
        ward.setCode(jsonSource.get(CODE_FIELD).asInt());
        ward.setDivisionType(jsonSource.get(DIVISION_TYPE).asText());
        ward.setCodeName(jsonSource.get(CODE_NAME).asText());
        ward.setDistrict(district);
        return ward;
    }

    private District findDistrict(List<District> districts, int code) {
        int low = 0;
        int high = districts.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            District district = districts.get(mid);
            int cmp = district.getCode() - code;
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return districts.get(mid);
            }
        }
        return null;
    }
}
