package com.revolut.retail.ob.service;

import com.revolut.retail.ob.exception.DuplicateAccountIdException;
import com.revolut.retail.ob.exception.InsufficientBalanceException;
import com.revolut.retail.ob.exception.NoSuchAccountException;
import com.revolut.retail.ob.model.AccountTransaction;
import com.revolut.retail.ob.model.UserAccount;
import org.junit.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.IntStream.rangeClosed;

public class TransferServiceTest {

    private static TransferService transferService;
    private static AccountService accountService;

    @BeforeClass
    public static void setUp() {
        transferService = TransferServiceFactory.getTransferService();
        accountService = AccountServiceFactory.getAccountService();
    }

    @AfterClass
    public static void tearDown() {
        transferService = null;
        accountService = null;
    }

    @Before
    public void cleanUp() {
        accountService.deleteAccounts();
    }

    @Test(timeout = 5000)
    public void transferFundsMultiThreaded_OK() throws Exception {
        // Given
        int transactionsNum = 10;

        long senderAccountId = 10000L;
        long recipientAccountId = 10001L;
        BigDecimal senderBalance = BigDecimal.valueOf(200.00).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal toBalance = BigDecimal.valueOf(10.00).setScale(4, RoundingMode.HALF_EVEN);
        String ccyCode = "GBP";

        UserAccount fromAccount = accountService.createAccount(senderAccountId, senderBalance, ccyCode);
        UserAccount toAccount = accountService.createAccount(recipientAccountId, toBalance, ccyCode);

        CountDownLatch startTransaction = new CountDownLatch(1);
        CountDownLatch transactionsCompleted = new CountDownLatch(transactionsNum);
        ExecutorService executorService = Executors.newFixedThreadPool(transactionsNum);

        try {
            // When
            rangeClosed(1, transactionsNum).forEach(value ->
                executorService.execute(() -> {
                    try {
                        startTransaction.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    try {
                        AccountTransaction transaction = new AccountTransaction(ccyCode, BigDecimal.valueOf(10.0), senderAccountId, recipientAccountId);
                        transferService.transferFunds(transaction);
                    } catch (Exception e) {
                        Assert.fail(e.toString());
                    }
                    transactionsCompleted.countDown();
                })
            );
            startTransaction.countDown();

            // Then
            transactionsCompleted.await();
            Assert.assertEquals(BigDecimal.valueOf(100d).setScale(4, RoundingMode.HALF_EVEN), accountService.getBalance(fromAccount.getAccountId()));
            Assert.assertEquals(BigDecimal.valueOf(110d).setScale(4, RoundingMode.HALF_EVEN), accountService.getBalance(toAccount.getAccountId()));
        } finally {
            executorService.shutdown();
        }
    }

    @Test
    public void testTransferFunds() throws InsufficientBalanceException, NoSuchAccountException, DuplicateAccountIdException {
        long senderAccountId = 1000L;
        long recipientAccountId = 1001L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000D);
        BigDecimal transactionAmount = BigDecimal.valueOf(500D);
        String ccyCode = "GBP";

        accountService.createAccount(senderAccountId, initialBalance, ccyCode);
        accountService.createAccount(recipientAccountId, initialBalance, ccyCode);

        AccountTransaction transaction = new AccountTransaction(ccyCode, transactionAmount, senderAccountId, recipientAccountId);

        transferService.transferFunds(transaction);

        Assert.assertTrue((initialBalance.subtract(transactionAmount)).compareTo(accountService.getBalance(senderAccountId)) == 0);
        Assert.assertTrue(initialBalance.add(transactionAmount).compareTo(accountService.getBalance(recipientAccountId)) == 0);
    }

    @Test
    public void testTransferFunds_NoSuchAccountException_1() throws DuplicateAccountIdException {
        long senderAccountId = 1002L;
        long recipientAccountId = 1003L;
        String ccyCode = "GBP";

        accountService.createAccount(senderAccountId, BigDecimal.valueOf(1000D), ccyCode);

        AccountTransaction transaction = new AccountTransaction(ccyCode, BigDecimal.valueOf(500D), senderAccountId, recipientAccountId);
        Assert.assertThrows(NoSuchAccountException.class, () -> transferService.transferFunds(transaction));
    }

    @Test
    public void testTransferFunds_NoSuchAccountException_2() throws DuplicateAccountIdException {
        long senderAccountId = 1004L;
        long recipientAccountId = 1005L;
        String ccyCode = "GBP";

        accountService.createAccount(recipientAccountId, BigDecimal.valueOf(500D), ccyCode);

        AccountTransaction transaction = new AccountTransaction(ccyCode, BigDecimal.valueOf(500D), senderAccountId, recipientAccountId);
        Assert.assertThrows(NoSuchAccountException.class, () -> transferService.transferFunds(transaction));
    }

    @Test
    public void testTransferFunds_InsufficientBalanceException() throws DuplicateAccountIdException, NoSuchAccountException {
        long senderAccountId = 1007L;
        long recipientAccountId = 1006L;
        BigDecimal initialBalance = BigDecimal.valueOf(1000D);
        BigDecimal transactionAmount = BigDecimal.valueOf(2000D);
        String ccyCode = "GBP";

        UserAccount fromAccount = accountService.createAccount(senderAccountId, initialBalance, ccyCode);
        UserAccount toAccount = accountService.createAccount(recipientAccountId, initialBalance, ccyCode);
        AccountTransaction transaction = new AccountTransaction(ccyCode, transactionAmount, senderAccountId, recipientAccountId);
        Assert.assertThrows(InsufficientBalanceException.class, () -> transferService.transferFunds(transaction));
    }

}
