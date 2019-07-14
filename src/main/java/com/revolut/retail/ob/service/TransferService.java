package com.revolut.retail.ob.service;

import com.revolut.retail.ob.exception.InsufficientBalanceException;
import com.revolut.retail.ob.exception.NoSuchAccountException;
import com.revolut.retail.ob.model.AccountTransaction;

public interface TransferService {

    void transferFunds(AccountTransaction transaction) throws NoSuchAccountException, InsufficientBalanceException;

}
