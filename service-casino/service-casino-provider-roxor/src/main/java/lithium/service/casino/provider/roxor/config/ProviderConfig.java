package lithium.service.casino.provider.roxor.config;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProviderConfig implements Serializable {
    private static final long serialVersionUID = 8742924988855907762L;

    private String launchURL;
    private Boolean sound;
    private Boolean homeButton;
    private String homePos; // "left | right | none"
    private String balancePos; // "left | right | none"
    private String chatHost;
    private String chatContext;
    private Integer sessionReminderInterval;
    private Integer sessionElapsed;
    private String homePageURL;
    private String depositPageURL;
    private String lobbyPageURL;
    private String transactionURL;
    private String logoutURL;
    private String loginPageURL;
    private String gameApiUrl;
    private String website;
    private String ipWhiteList;
    private String gameListUrl;
    private String rewardsUrl;
    private Integer rewardsDefaultDurationInHours;
    private String progressiveUrl;
    private Boolean addClock;
    private String betHistoryRoundDetailUrl;
    private String betHistoryRoundDetailPid;
    private Boolean usePlayerApiToken;
    private Boolean usePlayerIdFromGuid;
    private String betRoundReplayUrl;
    private String language;
    private String currency;
    private String country;
    private String freeGamesUrl;
    private String evolutionDirectGameLaunchApiUrl;
    private String evolutionDirectGameLaunchApiCasinoId;
    private String evolutionDirectGameLaunchApiUsername;
    private String evolutionDirectGameLaunchApiPassword;
    private String microGamingProgressiveJackpotFeedUrl;
}
