package com.revolut.retail.ob.exception;


import com.revolut.retail.ob.common.Constants;

public final class DuplicateAccountIdException extends Exception {
    public DuplicateAccountIdException(long accountId) {
        super(Constants.ACCOUNT_ID_ALREADY_EXISTS + accountId);
    }
}
