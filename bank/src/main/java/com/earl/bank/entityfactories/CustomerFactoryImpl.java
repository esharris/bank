package com.earl.bank.entityfactories;

import java.util.HashSet;

import org.springframework.stereotype.Component;

import com.earl.bank.entities.Customer;

@Component
public class CustomerFactoryImpl implements CustomerFactory {

	@Override
	public Customer create(String socialsecurityNumber, String firstName, String lastName) {
		return new Customer(socialsecurityNumber, firstName, lastName, new HashSet<>());
	}
}
