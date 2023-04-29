package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Address;

public interface AddressRepository {

    Address composeSave(Address address, CustomerRepository customerRepository);

    Address updateAddress(Address address);

    boolean deleteAddress(Address address);
}
