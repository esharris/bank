package com.earl.bank.entityfactories;

import java.math.BigDecimal;

public class BaseRateSingleton {

	private static BaseRateSingleton instance = new BaseRateSingleton("2.5");

	public static BaseRateSingleton getInstance() {
		return instance;
	}

	private final BigDecimal value;

	private BaseRateSingleton(String value) {
		super();
		this.value = new BigDecimal(value);
	}

	public BigDecimal getValue() {
		return value;
	}

}
