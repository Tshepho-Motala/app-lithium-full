package lithium.service.casino.provider.iforium.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class TransactionNotFoundException extends RuntimeException {

    private BigDecimal balanceAmount;

    private String currencyCode;

    public TransactionNotFoundException() {
    }

    public TransactionNotFoundException(String message, Throwable e, BigDecimal balanceAmount, String currencyCode) {
        super(message, e);
        this.balanceAmount = balanceAmount;
        this.currencyCode = currencyCode;
    }
}
