package lithium.service.casino.provider.iforium.exception;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class GameRoundNotFoundException extends RuntimeException {

    private final BigDecimal balanceAmount;

    private final String currencyCode;

    public GameRoundNotFoundException(String message, BigDecimal balanceAmount, String currencyCode, Throwable e) {
        super(message, e);
        this.balanceAmount = balanceAmount;
        this.currencyCode = currencyCode;
    }
}
