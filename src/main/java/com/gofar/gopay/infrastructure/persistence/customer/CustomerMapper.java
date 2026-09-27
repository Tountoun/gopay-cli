package com.gofar.gopay.infrastructure.persistence.customer;


import com.gofar.gopay.domain.customer.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {


    Customer toCustomer(CustomerEntity customerEntity);

    List<Customer> toCustomers(List<CustomerEntity> customerEntities);

    CustomerEntity toEntity(Customer customer);
}
