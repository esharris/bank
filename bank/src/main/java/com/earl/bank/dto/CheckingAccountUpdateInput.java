package com.earl.bank.dto;

import java.math.BigDecimal;

public record CheckingAccountUpdateInput(BigDecimal balance, BigDecimal rate, String debitCardPIN) {

}
