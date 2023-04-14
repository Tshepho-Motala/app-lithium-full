package lithium.service.casino.provider.iforium.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class InvalidGameException extends RuntimeException {

    private final BigDecimal balanceAmount;

    private final String currencyCode;

    public InvalidGameException(String message, BigDecimal balanceAmount, String currencyCode) {
        super(message);
        this.balanceAmount = balanceAmount;
        this.currencyCode = currencyCode;
    }
}
