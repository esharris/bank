package com.earl.bank.random;

import org.springframework.stereotype.Component;

@Component
public class RandomAccountNumberGeneratorImpl implements RandomAccountNumberGenerator {

	private static long index = 10000;

	private final RandomNumeralString randomNumeralString;

	public RandomAccountNumberGeneratorImpl(RandomNumeralString randomNumeralString) {
		this.randomNumeralString = randomNumeralString;
	}

	@Override
	public String nextAccountNumber(String socialSecurityNumber) {
		int socialSecurityNumberLength = socialSecurityNumber.length();
		String lastTwoOfSSN = socialSecurityNumber.substring(socialSecurityNumberLength - 2,
				socialSecurityNumberLength);
		long uniqueID = index++;
		String randomNumericString = randomNumeralString.nextNumeralString(3);
		return lastTwoOfSSN + uniqueID + randomNumericString;
	}
}
