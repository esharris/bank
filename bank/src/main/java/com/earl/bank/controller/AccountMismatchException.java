package com.earl.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class AccountMismatchException extends RuntimeException {

	private static final long serialVersionUID = -9006395008073416073L;

	public AccountMismatchException(String accountType, long accountNumber) {
		super("Expecting " + accountType + ". account number: " + accountNumber);
	}
}
