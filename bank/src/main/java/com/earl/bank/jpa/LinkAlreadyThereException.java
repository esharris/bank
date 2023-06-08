package com.earl.bank.jpa;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class LinkAlreadyThereException extends RuntimeException {

	private static final long serialVersionUID = 6609952950986218710L;

	public LinkAlreadyThereException(String socialSecuriityNumber, String accountNumber) {
		super("The customer already has this account. social security number: " + socialSecuriityNumber
				+ ", account number: " + accountNumber);
	}
}
