package com.revolut.retail.ob.controller;

import com.revolut.retail.ob.exception.InsufficientBalanceException;
import com.revolut.retail.ob.exception.NoSuchAccountException;
import com.revolut.retail.ob.model.AccountTransaction;
import com.revolut.retail.ob.service.TransferService;
import com.revolut.retail.ob.service.TransferServiceFactory;
import com.revolut.retail.ob.utils.ParamUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

@Path("/command/api/transfer")
@Produces(MediaType.APPLICATION_JSON)
public final class TransactionController {

    private final TransferService transferService = TransferServiceFactory.getTransferService();

    /**
     * Transfer fund between two accounts
     */
    @POST
    public Response transferFunds(@QueryParam("from") String senderId,
                                  @QueryParam("to") String recipientId,
                                  @QueryParam("amount") String amount,
                                  @QueryParam("ccyCode") String ccyCode) {
        try {
            long senderNumAccId = ParamUtils.validateId(senderId);
            long recipientNumAccId = ParamUtils.validateId(recipientId);
            BigDecimal numAmount = ParamUtils.validateTransferAmount(amount);
            AccountTransaction transaction = new AccountTransaction(ccyCode, numAmount, senderNumAccId, recipientNumAccId);
            transferService.transferFunds(transaction);
            return Response.ok().build();
        } catch (BadRequestException | NoSuchAccountException | InsufficientBalanceException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }

}
