package com.earl.bank.controller;

public class CustomerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4501818700053623278L;

	public CustomerNotFoundException(String socialSecurityNumber) {
		super("social security number=" + socialSecurityNumber);
	}
}
