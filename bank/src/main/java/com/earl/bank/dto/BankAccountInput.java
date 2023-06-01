package com.earl.bank.dto;

import java.math.BigDecimal;

public record BankAccountInput(String firstName, String lastName, String socialSecurityNumber, String accountType,
		BigDecimal initDeposit) {

}
