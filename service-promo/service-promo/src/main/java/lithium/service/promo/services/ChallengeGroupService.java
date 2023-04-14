package lithium.service.promo.services;

import lithium.service.promo.data.entities.ChallengeGroup;
import lithium.service.promo.data.entities.PromotionRevision;
import lithium.service.promo.data.repositories.ChallengeGroupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChallengeGroupService {

    private final ChallengeGroupRepository challengeGroupRepository;

    public ChallengeGroup findOrCreate(Long challengeGroupId, PromotionRevision promotionRevision) {
        if(challengeGroupId == null) {
            return challengeGroupRepository.save(
                    ChallengeGroup.builder()
                            .promotionRevision(promotionRevision)
                            .build());
        }

        ChallengeGroup challengeGroup = challengeGroupRepository.getById(challengeGroupId);

        if(challengeGroup == null) {
            challengeGroup = challengeGroupRepository.save(
                    ChallengeGroup.builder()
                            .promotionRevision(promotionRevision)
                            .build());
        }

        return challengeGroup;
    }
}
