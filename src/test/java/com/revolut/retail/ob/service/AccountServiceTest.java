package com.revolut.retail.ob.service;

import com.revolut.retail.ob.exception.DuplicateAccountIdException;
import com.revolut.retail.ob.exception.InsufficientBalanceException;
import com.revolut.retail.ob.exception.NoSuchAccountException;
import com.revolut.retail.ob.model.UserAccount;
import org.junit.*;

import java.math.BigDecimal;


public class AccountServiceTest {

    private static AccountService accountService;

    @BeforeClass
    public static void setUp() {
        accountService = AccountServiceFactory.getAccountService();
    }

    @AfterClass
    public static void tearDown() {
        accountService = null;
    }

    @Before
    public void cleanUp() {
        accountService.deleteAccounts();
    }

    @Test
    public void testCreateAndGetAccount() throws DuplicateAccountIdException, NoSuchAccountException {
        long accountId = 1L;
        BigDecimal initialBalance = new BigDecimal(100.00);
        UserAccount account = accountService.createAccount(accountId, initialBalance, "GBP");
        Assert.assertEquals(account, accountService.getAccount(accountId));
    }

    @Test
    public void testCreateAccount_DuplicateAccountIdException() throws DuplicateAccountIdException {
        long accountId = 2L;
        BigDecimal initialBalance = new BigDecimal(100.00);
        accountService.createAccount(accountId, initialBalance, "GBP");
        Assert.assertThrows(DuplicateAccountIdException.class, () -> accountService.createAccount(accountId, initialBalance, "GBP"));
    }

    @Test
    public void testGetAccount_NoSuchAccountException() {
        long accountId = 10L;
        Assert.assertThrows(NoSuchAccountException.class, () -> accountService.getAccount(accountId));
    }

    @Test
    public void testGetBalance() throws NoSuchAccountException, DuplicateAccountIdException {
        long accountId = 11L;
        BigDecimal initialBalance = new BigDecimal(100.00);
        accountService.createAccount(accountId, initialBalance, "GBP");
        Assert.assertTrue(initialBalance.compareTo(accountService.getBalance(accountId)) == 0);
    }

    @Test
    public void testGetBalance_NoSuchAccountException() {
        Assert.assertThrows(NoSuchAccountException.class, () -> accountService.getBalance(1000L));
    }

    @Test
    public void testWithdraw() throws DuplicateAccountIdException, InsufficientBalanceException, NoSuchAccountException {
        long accountId = 12L;
        BigDecimal initialBalance = new BigDecimal(100.00);
        BigDecimal withdrawnAmount = new BigDecimal(20.00);

        accountService.createAccount(accountId, initialBalance, "GBP");
        accountService.withdraw(accountId, withdrawnAmount);
        Assert.assertTrue((initialBalance.subtract(withdrawnAmount)).compareTo(accountService.getBalance(accountId)) == 0);
    }

    @Test
    public void testWithdraw_NoSuchAccountException() {
        Assert.assertThrows(NoSuchAccountException.class, () -> accountService.withdraw(1001L, new BigDecimal(1000D)));
    }

    @Test
    public void testWithdraw_InsufficientBalanceException() throws DuplicateAccountIdException {
        long accountId = 13L;
        BigDecimal initialBalance = new BigDecimal(100.00);
        BigDecimal withdrawnAmount = new BigDecimal(2000.00);
        accountService.createAccount(accountId, initialBalance, "GBP");
        Assert.assertThrows(InsufficientBalanceException.class, () -> accountService.withdraw(accountId, withdrawnAmount));
    }

    @Test
    public void testDeposit() throws DuplicateAccountIdException, NoSuchAccountException {
        long accountId = 14L;
        BigDecimal initialBalance = new BigDecimal(100.00);
        BigDecimal depositedAmount = new BigDecimal(200.00);

        accountService.createAccount(accountId, initialBalance, "GBP");
        accountService.deposit(accountId, depositedAmount);
        Assert.assertTrue(initialBalance.add(depositedAmount).compareTo(accountService.getBalance(accountId)) == 0);
    }

    @Test
    public void testDeposit_NoSuchAccountException() {
        Assert.assertThrows(NoSuchAccountException.class, () -> accountService.deposit(1002L, new BigDecimal(1000.00)));
    }

}
