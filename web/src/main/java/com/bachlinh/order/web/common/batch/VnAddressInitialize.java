package com.bachlinh.order.web.common.batch;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bachlinh.order.batch.job.AbstractJob;
import com.bachlinh.order.batch.job.JobType;
import com.bachlinh.order.core.annotation.BatchJob;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.http.client.RestClient;
import com.bachlinh.order.entity.EntityFactory;
import com.bachlinh.order.entity.model.District;
import com.bachlinh.order.entity.model.Province;
import com.bachlinh.order.entity.model.VnAddress;
import com.bachlinh.order.entity.model.Ward;
import com.bachlinh.order.web.repository.spi.DistrictRepository;
import com.bachlinh.order.web.repository.spi.ProvinceRepository;
import com.bachlinh.order.web.repository.spi.WardRepository;

@BatchJob(name = "vnAddressInitialize")
public class VnAddressInitialize extends AbstractJob {

    private static final String DIVISION_TYPE = "division_type";
    private static final String CODE_NAME = "codename";
    private static final String NAME_FIELD = "name";
    private static final String CODE_FIELD = "code";

    private final LocalDateTime now = LocalDateTime.now();
    private final Logger log = LoggerFactory.getLogger(getClass());

    private String provinceApi;
    private String districtApi;
    private String wardApi;

    private EntityFactory entityFactory;
    private RestClient restClient;
    private ProvinceRepository provinceRepository;
    private DistrictRepository districtRepository;
    private WardRepository wardRepository;

    private VnAddressInitialize(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        super(name, activeProfile, dependenciesResolver);
    }

    @Override
    public AbstractJob newInstance(String name, String activeProfile, DependenciesResolver dependenciesResolver) {
        return new VnAddressInitialize(name, activeProfile, dependenciesResolver);
    }

    @Override
    protected void inject() {
        if (entityFactory == null) {
            entityFactory = resolveDependencies(EntityFactory.class);
        }
        if (restClient == null) {
            restClient = resolveDependencies(RestClient.class);
        }
        if (provinceRepository == null) {
            provinceRepository = resolveRepository(ProvinceRepository.class);
        }
        if (districtRepository == null) {
            districtRepository = resolveRepository(DistrictRepository.class);
        }
        if (wardRepository == null) {
            wardRepository = resolveRepository(WardRepository.class);
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
    protected void doExecuteInternal() {
        indexProvince();
        indexDistrict();
        indexWard();
    }

    @Override
    protected LocalDateTime doGetNextExecutionTime() {
        return doGetPreviousExecutionTime();
    }

    @Override
    protected LocalDateTime doGetPreviousExecutionTime() {
        return now;
    }

    @Override
    public JobType getJobType() {
        return JobType.ONCE;
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
            resp = restClient.get(provinceApi, null, null);
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

    @SuppressWarnings("rawtypes")
    private void indexDistrict() {
        if (districtRepository.countDistrict() != 0) {
            return;
        }
        JsonNode resp;
        try {
            resp = restClient.get(districtApi, null, null);
        } catch (IOException e) {
            log.warn("Failure when process district response");
            return;
        }
        List<VnAddress> provinces = provinceRepository.getAllProvinces()
                .stream()
                .sorted(Comparator.comparingInt(Province::getCode))
                .map(VnAddress.class::cast)
                .toList();
        Collection<District> districts = new ArrayList<>();
        resp.forEach(district -> {
            int provinceCode = district.get("province_code").asInt();
            Province province = (Province) findVnAddress(provinces, provinceCode);
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

    @SuppressWarnings("rawtypes")
    private VnAddress<?> findVnAddress(List<VnAddress> addresses, int code) {
        int low = 0;
        int high = addresses.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            VnAddress<?> address = addresses.get(mid);
            int cmp = address.getCode() - code;
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return addresses.get(mid);
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

    @SuppressWarnings("rawtypes")
    private void indexWard() {
        if (wardRepository.countAllWards() != 0) {
            return;
        }
        JsonNode resp;
        try {
            resp = restClient.get(wardApi, null, null);
        } catch (IOException e) {
            log.warn("Failure when process district response");
            return;
        }
        List<VnAddress> districts = districtRepository.getAllDistrict()
                .stream()
                .sorted(Comparator.comparingInt(District::getCode))
                .map(VnAddress.class::cast)
                .toList();
        Collection<Ward> wards = new ArrayList<>();
        resp.forEach(ward -> {
            int districtCode = ward.get("district_code").asInt();
            District district = (District) findVnAddress(districts, districtCode);
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
}
