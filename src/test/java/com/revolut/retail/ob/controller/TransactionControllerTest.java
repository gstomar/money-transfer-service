package com.revolut.retail.ob.controller;

import com.revolut.retail.ob.common.Constants;
import com.revolut.retail.ob.exception.DuplicateAccountIdException;
import com.revolut.retail.ob.exception.NoSuchAccountException;
import com.revolut.retail.ob.service.AccountService;
import com.revolut.retail.ob.service.AccountServiceFactory;
import com.revolut.retail.ob.service.TransferService;
import com.revolut.retail.ob.service.TransferServiceFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.IntStream.rangeClosed;

public class TransactionControllerTest {

    private static TransferService transferService;
    private static AccountService accountService;

    @ClassRule
    public static final TestServer TEST_SERVER = new TestServer();

    @BeforeClass
    public static void setUp() throws Exception {
        transferService = TransferServiceFactory.getTransferService();
        accountService = AccountServiceFactory.getAccountService();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        transferService = null;
    }

    @Before
    public void cleanUp() {
        accountService.deleteAccounts();
    }

    @Test
    public void testTransferFundsWithMultipleClients() throws IOException, DuplicateAccountIdException, NoSuchAccountException, InterruptedException {
        long accountId1 = 39L;
        long accountId2 = 38L;
        Double transferAmount = 50d;

        BigDecimal senderBalance = BigDecimal.valueOf(1000D).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal toBalance = BigDecimal.valueOf(1000D).setScale(4, RoundingMode.HALF_EVEN);
        String ccyCode = "GBP";

        accountService.createAccount(accountId1, senderBalance, ccyCode);
        accountService.createAccount(accountId2, toBalance, ccyCode);

        // Given
        int transactionsNum = 20;

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
                        HttpUriRequest request = new HttpPost(Constants.TRANSACTION_OPERATIONS_PATH +
                            "?from=" + accountId1 +
                            "&to=" + accountId2 +
                            "&amount=" + transferAmount.toString() +
                            "&ccyCode=" + ccyCode);
                        HttpResponse response = HttpClientBuilder.create().build().execute(request);
                        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
                    } catch (Exception e) {
                        Assert.fail(e.toString());
                    }
                    transactionsCompleted.countDown();
                })
            );
            startTransaction.countDown();

            // Then
            transactionsCompleted.await();
            // Asserts
            Assert.assertEquals(BigDecimal.valueOf(0d).setScale(4, RoundingMode.HALF_EVEN), accountService.getBalance(accountId1));
            Assert.assertEquals(BigDecimal.valueOf(2000d).setScale(4, RoundingMode.HALF_EVEN), accountService.getBalance(accountId2));
        } finally {
            executorService.shutdown();
        }
    }

    @Test
    public void testTransferFunds() throws IOException, DuplicateAccountIdException, NoSuchAccountException {
        long accountId1 = 19L;
        long accountId2 = 28L;
        Double transferAmount = 500D;

        BigDecimal senderBalance = BigDecimal.valueOf(1000D).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal toBalance = BigDecimal.valueOf(1000D).setScale(4, RoundingMode.HALF_EVEN);
        String ccyCode = "GBP";

        accountService.createAccount(accountId1, senderBalance, ccyCode);
        accountService.createAccount(accountId2, toBalance, ccyCode);

        HttpUriRequest request = new HttpPost(Constants.TRANSACTION_OPERATIONS_PATH +
            "?from=" + accountId1 +
            "&to=" + accountId2 +
            "&amount=" + transferAmount.toString() +
            "&ccyCode=" + ccyCode);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        Assert.assertTrue(senderBalance.subtract(BigDecimal.valueOf(transferAmount)).compareTo(accountService.getBalance(accountId1)) == 0.0);
        Assert.assertTrue(toBalance.add(BigDecimal.valueOf(transferAmount)).compareTo(accountService.getBalance(accountId2)) == 0.0);
    }

    @Test
    public void testTransferFunds_InSufficientBalance() throws DuplicateAccountIdException, IOException, InterruptedException {
        long accountId1 = 21L;
        long accountId2 = 22L;
        Double transferAmount = 2000D;

        BigDecimal senderBalance = BigDecimal.valueOf(1000D).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal toBalance = BigDecimal.valueOf(1000D).setScale(4, RoundingMode.HALF_EVEN);
        String ccyCode = "GBP";

        accountService.createAccount(accountId1, senderBalance, ccyCode);
        accountService.createAccount(accountId2, toBalance, ccyCode);

        HttpUriRequest request = new HttpPost(Constants.TRANSACTION_OPERATIONS_PATH +
            "?from=" + accountId1 +
            "&to=" + accountId2 +
            "&amount=" + transferAmount.toString() +
            "&ccyCode=" + ccyCode);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
        Assert.assertEquals(Constants.INSUFFICIENT_BALANCE + accountId1, EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testTransferFunds_InvalidTransferAmount() throws DuplicateAccountIdException, IOException {
        long accountId1 = 23L;
        long accountId2 = 24L;
        Double transferAmount = -100D;

        BigDecimal senderBalance = BigDecimal.valueOf(1000D).setScale(4, RoundingMode.HALF_EVEN);
        BigDecimal toBalance = BigDecimal.valueOf(1000D).setScale(4, RoundingMode.HALF_EVEN);
        String ccyCode = "GBP";

        accountService.createAccount(accountId1, senderBalance, ccyCode);
        accountService.createAccount(accountId2, toBalance, ccyCode);

        HttpUriRequest request = new HttpPost(Constants.TRANSACTION_OPERATIONS_PATH +
            "?from=" + accountId1 +
            "&to=" + accountId2 +
            "&amount=" + transferAmount.toString() +
            "&ccyCode=" + ccyCode);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
        Assert.assertEquals(Constants.NEGATIVE_TRANSFER_AMOUNT, EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testTransferFunds_NoSuchAccount() throws DuplicateAccountIdException, IOException {
        long accountId1 = 25L;
        long accountId2 = 26L;
        Double transferAmount = -100D;

        BigDecimal initialAmount1 = BigDecimal.valueOf(1000D).setScale(4, RoundingMode.HALF_EVEN);
        String ccyCode = "GBP";

        accountService.createAccount(accountId1, initialAmount1, ccyCode);

        HttpUriRequest request = new HttpPost(Constants.TRANSACTION_OPERATIONS_PATH +
            "?from=" + accountId1 +
            "&to=" + accountId2 +
            "&amount=" + transferAmount.toString() +
            "&ccyCode=" + ccyCode);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
        Assert.assertEquals(Constants.NEGATIVE_TRANSFER_AMOUNT, EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void testTransferFunds_InvalidID() throws DuplicateAccountIdException, IOException {
        long accountId1 = 27L;
        long accountId2 = -10L;
        Double transferAmount = -100D;

        BigDecimal initialAmount1 = BigDecimal.valueOf(1000D).setScale(4, RoundingMode.HALF_EVEN);
        String ccyCode = "GBP";

        accountService.createAccount(accountId1, initialAmount1, ccyCode);

        HttpUriRequest request = new HttpPost(Constants.TRANSACTION_OPERATIONS_PATH +
            "?from=" + accountId1 +
            "&to=" + accountId2 +
            "&amount=" + transferAmount.toString() +
            "&ccyCode=" + ccyCode);
        HttpResponse response = HttpClientBuilder.create().build().execute(request);
        Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusLine().getStatusCode());
        Assert.assertEquals(Constants.INVALID_ID + accountId2, EntityUtils.toString(response.getEntity()));
    }


}
