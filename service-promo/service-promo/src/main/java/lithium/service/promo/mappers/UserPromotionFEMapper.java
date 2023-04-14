package lithium.service.promo.mappers;

import lithium.service.promo.client.objects.frontend.UserChallengeFE;
import lithium.service.promo.client.objects.frontend.UserChallengeGroupFE;
import lithium.service.promo.client.objects.frontend.UserPromotionFE;
import lithium.service.promo.client.objects.frontend.UserRuleFE;
import lithium.service.promo.data.entities.UserPromotion;
import lithium.service.promo.data.entities.UserPromotionChallenge;
import lithium.service.promo.data.entities.UserPromotionChallengeGroup;
import lithium.service.promo.data.entities.UserPromotionChallengeRule;

import java.math.BigDecimal;
import java.util.Optional;

public class UserPromotionFEMapper extends  BaseMapper{
    private final PromotionFEMapper promotionFEMapper =new PromotionFEMapper();

    public UserPromotionFE mapToUserPromotionFE(UserPromotion userPromotion) {
        String eventStartDate = formatDate(userPromotion.getPeriod().getDateStart());
        String eventEndDate =  formatDate(userPromotion.getPeriod().getDateEnd());

        return UserPromotionFE.builder()
                .id(userPromotion.getId())
                .completed(userPromotion.getPromotionComplete())
                .percentage(userPromotion.getPercentage().doubleValue())
                .playerGuid(userPromotion.getUser().getGuid())
                .promoEventStart(eventStartDate)
                .promoEventEnd(eventEndDate)
                .promoEventCompleted(formatDate(userPromotion.getCompleted()))
                .promoEventStarted(formatDate(userPromotion.getStarted()))
                .userChallengeGroups(userPromotion.getUserChallengeGroups().stream().map(this::mapToUserChallengeGroupFE).toList())
                .promotion(promotionFEMapper.mapToPromotionFE(userPromotion.getPromotionRevision()))
                .build();
    }

    public UserChallengeGroupFE mapToUserChallengeGroupFE(UserPromotionChallengeGroup userPromotionChallengeGroup) {
        return UserChallengeGroupFE.builder()
                .completed(formatDate(userPromotionChallengeGroup.getCompleted()))
                .percentage(userPromotionChallengeGroup.getPercentage().doubleValue())
                .userChallenges(userPromotionChallengeGroup.getUserPromotionChallenges().stream().map(this::mapToUserChallenge).toList())
                .build();
    }

    public UserChallengeFE mapToUserChallenge(UserPromotionChallenge userPromotionChallenge) {
        return UserChallengeFE.builder()
                .complete(userPromotionChallenge.getChallengeComplete())
                .completed(formatDate(userPromotionChallenge.getCompleted()))
                .started(formatDate(userPromotionChallenge.getStarted()))
                .percentage(userPromotionChallenge.getPercentage().doubleValue())
                .challengeFE(promotionFEMapper.mapChallenge(userPromotionChallenge.getChallenge()))
                .userRules(userPromotionChallenge.getRules().stream().map(this::mapToUserRule).toList())
                .build();
    }

    public UserRuleFE mapToUserRule(UserPromotionChallengeRule rule) {
        return UserRuleFE.builder()
                .completed(formatDate(rule.getCompleted()))
                .started(formatDate(rule.getStarted()))
                .complete(rule.getRuleComplete())
                .percentage(Optional.of(rule.getPercentage()).map(BigDecimal::doubleValue).orElse(0.00))
                .rule(promotionFEMapper.mapRule(rule.getRule()))
                .build();
    }
}
