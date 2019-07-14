package com.revolut.retail.ob.dao;

import com.revolut.retail.ob.exception.DuplicateAccountIdException;
import com.revolut.retail.ob.exception.InsufficientBalanceException;
import com.revolut.retail.ob.exception.NoSuchAccountException;
import com.revolut.retail.ob.model.UserAccount;

import java.math.BigDecimal;

public interface AccountDao {

    /**
     * Returns account for the given ID
     *
     * @param accountId Requested account's ID
     * @return Requested account
     * @throws NoSuchAccountException Thrown when no such account exists
     */
    UserAccount getAccount(long accountId) throws NoSuchAccountException;

    /**
     * Returns current balance of given account id
     *
     * @param accountId Requested account
     * @return Balance of that account
     * @throws NoSuchAccountException Thrown when no such account exists
     */
    BigDecimal getBalance(long accountId) throws NoSuchAccountException;

    /**
     * Creates an account with the given account id and the given initial balance
     *
     * @param accountId
     * @param balance
     * @param ccyCode
     * @return user account
     * @throws DuplicateAccountIdException
     */
    UserAccount createAccount(long accountId, BigDecimal balance, String ccyCode) throws DuplicateAccountIdException;

    /**
     * Withdraws money from given account
     *
     * @param accountId Account which will be withdrawn from
     * @param amount    Amount of money to be withrdawn
     * @throws NoSuchAccountException       Thrown when no such account exists
     * @throws InsufficientBalanceException Thrown when there isn't enough money to withdraw
     */
    void withdraw(long accountId, BigDecimal amount) throws NoSuchAccountException, InsufficientBalanceException;

    /**
     * Deposits money to the given account
     *
     * @param accountId Account which will be deposited to
     * @param amount    Amount of money to be deposited
     * @throws NoSuchAccountException Thrown when no such account exists
     */
    void deposit(long accountId, BigDecimal amount) throws NoSuchAccountException;


    /**
     * Method to clean all the accounts data
     */
    void deleteAccounts();

}
