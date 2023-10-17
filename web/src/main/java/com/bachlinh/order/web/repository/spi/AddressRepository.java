package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Address;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.repository.NativeQueryRepository;

import java.util.Collection;

public interface AddressRepository extends NativeQueryRepository {

    Address composeSave(Address address, CustomerRepository customerRepository);

    Address updateAddress(Address address);

    boolean deleteAddress(Address address);

    void bulkSave(Collection<Address> addresses);

    Address getAddressForUpdate(String id);

    Collection<Address> getAddressOfCustomer(Customer owner);

    void deleteAddresses(Collection<Address> addresses);
}
