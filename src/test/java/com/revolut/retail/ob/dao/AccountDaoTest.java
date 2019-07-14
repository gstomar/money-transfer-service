package com.revolut.retail.ob.dao;

import com.revolut.retail.ob.exception.DuplicateAccountIdException;
import com.revolut.retail.ob.exception.InsufficientBalanceException;
import com.revolut.retail.ob.exception.NoSuchAccountException;
import com.revolut.retail.ob.model.UserAccount;
import org.junit.*;

import java.math.BigDecimal;

public class AccountDaoTest {

    private static AccountDao accountDao;

    @BeforeClass
    public static void setUp() {
        accountDao = AccountDaoImpl.getInstance();
    }

    @AfterClass
    public static void tearDown() {
        accountDao = null;
    }

    @Before
    public void cleanUp() {
        accountDao.deleteAccounts();
    }

    @Test
    public void testCreateAndGetAccount() throws DuplicateAccountIdException, NoSuchAccountException {
        long accountId = 1L;
        BigDecimal balance = new BigDecimal(100.00);
        String ccyCode = "GBP";
        UserAccount account = accountDao.createAccount(accountId, balance, ccyCode);
        Assert.assertEquals(account, accountDao.getAccount(accountId));
    }

    @Test
    public void testCreateAccount_DuplicateAccountIdException() throws DuplicateAccountIdException {
        long accountId = 2L;
        String ccyCode = "GBP";
        accountDao.createAccount(accountId, BigDecimal.valueOf(0.0), ccyCode);
        Assert.assertThrows(DuplicateAccountIdException.class, () -> accountDao.createAccount(accountId, BigDecimal.valueOf(0.0), ccyCode));
    }

    @Test
    public void testGetAccount_NoSuchAccountException() {
        long accountId = 3L;
        String ccyCode = "GBP";
        Assert.assertThrows(NoSuchAccountException.class, () -> accountDao.getAccount(accountId));
    }

    @Test
    public void testWithdraw() throws DuplicateAccountIdException, InsufficientBalanceException, NoSuchAccountException {
        long accountId = 4L;
        BigDecimal balance = BigDecimal.valueOf(100.0);
        BigDecimal withdrawnAmount = BigDecimal.valueOf(20.0);
        String ccyCode = "GBP";
        accountDao.createAccount(accountId, balance, ccyCode);
        accountDao.withdraw(accountId, withdrawnAmount);
        Assert.assertTrue((balance.subtract(withdrawnAmount)).compareTo(accountDao.getBalance(accountId)) == 0.0);
    }

    @Test
    public void testWithdraw_NoSuchAccountException() {
        long accountId = 5L;
        BigDecimal withdrawnAmount = BigDecimal.valueOf(100.0);
        Assert.assertThrows(NoSuchAccountException.class, () -> accountDao.withdraw(accountId, withdrawnAmount));
    }

    @Test
    public void testWithdraw_InsufficientBalanceException() throws DuplicateAccountIdException {
        long accountId = 6L;
        BigDecimal balance = BigDecimal.valueOf(10.00);
        BigDecimal withdrawnAmount = BigDecimal.valueOf(50.00);
        String ccyCode = "GBP";
        accountDao.createAccount(accountId, balance, ccyCode);
        Assert.assertThrows(InsufficientBalanceException.class, () -> accountDao.withdraw(accountId, withdrawnAmount));
    }

    @Test
    public void testDeposit() throws DuplicateAccountIdException, NoSuchAccountException {
        long accountId = 7L;
        BigDecimal balance = BigDecimal.valueOf(10.0);
        BigDecimal depositedAmount = BigDecimal.valueOf(20.0);
        String ccyCode = "GBP";
        accountDao.createAccount(accountId, balance, ccyCode);
        accountDao.deposit(accountId, depositedAmount);
        Assert.assertTrue(balance.add(depositedAmount).compareTo(accountDao.getBalance(accountId)) == 0.0);
    }

    @Test
    public void testDeposit_NoSuchAccountException() {
        long accountId = 8L;
        BigDecimal depositedAmount = BigDecimal.valueOf(10.00);
        Assert.assertThrows(NoSuchAccountException.class, () -> accountDao.deposit(accountId, depositedAmount));
    }

    @Test
    public void testGetBalance() throws DuplicateAccountIdException, NoSuchAccountException {
        long accountId = 9L;
        BigDecimal initialBalance = BigDecimal.valueOf(10.00);
        String ccyCode = "GBP";
        accountDao.createAccount(accountId, initialBalance, ccyCode);
        Assert.assertTrue(initialBalance.compareTo(accountDao.getBalance(accountId)) == 0.0);
    }

    @Test
    public void testGetBalance_NoSuchAccountException() {
        long accountId = 10L;
        Assert.assertThrows(NoSuchAccountException.class, () -> accountDao.getBalance(accountId));
    }

}
