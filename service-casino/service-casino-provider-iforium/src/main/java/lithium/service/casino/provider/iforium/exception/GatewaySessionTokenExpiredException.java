package lithium.service.casino.provider.iforium.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class GatewaySessionTokenExpiredException extends RuntimeException {

    private final BigDecimal balanceAmount;

    private final String currencyCode;

    public GatewaySessionTokenExpiredException(String message, BigDecimal balanceAmount, String currencyCode) {
        super(message);
        this.balanceAmount = balanceAmount;
        this.currencyCode = currencyCode;
    }
}
