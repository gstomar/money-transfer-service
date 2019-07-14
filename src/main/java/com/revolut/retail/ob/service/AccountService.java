package com.revolut.retail.ob.service;

import com.revolut.retail.ob.exception.DuplicateAccountIdException;
import com.revolut.retail.ob.exception.InsufficientBalanceException;
import com.revolut.retail.ob.exception.NoSuchAccountException;
import com.revolut.retail.ob.model.UserAccount;

import java.math.BigDecimal;

/**
 * UserAccount Service intf
 */

public interface AccountService {

    /**
     * Returns account info for the given account ID
     *
     * @param accountId
     * @return Account
     * @throws NoSuchAccountException
     */
    UserAccount getAccount(long accountId) throws NoSuchAccountException;

    /**
     * Returns the balance info for the given account ID
     *
     * @param accountId
     * @return Balance
     * @throws NoSuchAccountException
     */
    BigDecimal getBalance(long accountId) throws NoSuchAccountException;

    ;

    /**
     * Create the user account
     *
     * @param accountId
     * @param balance
     * @param ccyCode
     * @return user account
     * @throws DuplicateAccountIdException
     */
    UserAccount createAccount(long accountId, BigDecimal balance, String ccyCode) throws DuplicateAccountIdException;

    /**
     * Deposit the money into given account id
     *
     * @param accountId
     * @param amount
     * @return void
     * @throws NoSuchAccountException
     */
    void deposit(long accountId, BigDecimal amount) throws NoSuchAccountException;

    /**
     * Withdraw the money from given account id
     *
     * @param accountId
     * @param amount
     * @return void
     * @throws NoSuchAccountException
     * @throws InsufficientBalanceException
     */
    void withdraw(long accountId, BigDecimal amount) throws NoSuchAccountException, InsufficientBalanceException;

    /**
     * Method to clean all the accounts data
     */
    void deleteAccounts();

}
