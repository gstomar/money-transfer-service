package com.revolut.retail.ob.utils;

import org.apache.log4j.Logger;

import java.util.Currency;

/**
 * Utilities class to operate on money
 */
public enum CurrencyUtils {

    INSTANCE;

    static Logger log = Logger.getLogger(CurrencyUtils.class);

    /**
     * @param inputCcyCode String Currency code to be validated
     * @return true if currency code is valid ISO code, false otherwise
     */
    public boolean validateCcyCode(String inputCcyCode) {
        try {
            Currency instance = Currency.getInstance(inputCcyCode);
            if (log.isDebugEnabled()) {
                log.debug("Validate Currency Code: " + instance.getSymbol());
            }
            return instance.getCurrencyCode().equals(inputCcyCode);
        } catch (Exception e) {
            log.warn("Cannot parse the input Currency Code, Validation Failed: ", e);
        }
        return false;
    }

}
