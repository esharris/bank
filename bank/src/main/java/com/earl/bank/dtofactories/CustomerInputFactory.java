package com.earl.bank.dtofactories;

import com.earl.bank.dto.CustomerInput;

public interface CustomerInputFactory {

	CustomerInput create(String dataRow);

}