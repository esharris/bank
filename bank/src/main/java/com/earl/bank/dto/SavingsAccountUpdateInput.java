package com.earl.bank.dto;

import java.math.BigDecimal;

public record SavingsAccountUpdateInput(BigDecimal balance, BigDecimal rate, String safetyDepositBoxId,
		String safetyDepositBoxKey) {

}
