package lithium.service.casino.provider.iforium.model.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCodes {

    SUCCESS(0),
    UNKNOWN_ERROR(-1),
    INSUFFICIENT_FUNDS(-2),
    SESSION_NOT_FOUND(-3),
    ACCOUNT_NOT_FOUND(-5),
    API_AUTHENTICATION_FAILED(-6),
    TRANSACTION_NOT_FOUND(-8),
    CURRENCY_MISMATCH(-9),
    LOSS_LIMIT(-12),
    SESSION_LIMIT(-13),
    GAME_ROUND_NOT_FOUND(-14),
    GAME_NOT_FOUND(-15);

    private final Integer code;
}
