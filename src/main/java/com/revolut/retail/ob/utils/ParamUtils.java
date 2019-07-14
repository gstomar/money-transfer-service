package com.revolut.retail.ob.utils;

import com.revolut.retail.ob.common.Constants;

import javax.ws.rs.BadRequestException;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utilities class to operate on input parameters
 */
public final class ParamUtils {

    private ParamUtils() {
        throw new IllegalArgumentException("Not meant for instantiation !!!");
    }

    //zero amount with scale 4 and financial rounding mode
    public static final BigDecimal zeroAmount = new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN);

    public static long validateId(String id) throws BadRequestException {
        long parsedId;
        if (id == null || id.isEmpty()) {
            throw new BadRequestException(Constants.INVALID_ID + Constants.NULL_OR_EMPTY_ID);
        } else {
            try {
                parsedId = Long.parseLong(id);
            } catch (Exception e) {
                throw new BadRequestException(Constants.INVALID_ID + id);
            }
        }
        if (parsedId < 0) {
            throw new BadRequestException(Constants.INVALID_ID + id);
        }
        return parsedId;
    }

    public static BigDecimal validateInitialAmount(String initialAmount) throws BadRequestException {
        BigDecimal parsedInitialAmount;
        if (initialAmount == null || initialAmount.isEmpty()) {
            return new BigDecimal(0.0);
        } else {
            try {
                parsedInitialAmount = new BigDecimal(initialAmount).setScale(4, RoundingMode.HALF_EVEN);
            } catch (Exception e) {
                throw new BadRequestException(Constants.INVALID_INITIAL_BALANCE + initialAmount);
            }
        }
        if (parsedInitialAmount.doubleValue() < 0) {
            throw new BadRequestException(Constants.NEGATIVE_INITIAL_BALANCE);
        }
        return parsedInitialAmount;
    }

    public static BigDecimal validateTransferAmount(String transferAmount) throws BadRequestException {
        BigDecimal parsedAmount;

        if (transferAmount == null || transferAmount.isEmpty()) {
            throw new BadRequestException(Constants.INVALID_TRANSFER_AMOUNT + transferAmount);
        } else {
            try {
                parsedAmount = new BigDecimal(transferAmount).setScale(4, RoundingMode.HALF_EVEN);
            } catch (Exception e) {
                throw new BadRequestException(Constants.INVALID_TRANSFER_AMOUNT + transferAmount);
            }
        }
        if (parsedAmount.doubleValue() < 0) {
            throw new BadRequestException(Constants.NEGATIVE_TRANSFER_AMOUNT);
        }
        return parsedAmount;
    }
}
