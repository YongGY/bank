package cs544.exercise5_3.bank.service;

import java.io.Serializable;
import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

import cs544.exercise5_3.bank.dao.AccountDAO;
import cs544.exercise5_3.bank.dao.IAccountDAO;
import cs544.exercise5_3.bank.domain.Account;
import cs544.exercise5_3.bank.domain.Customer;
import cs544.exercise5_3.bank.jms.IJMSSender;
import cs544.exercise5_3.bank.jms.JMSSender;
import cs544.exercise5_3.bank.logging.ILogger;
import cs544.exercise5_3.bank.logging.Logger;
@Named
@ApplicationScoped
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
	private Account account;

	public AccountService() {
		accountDAO = new AccountDAO();
		currencyConverter = new CurrencyConverter();
		jmsSender = new JMSSender();
		logger = new Logger();
 		
	}
	
	
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
		return "home";
	}
	
	public Collection<Account> getAllAccounts() {
		return  accountDAO.getAccounts();
	}
 
	public String depositPage(long accountNumber) {
		account = accountDAO.loadAccount(accountNumber);
		return "deposit";
	}
	
	
	public String deposit() {
		accountNumber = account.getAccountnumber();
		Account account = accountDAO.loadAccount(accountNumber);
		account.deposit(amount);
		accountDAO.updateAccount(account);
		logger.log("deposit with parameters accountNumber= " + accountNumber + " , amount= " + amount);
		if (amount > 10000) {
			jmsSender.sendJMSMessage("Deposit of $ " + amount + " to account with accountNumber= " + accountNumber);
		}
		return "deposit";
	}
	
	
	public String withdrawPage(long accountNumber) {
		account = accountDAO.loadAccount(accountNumber);
		return "withdraw";
	}
	
	public String withdraw() {
		accountNumber = account.getAccountnumber();
		Account account = accountDAO.loadAccount(accountNumber);
		account.withdraw(amount);
		accountDAO.updateAccount(account);
		return "withdraw";
	}
	//=========================================================================================================
	//=========================================================================================================
	//=========================================================================================================
	//=========================================================================================================
	

	






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
		System.out.println("1accountNumber:"+accountNumber);
		System.out.println("1accountNumber:"+amount);
		Account account = accountDAO.loadAccount(accountNumber);
		account.deposit(amount);
		accountDAO.updateAccount(account);
		logger.log("deposit with parameters accountNumber= " + accountNumber + " , amount= " + amount);
		if (amount > 10000) {
			jmsSender.sendJMSMessage("Deposit of $ " + amount + " to account with accountNumber= " + accountNumber);
		}
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
	
	
//===============================================================================================
//===============================================================================================
//===============================================================================================
//===============================================================================================
	
	
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


	public IAccountDAO getAccountDAO() {
		return accountDAO;
	}


	public void setAccountDAO(IAccountDAO accountDAO) {
		this.accountDAO = accountDAO;
	}
	public Account getAccount() {
		return account;
	}


	public void setAccount(Account account) {
		this.account = account;
	}
	

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}
	
	public void validate(FacesContext context, UIComponent component, Object obj) throws ValidatorException {
		long deposit = (Long) obj;

		if (deposit <= 0) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "the deposits are positive and greater than zero", "error！");
			throw new ValidatorException(message);
		}
	}
}
