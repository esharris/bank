package com.earl.bank.dtofactories;

import org.springframework.stereotype.Component;

import com.earl.bank.dto.CustomerInput;

@Component
public class CustomerInputFactoryImpl implements CustomerInputFactory {

	@Override
	public CustomerInput create(String dataRow) {
		String[] dataRecords = dataRow.split(",");
		if (dataRecords.length == 3) {
			String socialSecurityNumber = dataRecords[0];
			String firstName = dataRecords[1];
			String lastName = dataRecords[2];
			return new CustomerInput(socialSecurityNumber, firstName, lastName);
		} else {
			throw new RuntimeException("Expected 3 customer components, but saw " + dataRecords.length);
		}
	}
}
