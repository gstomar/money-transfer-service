package com.revolut.retail.ob.exception;

import com.revolut.retail.ob.common.Constants;

public final class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(long accountId) {
        super(Constants.INSUFFICIENT_BALANCE + accountId);
    }
}
