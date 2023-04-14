package lithium.service.casino.provider.iforium.config;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class IforiumProviderConfig {

    private final List<String> whitelistIPs;

    private final String secureUserName;

    private final String secureUserPassword;

    private final String lobbyUrl;

    private final String startGameUrl;

    private final String casinoId;

    private final String listGameUrl;

    private final boolean regulationsEnabled;

    private final Integer regulationSessionDuration;

    private final Integer regulationInterval;

    private final String regulationGameHistoryUrl;

    private final String regulationBonusUrl;

    private final String regulationOverrideRts13Mode;

    private final String regulationOverrideCmaMode;

    private final String blueprintProgressiveJackpotFeedUrl;

}
