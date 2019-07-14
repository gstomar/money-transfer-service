package com.revolut.retail.ob.exception;

import com.revolut.retail.ob.common.Constants;

public final class NoSuchAccountException extends Exception {
    public NoSuchAccountException(long accountId) {
        super(Constants.NO_SUCH_ACCOUNT + accountId);
    }
}
