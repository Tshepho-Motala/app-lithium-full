package lithium.service.casino.provider.iforium.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProviderConfigProperties {

    WHITELIST_IP("whitelistIPs", "Whitelist of IP addresses for IForium API."),
    SECURITY_USER_NAME("secureUserName", "Security username for IForium API."),
    SECURITY_USER_PASSWORD("secureUserPassword", "Security password for IForium API."),
    LOBBY_URL("lobbyurl",
              "URL Gameflex will redirect the player to when leaving the Game. Default value is: https://www.operator.com/lobby"),
    STARTGAME_BASE_URL("startGameUrl", "The operator will be provided with a BaseUrl to launch any Desktop or Mobile game."),
    CASINO_ID("casinoid", "Identifier supplied by Iforium to represent a single brand within the Operator Website"),
    LIST_GAME_URL("listgame", "URL need to obtain .CSV with list of games"),
    REGULATIONS_ENABLED("regulationsEnabled", "UK Regulations"),
    REGULATION_SESSION_DURATION("regulationSessionDuration", "Duration, in seconds, since the Player logged in to their session."),
    REGULATION_INTERVAL("regulationInterval", "Interval, in seconds, Player has requested for each reality check."),
    REGULATION_GAME_HISTORY_URL("regulationGameHistoryUrl",
                                "Url to the players game history or account area located on the Operator Website."),
    REGULATION_BONUS_URL("regulationBonusUrl", "URL to the bonus terms and conditions."),
    REGULATION_OVERRIDE_RTS_13_MODE("regulationOverrideRts13Mode", "Reality Check Message control."),
    REGULATION_OVERRIDE_CMA_MODE("regulationOverrideCmaMode", "CMA"),
    BLUEPRINT_JACKPOT_URL("blueprintProgressiveJackpotFeedUrl", "Progressive Jackpot Feeds URL");


    private final String name;
    private final String tooltip;
}
