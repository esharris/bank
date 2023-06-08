package com.earl.bank.jpa;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class LinkNonexistentException extends RuntimeException {

	private static final long serialVersionUID = 4137163574564294695L;

	public LinkNonexistentException(String socialSecurityNumber, String accountNumber) {
		super("The customer does not have this account. social security number: " + socialSecurityNumber
				+ ", account number: " + accountNumber);
	}

}
