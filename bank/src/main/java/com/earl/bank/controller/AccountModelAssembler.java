package com.earl.bank.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.earl.bank.entities.Account;

@Component
public class AccountModelAssembler implements RepresentationModelAssembler<Account, EntityModel<Account>> {

	@Override
	public EntityModel<Account> toModel(Account entity) {
		return EntityModel.of(entity, //
				linkTo(methodOn(BankController.class).retrieveAllAccounts()).withRel("accounts"),
				linkTo(methodOn(BankController.class).retrieveAccount(entity.getAccountNumber())).withSelfRel());
	}

}
