package com.earl.bank.entityfactories;

import com.earl.bank.entities.Customer;

public interface CustomerFactory {

	Customer create(String socialsecurityNumber, String firstName, String lastName);

}