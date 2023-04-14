package lithium.service.casino.provider.iforium.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class RequestUtils {

    private static final String START_GAME = "startGame";
    private static final String DEMO_GAME = "demoGame";

    public static String getDomainNameFromPlayerGuid(final String playerGuid) {
        return playerGuid.split("/")[0];
    }

    public static String playMode(boolean demo) {
        return demo ? DEMO_GAME : START_GAME;
    }
}
