package lithium.service.casino.provider.iforium.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SessionLengthLimitReachedException extends RuntimeException {

    private final BigDecimal balanceAmount;

    private final String currencyCode;

    public SessionLengthLimitReachedException(Throwable e, String message, BigDecimal balanceAmount, String currencyCode) {
        super(message, e);
        this.balanceAmount = balanceAmount;
        this.currencyCode = currencyCode;
    }
}
