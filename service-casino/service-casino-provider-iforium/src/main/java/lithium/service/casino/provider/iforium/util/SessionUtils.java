package lithium.service.casino.provider.iforium.util;

import lithium.service.casino.provider.iforium.model.SessionTokenInfo;
import lithium.service.casino.provider.iforium.model.response.ErrorCodes;
import lithium.service.casino.provider.iforium.model.response.SessionTokenResponse;
import lithium.service.casino.provider.iforium.model.response.SessionTokenResult;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class SessionUtils {

    private static final int HEXADECIMAL_RADIX = 16;

    public static String wrapSessionToken(String sessionKey) {
        return sessionKey + "-" + currentTimeMillisInHex();
    }

    private static String currentTimeMillisInHex() {
        return Long.toHexString(System.currentTimeMillis());
    }

    public static SessionTokenInfo decomposeSessionToken(String sessionToken) {
        int lastDashIndex = sessionToken.lastIndexOf('-');

        String timestamp = sessionToken.substring(lastDashIndex + 1);
        long issuedTimeMillis = Long.parseLong(timestamp, HEXADECIMAL_RADIX);

        String sessionKey = sessionToken.substring(0, lastDashIndex);

        return new SessionTokenInfo(sessionKey, issuedTimeMillis);
    }

    public static SessionTokenResponse buildSessionTokenResponse(String sessionToken) {
        return SessionTokenResponse.builder()
                                   .errorCode(ErrorCodes.SUCCESS.getCode())
                                   .result(SessionTokenResult.builder().sessionToken(sessionToken).build())
                                   .build();
    }
}
