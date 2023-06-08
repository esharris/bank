package com.earl.bank.jpa.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE)
public class DuplicateAccountNumberException extends RuntimeException {
	private static final long serialVersionUID = 7238651371301457460L;

	public DuplicateAccountNumberException(String accountNumber) {
		super("Seen duplicate account number " + accountNumber + ". It must be unique.");
	}
}
