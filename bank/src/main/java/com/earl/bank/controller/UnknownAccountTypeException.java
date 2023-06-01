package com.earl.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UnknownAccountTypeException extends RuntimeException {

	private static final long serialVersionUID = -8450034825380890886L;

	public UnknownAccountTypeException(String accountType) {
		super("Unknown account type; " + accountType);
	}
}
