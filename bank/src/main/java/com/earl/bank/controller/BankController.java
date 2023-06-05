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
import com.earl.bank.dto.CustomerInput;
import com.earl.bank.entities.Account;
import com.earl.bank.entities.Customer;
import com.earl.bank.entityfactories.CheckingAccountFactory;
import com.earl.bank.entityfactories.CustomerFactory;
import com.earl.bank.entityfactories.SavingsAccountFactory;
import com.earl.bank.jpa.AccountRepository;
import com.earl.bank.jpa.AccountRepositoryHelper;
import com.earl.bank.jpa.CustomerRepository;
import com.earl.bank.jpa.CustomerRepositoryHelper;

@RestController
public class BankController {

	private final AccountRepository accountRepository;

	private final RepresentationModelAssembler<Account, EntityModel<Account>> accountModelAssembler;

	private final CustomerRepository customerRepository;

	private final RepresentationModelAssembler<Customer, EntityModel<Customer>> customerModelAssembler;

	private final CheckingAccountFactory checkingAccountFactory;

	private final SavingsAccountFactory savingsAccountFactory;

	private final CustomerFactory customerFactory;

	@Autowired
	public BankController(AccountRepository accountRepository,
			RepresentationModelAssembler<Account, EntityModel<Account>> accountModelAssembler,
			CustomerRepository customerRepository,
			RepresentationModelAssembler<Customer, EntityModel<Customer>> customerModelAssembler,
			CheckingAccountFactory checkingAccountFactory, SavingsAccountFactory savingsAccountFactory,
			CustomerFactory customerFactory) {
		this.accountRepository = accountRepository;
		this.accountModelAssembler = accountModelAssembler;
		this.customerRepository = customerRepository;
		this.customerModelAssembler = customerModelAssembler;
		this.savingsAccountFactory = savingsAccountFactory;
		this.checkingAccountFactory = checkingAccountFactory;
		this.customerFactory = customerFactory;
	}

	@GetMapping("/accounts")
	public CollectionModel<EntityModel<Account>> retrieveAllAccounts() {
		List<EntityModel<Account>> accountList = accountRepository.findAll().stream()
				.map(accountModelAssembler::toModel).toList();
		return CollectionModel.of(accountList,
				linkTo(methodOn(BankController.class).retrieveAllAccounts()).withSelfRel());
	}

	@GetMapping("/customers")
	public CollectionModel<EntityModel<Customer>> retrieveAllCustomers() {
		List<EntityModel<Customer>> customerList = customerRepository.findAll().stream()
				.map(customerModelAssembler::toModel).toList();
		return CollectionModel.of(customerList,
				linkTo(methodOn(BankController.class).retrieveAllAccounts()).withSelfRel());
	}

	@GetMapping("/accounts/{accountNumber}")
	public EntityModel<Account> retrieveAccount(@PathVariable String accountNumber) {
		Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
		Account account = optionalAccount.orElseThrow(() -> new AccountNotFoundException(accountNumber));
		return accountModelAssembler.toModel(account);
	}

	@GetMapping("/customers/{socialSecurityNumber}")
	public EntityModel<Customer> retrieveCustomer(@PathVariable String socialSecurityNumber) {
		Optional<Customer> optionalCustomer = customerRepository.findBySocialSecurityNumber(socialSecurityNumber);
		Customer customer = optionalCustomer.orElseThrow(() -> new CustomerNotFoundException(socialSecurityNumber));
		return customerModelAssembler.toModel(customer);
	}

	@PostMapping("/accounts")
	ResponseEntity<?> newAccount(@RequestBody BankAccountInput bankAccountInput) {
		Account savedAccount;
		if ("Checking".equals(bankAccountInput.accountType())) {
			savedAccount = AccountRepositoryHelper.saveIfAccountNumberUnique(accountRepository,
					checkingAccountFactory.create(bankAccountInput.accountNumber(), bankAccountInput.initDeposit()));
		} else if ("Savings".equals(bankAccountInput.accountType())) {
			savedAccount = AccountRepositoryHelper.saveIfAccountNumberUnique(accountRepository,
					savingsAccountFactory.create(bankAccountInput.accountNumber(), bankAccountInput.initDeposit()));
		} else {
			throw new UnknownAccountTypeException(bankAccountInput.accountType());
		}
		EntityModel<?> entityModel = accountModelAssembler.toModel(savedAccount);
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}

	@PostMapping("/customers")
	ResponseEntity<?> newCustomer(@RequestBody CustomerInput customerInput) {
		Customer savedCustomer = CustomerRepositoryHelper.saveIfSocialSecurityNumberUnique(customerRepository,
				customerFactory.create(customerInput.socialSecurityNumber(), customerInput.firstName(),
						customerInput.lastName()));
		EntityModel<?> entityModel = customerModelAssembler.toModel(savedCustomer);
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}
}
