package com.earl.bank.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.earl.bank.entities.Customer;

@Component
public class CustomerModelAssembler implements RepresentationModelAssembler<Customer, EntityModel<Customer>> {

	@Override
	public EntityModel<Customer> toModel(Customer entity) {
		return EntityModel.of(entity, //
				linkTo(methodOn(BankController.class).retrieveAllCustomers()).withRel("customers"),
				linkTo(methodOn(BankController.class).retrieveCustomer(entity.getSocialSecurityNumber()))
						.withSelfRel());
	}

}
