package com.revolut.retail.ob.dao;

import com.revolut.retail.ob.exception.DuplicateAccountIdException;
import com.revolut.retail.ob.exception.InsufficientBalanceException;
import com.revolut.retail.ob.exception.NoSuchAccountException;
import com.revolut.retail.ob.model.UserAccount;
import com.revolut.retail.ob.utils.ParamUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;

public final class AccountDaoImpl implements AccountDao {

    private static Logger LOG = Logger.getLogger(AccountDaoImpl.class);

    //local cache to keep the user account data
    private static final ConcurrentHashMap<Long, UserAccount> accountCache = new ConcurrentHashMap<>();

    private static final AccountDaoImpl instance = new AccountDaoImpl();

    private static final Object newAccountLock = new Object();

    private AccountDaoImpl() {
    }

    public static AccountDaoImpl getInstance() {
        return instance;
    }

    @Override
    public UserAccount getAccount(long accountId) throws NoSuchAccountException {
        if (accountCache.containsKey(accountId)) {
            return accountCache.get(accountId);
        } else {
            throw new NoSuchAccountException(accountId);
        }
    }

    @Override
    public BigDecimal getBalance(long accountId) throws NoSuchAccountException {
        if (!accountCache.containsKey(accountId)) {
            throw new NoSuchAccountException(accountId);
        } else {
            return accountCache.get(accountId).getBalance();
        }
    }

    @Override
    public UserAccount createAccount(long accountId, BigDecimal balance, String ccyCode) throws DuplicateAccountIdException {
        if (accountCache.containsKey(accountId)) {
            throw new DuplicateAccountIdException(accountId);
        } else {
            synchronized (newAccountLock) {
                if (accountCache.containsKey(accountId)) {
                    throw new DuplicateAccountIdException(accountId);
                } else {
                    UserAccount account = new UserAccount(accountId, balance, ccyCode);
                    accountCache.put(accountId, account);
                    return account;
                }
            }
        }
    }

    @Override
    public void withdraw(long accountId, BigDecimal amount) throws NoSuchAccountException, InsufficientBalanceException {
        if (amount.compareTo(ParamUtils.zeroAmount) <= 0) {
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }
        BigDecimal delta = amount.negate();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Withdraw service: delta change to account  " + delta + " UserAccount ID = " + accountId);
        }
        if (!accountCache.containsKey(accountId)) {
            throw new NoSuchAccountException(accountId);
        } else if (amount.compareTo(accountCache.get(accountId).getBalance()) > 0) {
            throw new InsufficientBalanceException(accountId);
        } else {
            UserAccount account = accountCache.get(accountId);
            account.setBalance(account.getBalance().add(delta.setScale(4, RoundingMode.HALF_EVEN)));
            accountCache.put(accountId, account);
        }
    }

    @Override
    public void deposit(long accountId, BigDecimal amount) throws NoSuchAccountException {
        if (amount.compareTo(ParamUtils.zeroAmount) <= 0) {
            throw new WebApplicationException("Invalid Deposit amount", Response.Status.BAD_REQUEST);
        }
        if (!accountCache.containsKey(accountId)) {
            throw new NoSuchAccountException(accountId);
        } else {
            UserAccount account = accountCache.get(accountId);
            account.setBalance(account.getBalance().add(amount));
            accountCache.put(accountId, account);
        }
    }

    @Override
    public void deleteAccounts() {
        accountCache.clear();
    }

}
