package cs544.exercise5_3.bank.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;

import cs544.exercise5_3.bank.dao.AccountDAO;
import cs544.exercise5_3.bank.dao.IAccountDAO;
import cs544.exercise5_3.bank.domain.Account;
import cs544.exercise5_3.bank.domain.Customer;
import cs544.exercise5_3.bank.jms.IJMSSender;
import cs544.exercise5_3.bank.jms.JMSSender;
import cs544.exercise5_3.bank.logging.ILogger;
import cs544.exercise5_3.bank.logging.Logger;
//@Named
@ManagedBean
@SessionScoped
public class AccountService implements IAccountService, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IAccountDAO accountDAO;
	private ICurrencyConverter currencyConverter;
	private IJMSSender jmsSender;
	private ILogger logger;
	 long accountNumber;
	private long amount;
	private String customerName;


	public String create() {
		System.out.println("create");
		return "success";
	}
	
	
	public String createAccount() {
		System.out.println("createAccountcreateAccountcreateAccount");
		Account account = new Account(accountNumber);
		Customer customer = new Customer(customerName);
		account.setCustomer(customer);
		accountDAO.saveAccount(account);
		

		ArrayList<Account> acc = (ArrayList<Account>) accountDAO.getAccounts();
		System.out.println(acc.size());
		logger.log(
				"createAccount with parameters accountNumber= " + accountNumber + " , customerName= " + customerName);
		return "home";
	}
	
	public Collection<Account> getAllAccounts() {
		return  accountDAO.getAccounts();
	}
	
	public ArrayList<Account> getAllAccounts2() {
		ArrayList<Account> acc = (ArrayList<Account>) accountDAO.getAccounts();
		return acc;
	}
	//================================================
	


	public AccountService() {
		accountDAO = new AccountDAO();
		currencyConverter = new CurrencyConverter();
		jmsSender = new JMSSender();
		logger = new Logger();
	}

	public Account createAccount(long accountNumber, String customerName) {
		Account account = new Account(accountNumber);
		Customer customer = new Customer(customerName);
		account.setCustomer(customer);
		accountDAO.saveAccount(account);
		
		logger.log(
				"createAccount with parameters accountNumber= " + accountNumber + " , customerName= " + customerName);
		return account;
	}



	public void deposit(long accountNumber, double amount) {
		Account account = accountDAO.loadAccount(accountNumber);
		account.deposit(amount);
		accountDAO.updateAccount(account);
		logger.log("deposit with parameters accountNumber= " + accountNumber + " , amount= " + amount);
		if (amount > 10000) {
			jmsSender.sendJMSMessage("Deposit of $ " + amount + " to account with accountNumber= " + accountNumber);
		}
	}
	public String deposit() {
		Account account = accountDAO.loadAccount(accountNumber);
		account.deposit(amount);
		accountDAO.updateAccount(account);
		logger.log("deposit with parameters accountNumber= " + accountNumber + " , amount= " + amount);
		if (amount > 10000) {
			jmsSender.sendJMSMessage("Deposit of $ " + amount + " to account with accountNumber= " + accountNumber);
		}
		return "home";
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public Account getAccount(long accountNumber) {
		Account account = accountDAO.loadAccount(accountNumber);
		return account;
	}


	public void withdraw(long accountNumber, double amount) {
		Account account = accountDAO.loadAccount(accountNumber);
		account.withdraw(amount);
		accountDAO.updateAccount(account);
		logger.log("withdraw with parameters accountNumber= " + accountNumber + " , amount= " + amount);
	}
	public String withdraw() {
		Account account = accountDAO.loadAccount(accountNumber);
		account.withdraw(amount);
		accountDAO.updateAccount(account);
		logger.log("withdraw with parameters accountNumber= " + accountNumber + " , amount= " + amount);
		return "home";
	}

	public void depositEuros(long accountNumber, double amount) {
		Account account = accountDAO.loadAccount(accountNumber);
		double amountDollars = currencyConverter.euroToDollars(amount);
		account.deposit(amountDollars);
		accountDAO.updateAccount(account);
		logger.log("depositEuros with parameters accountNumber= " + accountNumber + " , amount= " + amount);
		if (amountDollars > 10000) {
			jmsSender.sendJMSMessage("Deposit of $ " + amount + " to account with accountNumber= " + accountNumber);
		}
	}

	public void withdrawEuros(long accountNumber, double amount) {
		Account account = accountDAO.loadAccount(accountNumber);
		double amountDollars = currencyConverter.euroToDollars(amount);
		account.withdraw(amountDollars);
		accountDAO.updateAccount(account);
		logger.log("withdrawEuros with parameters accountNumber= " + accountNumber + " , amount= " + amount);
	}

	public void transferFunds(long fromAccountNumber, long toAccountNumber, double amount, String description) {
		Account fromAccount = accountDAO.loadAccount(fromAccountNumber);
		Account toAccount = accountDAO.loadAccount(toAccountNumber);
		fromAccount.transferFunds(toAccount, amount, description);
		accountDAO.updateAccount(fromAccount);
		accountDAO.updateAccount(toAccount);
		logger.log("transferFunds with parameters fromAccountNumber= " + fromAccountNumber + " , toAccountNumber= "
				+ toAccountNumber + " , amount= " + amount + " , description= " + description);
		if (amount > 10000) {
			jmsSender.sendJMSMessage("TransferFunds of $ " + amount + " from account with accountNumber= " + fromAccount
					+ " to account with accountNumber= " + toAccount);
		}
	}
	
	
	
	public long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
}
