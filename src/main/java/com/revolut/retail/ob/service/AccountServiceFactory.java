package com.revolut.retail.ob.service;

import com.revolut.retail.ob.dao.AccountDao;
import com.revolut.retail.ob.dao.AccountDaoImpl;
import com.revolut.retail.ob.exception.DuplicateAccountIdException;
import com.revolut.retail.ob.exception.InsufficientBalanceException;
import com.revolut.retail.ob.exception.NoSuchAccountException;
import com.revolut.retail.ob.model.UserAccount;
import org.apache.log4j.Logger;

import java.math.BigDecimal;


public final class AccountServiceFactory implements AccountService {

    private static Logger LOG = Logger.getLogger(AccountServiceFactory.class);

    private final static AccountService accountService = new AccountServiceFactory();

    private AccountDao accountDao = AccountDaoImpl.getInstance();

    public static AccountService getAccountService() {
        return accountService;
    }

    @Override
    public UserAccount getAccount(long accountId) throws NoSuchAccountException {
        return accountDao.getAccount(accountId);
    }

    @Override
    public BigDecimal getBalance(long accountId) throws NoSuchAccountException {
        return accountDao.getBalance(accountId);
    }

    @Override
    public UserAccount createAccount(long accountId, BigDecimal balance, String ccyCode) throws DuplicateAccountIdException {
        return accountDao.createAccount(accountId, balance, ccyCode);
    }

    @Override
    public void deposit(long accountId, BigDecimal amount) throws NoSuchAccountException {
        accountDao.deposit(accountId, amount);
    }

    @Override
    public void withdraw(long accountId, BigDecimal amount) throws NoSuchAccountException, InsufficientBalanceException {
        accountDao.withdraw(accountId, amount);
    }

    public void deleteAccounts() {
        accountDao.deleteAccounts();
    }

}
