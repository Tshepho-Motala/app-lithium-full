package lithium.service.casino.provider.iforium.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InsufficientFundsException extends RuntimeException {

    private BigDecimal balanceAmount;

    private String currencyCode;

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(String message, BigDecimal balanceAmount, String currencyCode) {
        super(message);
        this.balanceAmount = balanceAmount;
        this.currencyCode = currencyCode;
    }
}
