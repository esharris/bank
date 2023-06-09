package com.earl.bank.jpa.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -723919102700807002L;

	public AccountNotFoundException(long accountNumber) {
		super("Account not found. account number: " + accountNumber);
	}
}
