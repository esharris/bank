package com.earl.bank.random;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class RandomDigitGenratorImpl implements RandomDigitGenerator {

	/**
	 * Java has other, better random number generators. But using inversion of
	 * control here is like trying to spit hairs.
	 */
	private final Random random = new Random();

	@Override
	public char nextDigit() {
		return (char) ('0' + (random.nextInt(10)));
	}
}
