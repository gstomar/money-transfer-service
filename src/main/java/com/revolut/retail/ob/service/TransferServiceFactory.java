package com.revolut.retail.ob.service;

import com.revolut.retail.ob.exception.InsufficientBalanceException;
import com.revolut.retail.ob.exception.NoSuchAccountException;
import com.revolut.retail.ob.model.AccountTransaction;
import com.revolut.retail.ob.model.UserAccount;
import com.revolut.retail.ob.utils.CurrencyUtils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public final class TransferServiceFactory implements TransferService {

    private final AccountService accountService = AccountServiceFactory.getAccountService();

    private final static TransferService transferService = new TransferServiceFactory();

    public static TransferService getTransferService() {
        return transferService;
    }

    /**
     * Transfer fund between two accounts
     */
    public void transferFunds(AccountTransaction accTransaction) throws NoSuchAccountException, InsufficientBalanceException {
        if (CurrencyUtils.INSTANCE.validateCcyCode(accTransaction.getCcyCode())) {
            UserAccount senderAccount = accountService.getAccount(accTransaction.getFromAccountId());
            UserAccount recipientAccount = accountService.getAccount(accTransaction.getToAccountId());

            Object innerLock, outerLock;
            //lets have the locks in correct order based on sorted account ids
            if (senderAccount.getAccountId() > recipientAccount.getAccountId()) {
                innerLock = senderAccount.getLock();
                outerLock = recipientAccount.getLock();
            } else {
                innerLock = recipientAccount.getLock();
                outerLock = senderAccount.getLock();
            }

            synchronized (outerLock) {
                synchronized (innerLock) {
                    accountService.withdraw(accTransaction.getFromAccountId(), accTransaction.getAmount());
                    try {
                        accountService.deposit(accTransaction.getToAccountId(), accTransaction.getAmount());
                    } catch (Exception e) {
                        accountService.deposit(accTransaction.getFromAccountId(), accTransaction.getAmount());
                        throw e;
                    }
                }
            }

        } else {
            throw new WebApplicationException("Currency Code Invalid ", Response.Status.BAD_REQUEST);
        }

    }

}
