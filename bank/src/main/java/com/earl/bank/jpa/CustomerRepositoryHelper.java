package com.earl.bank.jpa;

import com.earl.bank.entities.Customer;

public class CustomerRepositoryHelper {

	public static Customer saveIfSocialSecurityNumberUnique(CustomerRepository customerRepository, Customer customer) {
		if (!customerRepository.existsBySocialSecurityNumber(customer.getSocialSecurityNumber())) {
			return customerRepository.save(customer);
		} else {
			throw new DuplicateSocialSecurityNumberException(customer.getSocialSecurityNumber());
		}
	}
}
