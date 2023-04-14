package lithium.service.casino.provider.roxor.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public enum ProviderConfigProperties {
    LAUNCH_URL("launchURL"),
    SOUND("sound"),
    HOME_BUTTON("homeButton"),
    HOME_POS("homePos"), // "left | right | none"
    BALANCE_POS("balancePos"), // "left | right | none"
    CHAT_HOST("chatHost"),
    CHAT_CONTEXT("chatContext"),
    SESSION_REMINDER_INTERVAL("sessionReminderInterval"),
    SESSION_ELAPSED("sessionElapsed"),
    HOME_PAGE_URL("homePageURL"),
    DEPOSIT_PAGE_URL("depositPageURL"),
    LOBBY_PAGE_URL("lobbyPageURL"),
    TRANSACTION_URL("transactionURL"),
    LOGOUT_URL("logoutURL"),
    LOGIN_PAGE_URL("loginPageURL"),
    GAME_API_URL("gameApiUrl"),
    WEBSITE("website"),
    IP_WHITE_LIST("ipWhiteList"),
    GAME_LIST_URL("gameListUrl"),
    REWARDS_URL("rewardsUrl"),
    REWARDS_DEFAULT_DURATION_IN_HOURS("rewardsDefaultDurationInHours"),
    PROGRESSIVE_URL("progressiveUrl"),
    ADD_CLOCK("addClock"),
    BET_HISTORY_ROUND_DETAIL_URL("betHistoryRoundDetailUrl"),
    BET_HISTORY_ROUND_DETAIL_PROVIDER_ID("betHistoryRoundDetailPid"),
    USE_PLAYER_API_TOKEN("usePlayerApiToken"),
    USE_PLAYER_ID_FROM_GUID("usePlayerIdFromGuid"),
    BET_ROUND_REPLAY_URL("betRoundReplayUrl"),
    FREE_GAMES_URL("freeGamesUrl"),
    EVOLUTION_DIRECT_GAME_LAUNCH_API_URL("evolutionDirectGameLaunchApiUrl"),
    EVOLUTION_DIRECT_GAME_LAUNCH_API_CASINO_ID("evolutionDirectGameLaunchApiCasinoId"),
    EVOLUTION_DIRECT_GAME_LAUNCH_API_USERNAME("evolutionDirectGameLaunchApiUsername"),
    EVOLUTION_DIRECT_GAME_LAUNCH_API_PASSWORD("evolutionDirectGameLaunchApiPassword"),
    MICRO_GAMING_PROGRESSIVE_JACKPOT_FEED_URL("microGamingProgressiveJackpotFeedUrl");

    @Getter
    private final String value;

//	ProviderConfigProperties(String valueParam) {
//			value = valueParam;
//		}
}
