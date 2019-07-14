package com.revolut.retail.ob.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Objects;

public final class UserAccount {

    private final long accountId;

    private BigDecimal balance;

    private final String ccyCode;

    private final Object lock = new Object();

    public UserAccount(long accountId, BigDecimal balance, String ccyCode) {
        this.accountId = accountId;
        this.balance = balance;
        this.ccyCode = ccyCode;
    }

    public long getAccountId() {
        return accountId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCcyCode() {
        return ccyCode;
    }

    public Object getLock() {
        return lock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount)) return false;
        UserAccount that = (UserAccount) o;
        return getAccountId() == that.getAccountId() &&
            Objects.equals(getBalance(), that.getBalance()) &&
            Objects.equals(getCcyCode(), that.getCcyCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccountId(), getBalance(), getCcyCode());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("accountId", accountId)
            .append("balance", balance)
            .append("ccyCode", ccyCode)
            .toString();
    }
}
