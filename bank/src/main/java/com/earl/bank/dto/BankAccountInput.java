package com.earl.bank.dto;

import java.math.BigDecimal;

public record BankAccountInput(long accountNumber, String accountType, BigDecimal initDeposit) {

}
