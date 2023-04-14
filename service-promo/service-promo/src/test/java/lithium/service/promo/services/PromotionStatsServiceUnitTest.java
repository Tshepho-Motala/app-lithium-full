package lithium.service.promo.services;

import lithium.service.promo.client.enums.Operation;
import lithium.service.promo.data.entities.Challenge;
import lithium.service.promo.data.entities.ChallengeGroup;
import lithium.service.promo.data.entities.PromotionRevision;
import lithium.service.promo.data.entities.Reward;
import lithium.service.promo.data.entities.Rule;
import lithium.service.promo.data.entities.User;
import lithium.service.promo.data.entities.UserPromotion;
import lithium.service.promo.data.entities.UserPromotionChallenge;
import lithium.service.promo.data.entities.UserPromotionChallengeGroup;
import lithium.service.promo.data.entities.UserPromotionChallengeRule;
import org.apache.commons.lang.BooleanUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PromotionStatsServiceUnitTest {

    @Mock
    private UserPromotionService userPromotionService;

    @Mock RewardService rewardService;

    @InjectMocks
    private PromotionStatsService promotionStatsService;

    @Test
    public void shouldCompleteChallengeWhenAllRulesAreRequiredWithHundredPercentValues() {
       var up = UserPromotion.builder()
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();
        var rule1= UserPromotionChallengeRule.builder()
                .rule(Rule.builder().operation(Operation.ACCUMULATOR).build())
                .percentage(BigDecimal.valueOf(100L))
                .build();
        var rule2 = UserPromotionChallengeRule.builder()
                .percentage(BigDecimal.valueOf(100L))
                .rule(Rule.builder().operation(Operation.ACCUMULATOR).build())
                .build();

        var upc = UserPromotionChallenge.builder()
                .rules(List.of(rule1, rule2))
                .userPromotion(up)
                .challenge(Challenge.builder().reward(Reward.builder().rewardId(100L).build()).requiresAllRules(true).build())
                .build();
        upc = promotionStatsService.calculateChallengePercentage(upc, false);

        Assertions.assertEquals(BigDecimal.valueOf(100L), upc.getPercentage());
        Assertions.assertTrue(BooleanUtils.toBoolean(upc.getChallengeComplete()));
    }

    @Test
    public void shouldNotCompleteChallengeWhenAllRulesAreRequiredAndNotAllRulesReachedHundredPercent() {
        var up = UserPromotion.builder()
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();
        var rule1= UserPromotionChallengeRule.builder().rule(Rule.builder().operation(Operation.ACCUMULATOR).build()).percentage(BigDecimal.valueOf(30L)).build();
        var rule2 = UserPromotionChallengeRule.builder().rule(Rule.builder().operation(Operation.ACCUMULATOR).build()).percentage(BigDecimal.valueOf(100L)).build();
        var upc = UserPromotionChallenge.builder()
                .rules(List.of(rule1, rule2))
                .userPromotion(up)
                .challenge(Challenge.builder().reward(Reward.builder().rewardId(100L).build()).requiresAllRules(true).build())
                .build();
        upc = promotionStatsService.calculateChallengePercentage(upc, false);
        Assertions.assertFalse(BooleanUtils.toBoolean(upc.getChallengeComplete()));
    }

    @Test
    public void shouldCompleteChallengeWhenNotAllRulesAreRequiredAndRuleContributionIsAtleastHundredPercent() {
        var up = UserPromotion.builder()
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();
        var rule1= UserPromotionChallengeRule.builder().rule(Rule.builder().operation(Operation.ACCUMULATOR).build()).percentage(BigDecimal.valueOf(30)).build();
        var rule2 = UserPromotionChallengeRule.builder().rule(Rule.builder().operation(Operation.ACCUMULATOR).build()).percentage(BigDecimal.valueOf(70L)).build();
        var upc = UserPromotionChallenge.builder()
                .rules(List.of(rule1, rule2))
                .userPromotion(up)
                .challenge(Challenge.builder().reward(Reward.builder().rewardId(100L).build()).requiresAllRules(false).build())
                .build();
        upc = promotionStatsService.calculateChallengePercentage(upc, false);

        Assertions.assertEquals(BigDecimal.valueOf(100L), upc.getPercentage());
        Assertions.assertTrue(BooleanUtils.toBoolean(upc.getChallengeComplete()));
    }

    @Test
    public void shouldNotCompleteChallengeWhenNotAllRulesAreRequiredAndRuleContributionIsNotHundredPercent() {
        var up = UserPromotion.builder()
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();
        var rule1= UserPromotionChallengeRule.builder().rule(Rule.builder().operation(Operation.ACCUMULATOR).build()).percentage(BigDecimal.valueOf(30)).build();
        var rule2 = UserPromotionChallengeRule.builder().rule(Rule.builder().operation(Operation.ACCUMULATOR).build()).percentage(BigDecimal.valueOf(60L)).build();
        var upc = UserPromotionChallenge.builder()
                .rules(List.of(rule1, rule2))
                .userPromotion(up)
                .challenge(Challenge.builder().reward(Reward.builder().rewardId(100L).build()).requiresAllRules(false).build())
                .build();
        upc = promotionStatsService.calculateChallengePercentage(upc, false);

        Assertions.assertFalse(BooleanUtils.toBoolean(upc.getChallengeComplete()));
    }

    @Test
    public void shouldCompleteChallengePathWith_OR_ChallengesWhenOneOfChallengesIsAtleastHundredPercent() {
        var up = UserPromotion.builder()
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();
        var upc1= UserPromotionChallenge.builder().percentage(BigDecimal.valueOf(10)).build();
        var upc2 = UserPromotionChallenge.builder().percentage(BigDecimal.valueOf(100L)).build();

        var upcg = UserPromotionChallengeGroup.builder()
                .challengeGroup(ChallengeGroup.builder().requiresAllChallenges(false).build())
                .userPromotionChallenges(List.of(upc1, upc2))
                .build();

        upcg = promotionStatsService.calculateChallengeGroupPercentage(upcg, false);

        Assertions.assertEquals(BigDecimal.valueOf(100L), upcg.getPercentage());
        Assertions.assertNotNull(upcg.getCompleted());
    }

    @Test
    public void shouldNotCompleteChallengePathWith_OR_ChallengesWhenOneOfChallengesIsAtleastHundredPercent() {
        var up = UserPromotion.builder()
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();
        var upc1= UserPromotionChallenge.builder().percentage(BigDecimal.valueOf(10)).build();
        var upc2 = UserPromotionChallenge.builder().percentage(BigDecimal.valueOf(70L)).build();

        var upcg = UserPromotionChallengeGroup.builder()
                .challengeGroup(ChallengeGroup.builder().requiresAllChallenges(false).build())
                .userPromotionChallenges(List.of(upc1, upc2))
                .build();

        upcg = promotionStatsService.calculateChallengeGroupPercentage(upcg, false);
        Assertions.assertEquals(70, upcg.getPercentage().intValue());
        Assertions.assertNull(upcg.getCompleted());
    }

    @Test
    public void shouldCompleteChallengePathWith_AND_ChallengesWhenAllChallengesAreHundredPercent() {
        var up = UserPromotion.builder()
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();
        var upc1= UserPromotionChallenge.builder().percentage(BigDecimal.valueOf(100L)).build();
        var upc2 = UserPromotionChallenge.builder().percentage(BigDecimal.valueOf(100L)).build();

        var upcg = UserPromotionChallengeGroup.builder()
                .challengeGroup(ChallengeGroup.builder().requiresAllChallenges(true).build())
                .userPromotionChallenges(List.of(upc1, upc2))
                .build();

        upcg = promotionStatsService.calculateChallengeGroupPercentage(upcg, false);

        Assertions.assertEquals(BigDecimal.valueOf(100L), upcg.getPercentage());
        Assertions.assertNotNull(upcg.getCompleted());
    }

    @Test
    public void shouldNotCompleteChallengePathWith_AND_ChallengesWhenAllChallengesAreNotHundredPercent() {
        var up = UserPromotion.builder()
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();
        var upc1= UserPromotionChallenge.builder().percentage(BigDecimal.valueOf(50L)).build();
        var upc2 = UserPromotionChallenge.builder().percentage(BigDecimal.valueOf(100L)).build();

        var upcg = UserPromotionChallengeGroup.builder()
                .challengeGroup(ChallengeGroup.builder().requiresAllChallenges(true).build())
                .userPromotionChallenges(List.of(upc1, upc2))
                .build();

        upcg = promotionStatsService.calculateChallengeGroupPercentage(upcg, false);

        Assertions.assertEquals(75, upcg.getPercentage().intValue());
        Assertions.assertNull(upcg.getCompleted());
    }


    @Test
    public void shouldCompletePromotionWith_AND_PathsWhenAllPathsAreHundredPercent() {
        var upcg1= UserPromotionChallengeGroup.builder().percentage(BigDecimal.valueOf(100L)).build();
        var upcg2 = UserPromotionChallengeGroup.builder().percentage(BigDecimal.valueOf(100L)).build();

        var up = UserPromotion.builder()
                .promotionRevision(PromotionRevision.builder().requiresAllChallengeGroups(true).build())
                .userChallengeGroups(List.of(upcg1, upcg2))
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();

        promotionStatsService.calculatePromotionPercentage(up, false);

        Assertions.assertEquals(BigDecimal.valueOf(100L), up.getPercentage());
        Assertions.assertNotNull(up.getCompleted());
    }

    @Test
    public void shouldNotCompletePromotionWith_AND_PathsWhenNotAllPathsAreHundredPercent() {
        var upcg1= UserPromotionChallengeGroup.builder().percentage(BigDecimal.valueOf(50L)).build();
        var upcg2 = UserPromotionChallengeGroup.builder().percentage(BigDecimal.valueOf(100L)).build();

        var up = UserPromotion.builder()
                .promotionRevision(PromotionRevision.builder().requiresAllChallengeGroups(true).build())
                .userChallengeGroups(List.of(upcg1, upcg2))
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();

        promotionStatsService.calculatePromotionPercentage(up, false);

        Assertions.assertEquals(75, up.getPercentage().intValue());
        Assertions.assertNull(up.getCompleted());
    }


    @Test
    public void shouldCompletePromotionWith_OR_PathsWhenOnePathIsHundredPercent() {
        var upcg1= UserPromotionChallengeGroup.builder().percentage(BigDecimal.valueOf(20)).build();
        var upcg2 = UserPromotionChallengeGroup.builder().percentage(BigDecimal.valueOf(100L)).build();

        var up = UserPromotion.builder()
                .promotionRevision(PromotionRevision.builder().requiresAllChallengeGroups(false).build())
                .userChallengeGroups(List.of(upcg1, upcg2))
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();

        promotionStatsService.calculatePromotionPercentage(up, false);

        Assertions.assertEquals(BigDecimal.valueOf(100L), up.getPercentage());
        Assertions.assertNotNull(up.getCompleted());
    }

    @Test
    public void shouldNotCompletePromotionWith_OR_PathsWhenNoPathIsHundredPercent() {
        var upcg1= UserPromotionChallengeGroup.builder().percentage(BigDecimal.valueOf(50L)).build();
        var upcg2 = UserPromotionChallengeGroup.builder().percentage(BigDecimal.valueOf(65L)).build();

        var up = UserPromotion.builder()
                .promotionRevision(PromotionRevision.builder().requiresAllChallengeGroups(false).build())
                .userChallengeGroups(List.of(upcg1, upcg2))
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();

        promotionStatsService.calculatePromotionPercentage(up, false);

        Assertions.assertEquals(65, up.getPercentage().intValue());
        Assertions.assertNull(up.getCompleted());
    }

    @Test
    public void shouldCompleteChallengeWhenNotAllLastValueRulesAreRequiredAndOneRuleContributionIsAtleastHundredPercent() {
        var up = UserPromotion.builder()
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();
        var rule1= UserPromotionChallengeRule.builder().rule(Rule.builder().operation(Operation.LAST_VALUE).build()).percentage(BigDecimal.valueOf(30)).build();
        var rule2 = UserPromotionChallengeRule.builder().rule(Rule.builder().operation(Operation.LAST_VALUE).build()).percentage(BigDecimal.valueOf(100L)).build();
        var upc = UserPromotionChallenge.builder()
                .rules(List.of(rule1, rule2))
                .userPromotion(up)
                .challenge(Challenge.builder().reward(Reward.builder().rewardId(100L).build()).requiresAllRules(false).build())
                .build();
        upc = promotionStatsService.calculateChallengePercentage(upc, false);

        Assertions.assertEquals(BigDecimal.valueOf(100L), upc.getPercentage());
        Assertions.assertTrue(BooleanUtils.toBoolean(upc.getChallengeComplete()));
    }

    @Test
    public void shouldNotCompleteChallengeWhenNotAllLastValueRulesAreRequiredAndOneRuleContributionIsNotAtleastHundredPercent() {
        var up = UserPromotion.builder()
                .user(User.builder().guid("livescore_uk/2022").build())
                .build();
        var rule1= UserPromotionChallengeRule.builder().rule(Rule.builder().operation(Operation.LAST_VALUE).build()).percentage(BigDecimal.valueOf(30)).build();
        var rule2 = UserPromotionChallengeRule.builder().rule(Rule.builder().operation(Operation.LAST_VALUE).build()).percentage(BigDecimal.valueOf(70L)).build();
        var upc = UserPromotionChallenge.builder()
                .rules(List.of(rule1, rule2))
                .userPromotion(up)
                .challenge(Challenge.builder().reward(Reward.builder().rewardId(100L).build()).requiresAllRules(false).build())
                .build();
        upc = promotionStatsService.calculateChallengePercentage(upc, false);

        Assertions.assertEquals(BigDecimal.valueOf(70).intValue(), upc.getPercentage().intValue());
        Assertions.assertFalse(BooleanUtils.toBoolean(upc.getChallengeComplete()));
    }
}
