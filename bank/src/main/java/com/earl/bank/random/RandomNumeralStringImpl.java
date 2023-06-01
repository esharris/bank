package com.earl.bank.random;

import org.springframework.stereotype.Component;

@Component
public class RandomNumeralStringImpl implements RandomNumeralString {

	private final RandomDigitGenerator randomDigitGenerator;

	public RandomNumeralStringImpl(RandomDigitGenerator randomDigitGenerator) {
		this.randomDigitGenerator = randomDigitGenerator;
	}

	@Override
	public String nextNumeralString(int length) {
		final StringBuilder result = new StringBuilder();
		for (int i = 0; i < length; i++) {
			result.append(randomDigitGenerator.nextDigit());
		}
		return result.toString();
	}
}
