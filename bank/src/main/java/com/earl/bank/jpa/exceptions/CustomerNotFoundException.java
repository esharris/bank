package com.earl.bank.jpa.exceptions;

public class CustomerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4501818700053623278L;

	public CustomerNotFoundException(String socialSecurityNumber) {
		super("Customer not found. social security number: " + socialSecurityNumber);
	}
}
