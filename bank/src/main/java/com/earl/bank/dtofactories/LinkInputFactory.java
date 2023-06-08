package com.earl.bank.dtofactories;

import com.earl.bank.dto.LinkInput;

public interface LinkInputFactory {

	LinkInput create(String dataRow);

}