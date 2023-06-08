package com.earl.bank.jpa;

import java.util.Optional;

import com.earl.bank.entities.Customer;
import com.earl.bank.jpa.exceptions.CustomerNotFoundException;
import com.earl.bank.jpa.exceptions.DuplicateSocialSecurityNumberException;

public class CustomerRepositoryHelper {

	public static Customer saveIfSocialSecurityNumberUnique(CustomerRepository customerRepository, Customer customer) {
		if (!customerRepository.existsBySocialSecurityNumber(customer.getSocialSecurityNumber())) {
			return customerRepository.save(customer);
		} else {
			throw new DuplicateSocialSecurityNumberException(customer.getSocialSecurityNumber());
		}
	}

	public static Customer getCustomer(CustomerRepository customerRepository, String socialSecurityNumber) {
		Optional<Customer> optionalCustomer = customerRepository.findBySocialSecurityNumber(socialSecurityNumber);
		return optionalCustomer.orElseThrow(() -> new CustomerNotFoundException(socialSecurityNumber));
	}
}
