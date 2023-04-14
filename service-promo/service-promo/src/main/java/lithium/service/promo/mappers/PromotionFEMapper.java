package lithium.service.promo.mappers;

import lithium.service.promo.client.objects.frontend.ChallengeFE;
import lithium.service.promo.client.objects.frontend.ChallengeGroupFE;
import lithium.service.promo.client.objects.frontend.PromotionFE;
import lithium.service.promo.client.objects.frontend.RuleFE;
import lithium.service.promo.data.entities.Challenge;
import lithium.service.promo.data.entities.PromotionRevision;
import lithium.service.promo.data.entities.Rule;

import java.text.MessageFormat;

public class PromotionFEMapper extends BaseMapper{

    public PromotionFE mapToPromotionFE(PromotionRevision promotionRevision) {

        Long rewardId = null;
        Long dependsOn = promotionRevision.getDependsOnPromotion() != null ? promotionRevision.getDependsOnPromotion().getId() : null;

        if (promotionRevision.getReward() != null) {
            rewardId = promotionRevision.getReward().getId();
        }

        return PromotionFE.builder()
                .id(promotionRevision.getId())
                .name(promotionRevision.getName())
                .dependsOnPromotion(dependsOn)
                .description(promotionRevision.getDescription())
                .startDate(formatDate(promotionRevision.getStartDate()))
                .endDate(formatDate(promotionRevision.getEndDate()))
                .eventDuration(MessageFormat.format("{0} days", promotionRevision.getEventDuration()))
                .exclusive(promotionRevision.getExclusive())
                .redeemableInEvent(promotionRevision.getRedeemableInEvent())
                .redeemableInTotal(promotionRevision.getRedeemableInTotal())
                .rewardId(rewardId)
                .rewardId(rewardId)
                .xpLevel(promotionRevision.getXpLevel())
                .challengeGroups(promotionRevision.getChallengeGroups().stream().map(group -> ChallengeGroupFE.builder()
                        .id(group.getId())
                        .challenges(group.getChallenges().stream().map(this::mapChallenge).toList())
                        .build()).toList())
                .build();
    }

    public ChallengeFE mapChallenge(Challenge challenge) {
        Long rewardId = null;

        if (challenge.getReward() != null) {
            rewardId = challenge.getReward().getId();
        }

        return ChallengeFE.builder()
                .id(challenge.getId())
                .rewardId(rewardId)
                .rules(challenge.getRules().stream().map(this::mapRule).toList())
                .build();
    }

    public RuleFE mapRule(Rule rule) {
        return RuleFE.builder()
                .activity(rule.getActivity().getName())
                .operator(rule.getOperation().type())
                .value(rule.getValue())
                .build();
    }
}
