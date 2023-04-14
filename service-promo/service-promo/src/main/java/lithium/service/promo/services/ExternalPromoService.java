package lithium.service.promo.services;

import lithium.exceptions.Status470HashInvalidException;
import lithium.service.promo.client.exception.Status411InvalidPromotionException;
import lithium.service.promo.config.ServicePromoConfigurationProperties;
import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.PromotionRevision;
import lithium.service.promo.data.repositories.PromotionRepository;
import lithium.service.promo.data.repositories.PromotionRevisionRepository;
import lithium.service.promo.dtos.ExclusiveAddResponse;
import lithium.service.promo.dtos.PromotionExt;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.HmacSha256HashCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalPromoService {

    private final PromotionRepository promotionRepository;
    private final PromotionRevisionRepository promotionRevisionRepository;
    private final ServicePromoConfigurationProperties configurationProperties;
    private final UserService userService;
    private final UserApiInternalClientService userApiInternalClientService;

    public List<PromotionExt> getPromotionsForDomain(String domainName) {
        List<Promotion> promotionList =  promotionRepository.findByCurrentDomainName(domainName);
        return promotionList.stream().map(promotion -> convertToPromotionExt(promotion.getCurrent()))
                .toList();
    }

    public ExclusiveAddResponse addPlayersToPromotion(Long promotionId, List<String> playerGuids) {
        ExclusiveAddResponse exclusiveAddResponse = ExclusiveAddResponse.builder().build();
        PromotionRevision promotionRevision = promotionRevisionRepository.findOne(promotionId);

        if (promotionRevision == null) {
            throw new Status411InvalidPromotionException();
        }

        for (String playerGuid: playerGuids) {
            try {
                User externalUser = userApiInternalClientService.getUserByGuid(playerGuid);
                promotionRevision.addExclusivePlayer(userService.findOrCreate(playerGuid));
                promotionRevision = promotionRevisionRepository.save(promotionRevision);
                exclusiveAddResponse.setValidPlayerCount(exclusiveAddResponse.getValidPlayerCount() + 1);
            } catch (UserClientServiceFactoryException | Exception e) {
                log.error("Failed to add player {} to promotion {}, reason : {}", playerGuid, promotionRevision, e);
                exclusiveAddResponse.setInvalidPlayerCount(exclusiveAddResponse.getInvalidPlayerCount() + 1);
            }
        }

        exclusiveAddResponse.setSuccess(exclusiveAddResponse.getValidPlayerCount() > 0);

        return exclusiveAddResponse;
    }

    public void validateSha(String key, String sha) throws Status470HashInvalidException {
        HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(configurationProperties.getExternalSecretKey());
        hasher.addItem(key);
        hasher.validate(sha, log, key);
    }

    private PromotionExt convertToPromotionExt(PromotionRevision promotionRevision) {
        return PromotionExt.builder()
                .id(promotionRevision.getId())
                .name(promotionRevision.getName())
                .description(promotionRevision.getDescription())
                .build();
    }

}
