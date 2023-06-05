package com.earl.bank.dto;

import java.math.BigDecimal;

public record BankAccountInput(String accountNumber, String accountType, BigDecimal initDeposit) {

}
