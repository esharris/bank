package com.earl.bank.jpa;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class DuplicateSocialSecurityNumberException extends RuntimeException {

	private static final long serialVersionUID = -922101033894731027L;

	public DuplicateSocialSecurityNumberException(String socialSecurityNumber) {
		super("Seen duplicate social security number " + socialSecurityNumber + ". It must be unique.");
	}
}
