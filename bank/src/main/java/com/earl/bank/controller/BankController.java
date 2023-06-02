package com.earl.bank.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.earl.bank.dto.BankAccountInput;
import com.earl.bank.entities.Account;
import com.earl.bank.entityfactories.CheckingAccountFactory;
import com.earl.bank.entityfactories.SavingsAccountFactory;
import com.earl.bank.jpa.AccountRepository;

@RestController
public class BankController {

	private final AccountRepository accountRepository;
	private final RepresentationModelAssembler<Account, EntityModel<Account>> accountModelAssembler;

	private final CheckingAccountFactory checkingAccountFactory;

	private final SavingsAccountFactory savingsAccountFactory;

	@Autowired
	public BankController(AccountRepository accountRepository,
			RepresentationModelAssembler<Account, EntityModel<Account>> accountModelAssembler,
			CheckingAccountFactory checkingAccountFactory, SavingsAccountFactory savingsAccountFactory) {
		this.accountRepository = accountRepository;
		this.accountModelAssembler = accountModelAssembler;
		this.savingsAccountFactory = savingsAccountFactory;
		this.checkingAccountFactory = checkingAccountFactory;
	}

	@GetMapping("/bank")
	public CollectionModel<EntityModel<Account>> retrieveAllAccounts() {
		List<EntityModel<Account>> accountList = accountRepository.findAll().stream()
				.map(accountModelAssembler::toModel).toList();
		return CollectionModel.of(accountList,
				linkTo(methodOn(BankController.class).retrieveAllAccounts()).withSelfRel());
	}

	@GetMapping("/bank/{accountNumber}")
	public EntityModel<Account> retrieveAccount(@PathVariable String accountNumber) {
		Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
		Account account = optionalAccount.orElseThrow(() -> new AccountNotFoundException(accountNumber));
		return accountModelAssembler.toModel(account);
	}

	@PostMapping("/bank")
	ResponseEntity<?> newAccount(@RequestBody BankAccountInput bankAccountInput) {
		Account savedAccount;
		if ("Checking".equals(bankAccountInput.accountType())) {
			savedAccount = accountRepository
					.save(checkingAccountFactory.create(bankAccountInput.firstName(), bankAccountInput.lastName(),
							bankAccountInput.socialSecurityNumber(), bankAccountInput.initDeposit()));
		} else if ("Savings".equals(bankAccountInput.accountType())) {
			savedAccount = accountRepository
					.save(savingsAccountFactory.create(bankAccountInput.firstName(), bankAccountInput.lastName(),
							bankAccountInput.socialSecurityNumber(), bankAccountInput.initDeposit()));
		} else {
			throw new UnknownAccountTypeException(bankAccountInput.accountType());
		}
		EntityModel<?> entityModel = accountModelAssembler.toModel(savedAccount);
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}
}
