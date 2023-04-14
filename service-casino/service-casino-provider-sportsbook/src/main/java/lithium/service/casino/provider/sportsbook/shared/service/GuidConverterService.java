package lithium.service.casino.provider.sportsbook.shared.service;

import lithium.metrics.TimeThisMethod;
import lithium.modules.ModuleInfo;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.tokens.LithiumTokenUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GuidConverterService {

    @Autowired
    @Setter
    ProviderConfigService configService;

    @Autowired @Setter
    ModuleInfo moduleInfo;

    @TimeThisMethod
    public String convertFromLithiumToSportbook(String playerGuid) throws Status512ProviderNotConfiguredException {
        String playerDomain;
        String playerId;
        String convertedGuid;

        if (playerGuid.contains("/")) {
            playerDomain = playerGuid.split("/")[0];
            playerId = playerGuid.split("/")[1];
        } else {
            return playerGuid;
        }

        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), playerDomain);
        if (config.getPlayerOffset() != null && !config.getPlayerOffset().isEmpty()) {
            switch (LithiumTokenUtil.getUserGuidStrategy()) {
                case ID:
                    Long playerIDValue = Long.valueOf(playerId);
                    Long playerOffsetValue = Long.valueOf(config.getPlayerOffset());
                    Long finalPlayerIdValue = playerIDValue + playerOffsetValue;
                    convertedGuid = playerDomain + "/" + finalPlayerIdValue;
                    log.debug("Convert from Lithium to SportBook - GuidStrat=ID value received:"+playerGuid+" converted to : "+ convertedGuid);
                    return convertedGuid;
                case USERNAME:
                    convertedGuid = playerDomain + "/" + config.getPlayerOffset() + "_" + playerId;
                    log.debug("Convert from Lithium to SportBook - GuidStrat=UserName value received:"+playerGuid+" converted to : "+ convertedGuid);
                    return convertedGuid;
            }
        }

        log.debug("Convert from Lithium to SportBook - No offset value received:"+playerGuid+" converted to : "+ playerGuid);
        return playerGuid;
    }

    @TimeThisMethod
    public String convertFromSportbookToLithium(String playerGuid) throws Status512ProviderNotConfiguredException {
        String playerDomain;
        String playerId;
        String convertedGuid;

        if (playerGuid.contains("/")) {
            playerDomain = playerGuid.split("/")[0];
            playerId = playerGuid.split("/")[1];
        } else {
            return playerGuid;
        }

        ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), playerDomain);
        if (config.getPlayerOffset() != null && !config.getPlayerOffset().isEmpty()) {
            switch (LithiumTokenUtil.getUserGuidStrategy()) {
                case ID:
                    Long playerIDValue = Long.valueOf(playerId);
                    Long playerOffsetValue = Long.valueOf(config.getPlayerOffset());
                    Long finalPlayerIdValue = playerIDValue - playerOffsetValue;
                    convertedGuid = playerDomain + "/" + finalPlayerIdValue;
                    log.debug("Convert from SportBook to Lithium - GuidStrat=ID value received:"+playerGuid+" converted to : "+ convertedGuid);
                    return convertedGuid;
                case USERNAME:
                    String sportsBookPrefix = config.getPlayerOffset() + "_";
                    String lithiumPlayerId = playerId.substring(
                            playerId.indexOf(sportsBookPrefix) + sportsBookPrefix.length()
                    );
                    convertedGuid = playerDomain + "/" + lithiumPlayerId;
                    log.debug("Convert from Sportbook to Lithium - GuidStrat=UserName value received:"+playerGuid+" converted to : "+ convertedGuid);
                    return convertedGuid;
            }
        }

        log.debug("Convert from Sportbook to Lithium - No offset value received:"+playerGuid+" converted to : "+ playerGuid);
        return playerGuid;
    }
}
