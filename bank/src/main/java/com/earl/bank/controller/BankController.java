package com.earl.bank.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.earl.bank.dto.BankAccountInput;
import com.earl.bank.dto.CheckingAccountUpdateInput;
import com.earl.bank.dto.CustomerInput;
import com.earl.bank.dto.CustomerUpdateInput;
import com.earl.bank.dto.SavingsAccountUpdateInput;
import com.earl.bank.entities.Account;
import com.earl.bank.entities.CheckingAccount;
import com.earl.bank.entities.Customer;
import com.earl.bank.entities.SavingsAccount;
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

	private final RelationshipManager relationshipManager;

	private final CheckingAccountFactory checkingAccountFactory;

	private final SavingsAccountFactory savingsAccountFactory;

	private final CustomerFactory customerFactory;

	@Autowired
	public BankController(AccountRepository accountRepository,
			RepresentationModelAssembler<Account, EntityModel<Account>> accountModelAssembler,
			CustomerRepository customerRepository,
			RepresentationModelAssembler<Customer, EntityModel<Customer>> customerModelAssembler,
			RelationshipManager relationshipManager, CheckingAccountFactory checkingAccountFactory,
			SavingsAccountFactory savingsAccountFactory, CustomerFactory customerFactory) {
		this.accountRepository = accountRepository;
		this.accountModelAssembler = accountModelAssembler;
		this.customerRepository = customerRepository;
		this.customerModelAssembler = customerModelAssembler;
		this.relationshipManager = relationshipManager;
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
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		return accountModelAssembler.toModel(account);
	}

	@GetMapping("/customers/{socialSecurityNumber}")
	public EntityModel<Customer> retrieveCustomer(@PathVariable String socialSecurityNumber) {
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);
		return customerModelAssembler.toModel(customer);
	}

	@GetMapping("/accounts/{accountNumber}/customers")
	public CollectionModel<EntityModel<Customer>> retrieveCustomersForAccount(@PathVariable String accountNumber) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		List<EntityModel<Customer>> customerList = account.getCustomerSet().stream()
				.map(customerModelAssembler::toModel).toList();
		return CollectionModel.of(customerList,
				linkTo(methodOn(BankController.class).retrieveCustomersForAccount(accountNumber)).withSelfRel());
	}

	@GetMapping("/customers/{socialSecurityNumber}/accounts")
	public CollectionModel<EntityModel<Account>> retrieveAccountsForCustomer(
			@PathVariable String socialSecurityNumber) {
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);
		List<EntityModel<Account>> accountList = customer.getAccountSet().stream().map(accountModelAssembler::toModel)
				.toList();
		return CollectionModel.of(accountList,
				linkTo(methodOn(BankController.class).retrieveAccountsForCustomer(socialSecurityNumber)).withSelfRel());
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

	@PostMapping("/customers/{socialSecurityNumber}/accounts/{accountNumber}")
	public ResponseEntity<Customer> addAccount(@PathVariable String socialSecurityNumber,
			@PathVariable String accountNumber) {
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		relationshipManager.addAccount(customer, account);
		String locationString = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUriString();
		int i = locationString.lastIndexOf("/");
		URI location = URI.create(locationString.substring(0, i));
		return ResponseEntity.created(location).build();
	}

	@PostMapping("accounts/{accountNumber}/customers/{socialSecurityNumber}")
	public ResponseEntity<Account> addStudent(@PathVariable String accountNumber,
			@PathVariable String socialSecurityNumber) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);
		relationshipManager.addCustomer(account, customer);
		String locationString = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUriString();
		int i = locationString.lastIndexOf("/");
		URI location = URI.create(locationString.substring(0, i));
		return ResponseEntity.created(location).build();
	}

	@PutMapping("/customers/{socialSecurityNumber}")
	public ResponseEntity<Customer> replaceEntity(@PathVariable String socialSecurityNumber,
			@RequestBody CustomerUpdateInput customerUpdateInput) {
		Customer customer = CustomerRepositoryHelper.getCustomer(customerRepository, socialSecurityNumber);
		customer.setFirstName(customerUpdateInput.firstName());
		customer.setLastName(customerUpdateInput.lastName());
		customerRepository.save(customer);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
		return ResponseEntity.created(location).build();
	}

	@PutMapping("accounts/{accountNumber}/checking")
	public ResponseEntity<Account> replaceCheckingAccount(@PathVariable String accountNumber,
			@RequestBody CheckingAccountUpdateInput checkingAccountUpdateInput) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		if (account instanceof CheckingAccount) {
			CheckingAccount checkingAccount = (CheckingAccount) account;
			checkingAccount.setBalance(checkingAccountUpdateInput.balance());
			checkingAccount.setRate(checkingAccountUpdateInput.rate());
			checkingAccount.setDebitCardPIN(checkingAccountUpdateInput.debitCardPIN());
			accountRepository.save(checkingAccount);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
			return ResponseEntity.created(location).build();
		} else {
			throw new AccountMismatchException("checking", account.getAccountNumber());
		}
	}

	@PutMapping("accounts/{accountNumber}/savings")
	public ResponseEntity<Account> replaceSavingsAccount(@PathVariable String accountNumber,
			@RequestBody SavingsAccountUpdateInput savingsAccountUpdateInput) {
		Account account = AccountRepositoryHelper.getAccount(accountRepository, accountNumber);
		if (account instanceof SavingsAccount) {
			SavingsAccount savingsAccount = (SavingsAccount) account;
			savingsAccount.setBalance(savingsAccountUpdateInput.balance());
			savingsAccount.setRate(savingsAccountUpdateInput.rate());
			savingsAccount.setSafetyDepositBoxID(savingsAccountUpdateInput.safetyDepositBoxId());
			savingsAccount.setSafetyDepositBoxKey(savingsAccountUpdateInput.safetyDepositBoxKey());
			accountRepository.save(savingsAccount);
			URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri();
			return ResponseEntity.created(location).build();
		} else {
			throw new AccountMismatchException("savings", account.getAccountNumber());
		}
	}
}
