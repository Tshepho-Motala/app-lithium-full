package lithium.service.casino.provider.sportsbook.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.modules.ModuleInfo;
import lithium.service.casino.client.objects.BonusRestrictionRequest;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.service.casino.provider.sportsbook.data.ExternalBonusRestrictionRequest;
import lithium.service.casino.provider.sportsbook.response.BonusRestrictionResponse;
import lithium.util.HmacSha256HashCalculator;
import lithium.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class BonusRestrictionService {
    private Logger log = LoggerFactory.getLogger(BonusRestrictionService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProviderConfigService providerConfigService;

    @Autowired
    ModuleInfo moduleInfo;

    @Autowired
    ChangeLogService changeLogService;

    public BonusRestrictionResponse toggle(BonusRestrictionRequest bonusRestriction, String domainName) {

        BonusRestrictionResponse response = BonusRestrictionResponse.builder()
                .errorCode(0)
                .build();

        List<ChangeLogFieldChange> cls = new ArrayList<>();

        try {

            ProviderConfig providerConfig =  providerConfigService.getConfig(moduleInfo.getModuleName(), domainName);

            if(!StringUtil.isEmpty(providerConfig.getBonusRestrictionUrl()) && providerConfig.getBonusRestrictionUrl().trim().length() > 0) {
                ExternalBonusRestrictionRequest externalBonusRestrictionRequest = ExternalBonusRestrictionRequest.builder()
                        .restrict(bonusRestriction.isRestricted())
                        .sha(calculateSha(bonusRestriction, providerConfig.getBonusRestrictionKey()))
                        .playerId(bonusRestriction.getPlayerId())
                        .build();


                ResponseEntity<BonusRestrictionResponse> responseEntity = restTemplate.postForEntity(providerConfig.getBonusRestrictionUrl(), externalBonusRestrictionRequest, BonusRestrictionResponse.class);
                response = responseEntity.getBody();

                if (responseEntity.getStatusCodeValue() == 200) {
                    log.debug("Received bonus restriction response for user " + bonusRestriction.getPlayerGuid(), response);

                    changeLogService.registerChangesForNotesWithFullNameAndDomain("user.restriction.external.comps", bonusRestriction.isRestricted() ? "create" : "delete", bonusRestriction.getPlayerId(), "default/system", null,
                            String.format("External bonus restriction was updated for user: %s", bonusRestriction.getPlayerGuid()), null, cls, Category.RESPONSIBLE_GAMING, SubCategory.RESTRICTION, 1, domainName);
                    return response;
                }

                throw new RuntimeException(response.getErrorMessage());
            }
            else {
                log.debug(String.format("External bonus restriction was skipped because service-casino-provider-sportsbook property bonusRestrictionUrl was not configured for domain:%s, user: %s"
                        , domainName, bonusRestriction.getPlayerGuid()));
            }
        }
        catch(Exception e) {
            String message = String.format("Could not toggle external sportsbook bonus restriction for user %s, playerId: %s, restrict: %s",
                    bonusRestriction.getPlayerGuid(), bonusRestriction.getPlayerId(), bonusRestriction.isRestricted());

            if (bonusRestriction.getRetryCount() != null) {
                message = String.format("%s, retry %s of %s", message, bonusRestriction.getRetryCount(), bonusRestriction.getRetryTotal());
            }

            log.debug("BonusRestriction" +  bonusRestriction.toString());
            log.error(message, e);


            cls.add(ChangeLogFieldChange.builder()
                            .field("error")
                            .toValue(e.getMessage())
                    .build()
            );

            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.restriction.external.comps", "edit", bonusRestriction.getPlayerId(), "default/system", null,
                    message, null, cls, Category.RESPONSIBLE_GAMING, SubCategory.RESTRICTION, 1, domainName);

            response.setErrorCode(500);
            response.setErrorMessage(message);
        }

        return response;
    }

    public String calculateSha(BonusRestrictionRequest bonusRestriction, String key) {
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(key);
        hasher.addItem(bonusRestriction.getPlayerId());
        hasher.addItem(bonusRestriction.isRestricted());

        return hasher.calculateHash();
    }
}

