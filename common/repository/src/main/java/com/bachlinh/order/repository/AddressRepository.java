package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Address;

import java.util.Collection;

public interface AddressRepository extends NativeQueryRepository {

    Address composeSave(Address address, CustomerRepository customerRepository);

    Address updateAddress(Address address);

    boolean deleteAddress(Address address);

    void bulkSave(Collection<Address> addresses);
}
