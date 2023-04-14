package lithium.service.casino.provider.iforium.util;

import lithium.service.casino.provider.iforium.model.request.CreateSessionTokenRequest;
import lithium.service.casino.provider.iforium.model.request.RedeemSessionTokenRequest;
import lithium.service.casino.provider.iforium.model.response.ErrorCodes;
import lithium.service.casino.provider.iforium.model.response.RedeemSessionTokenResponse;
import lithium.service.casino.provider.iforium.model.response.Result;
import lithium.service.casino.provider.iforium.model.response.SessionTokenResponse;
import lombok.experimental.UtilityClass;

import java.util.Date;

import static lithium.service.casino.provider.iforium.constant.TestConstants.COUNTRY_CODE;
import static lithium.service.casino.provider.iforium.constant.TestConstants.GBP_CURRENCY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.OPERATOR_ACCOUNT_ID;
import static lithium.service.casino.provider.iforium.constant.TestConstants.PLATFORM_KEY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SEQUENCE;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_KEY;
import static lithium.service.casino.provider.iforium.constant.TestConstants.SESSION_TOKEN_LENGTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@UtilityClass
public final class TestSessionUtils {

    public static String getValidSessionToken() {
        return SESSION_KEY + "-" + Long.toHexString(System.currentTimeMillis());
    }

    public static String getExpiredSessionToken() {
        return SESSION_KEY + "-" + Long.toHexString(1);
    }

    public static RedeemSessionTokenRequest validRedeemSessionTokenRequest() {
        return RedeemSessionTokenRequest.builder()
                                        .platformKey(PLATFORM_KEY)
                                        .sequence(SEQUENCE)
                                        .timestamp(new Date())
                                        .sessionToken(getValidSessionToken())
                                        .build();
    }

    public static RedeemSessionTokenResponse getSuccessRedeemSessionTokenResponse() {
        return new RedeemSessionTokenResponse(
                ErrorCodes.SUCCESS.getCode(),
                Result.builder()
                      .operatorAccountId(OPERATOR_ACCOUNT_ID)
                      .countryCode(COUNTRY_CODE)
                      .gatewaySessionToken(SESSION_KEY)
                      .currencyCode(GBP_CURRENCY)
                      .build()
        );
    }

    public static void basicValidationForSessionTokenResponse(SessionTokenResponse sessionTokenResponse) {
        assertEquals(ErrorCodes.SUCCESS.getCode(), sessionTokenResponse.getErrorCode());
        assertFalse(sessionTokenResponse.getResult().getSessionToken().isEmpty());
        assertNotNull(sessionTokenResponse.getResult().getSessionToken());
        assertEquals(SESSION_TOKEN_LENGTH, sessionTokenResponse.getResult().getSessionToken().length());
    }

    public static CreateSessionTokenRequest validCreateSessionTokenRequest() {
        return CreateSessionTokenRequest.builder()
                                        .platformKey(PLATFORM_KEY)
                                        .sequence(SEQUENCE)
                                        .timestamp(new Date())
                                        .gameId(getValidSessionToken())
                                        .operatorAccountId(OPERATOR_ACCOUNT_ID)
                                        .build();
    }
}
