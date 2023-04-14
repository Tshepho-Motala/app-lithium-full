package lithium.service.casino.service;

import lithium.service.casino.client.objects.request.AwardBonusRequest;
import lithium.service.casino.client.objects.response.AwardBonusResponse;
import lithium.service.casino.controllers.FreeRoundBonusController;
import lithium.service.casino.data.entities.BonusRulesInstantReward;
import lithium.service.casino.data.entities.BonusRulesInstantRewardGames;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.repositories.BonusRulesInstantRewardGamesRepository;
import lithium.service.casino.data.repositories.BonusRulesInstantRewardRepository;
import lithium.service.casino.exceptions.Status422InvalidGrantBonusException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CasinoBonusInstantRewardService {

    @Autowired
    private FreeRoundBonusController freeRoundBonusController;

    @Transactional(propagation= Propagation.REQUIRED)
    public void triggerInstantRewards(PlayerBonusHistory pbh, boolean instantBonus) throws Status422InvalidGrantBonusException {
        triggerInstantRewards(pbh, instantBonus, null);
    }
    @Transactional(propagation= Propagation.REQUIRED)
    public void triggerInstantRewards(PlayerBonusHistory pbh, boolean instantBonus, Integer customAmountNotMoney) throws Status422InvalidGrantBonusException {
        log.info("triggerInstantRewards : "+pbh);
        try {
            List<BonusRulesInstantReward> instantRewardRules = instantRewardRules(pbh.getBonus().getId());
            for (BonusRulesInstantReward brir:instantRewardRules) {
                log.info("BonusRulesInstantRewards : "+brir);

                if (brir.getNumberOfUnits() <= 0 || brir.getProvider().trim().isEmpty()) continue;
                String gameList = "";
                List<BonusRulesInstantRewardGames> brirGames = bonusRulesInstantRewardGamesRepository.findByBonusRulesInstantRewardId(brir.getId());
                for (BonusRulesInstantRewardGames brirg:brirGames) {
                    gameList += brirg.getGameId()+"|";
                }
                gameList = StringUtils.removeEnd(gameList, "|");

                AwardBonusRequest request = AwardBonusRequest.builder()
                        .userId(pbh.getPlayerBonus().getPlayerGuid())
                        .rounds((customAmountNotMoney==null)?brir.getNumberOfUnits():customAmountNotMoney)
                        .roundValueInCents(brir.getInstantRewardUnitValue())
                        .games(gameList)
                        .comment(CasinoBonusService.PLAYER_BONUS_HISTORY_ID+"-"+pbh.getId())
                        .description(pbh.getBonus().getBonusName())
                        .extBonusId(""+ pbh.getId())
                        .frbBetConfigId(pbh.getBonus().getId().intValue())
                        .expirationHours((pbh.getBonus().getValidDays()!=null)?(pbh.getBonus().getValidDays()*24):null)
                        .rewardType("INSTANT_REWARD")
                        .bonusCode(pbh.getBonus().getBonusCode())
                        .build();
                request.setDomainName(pbh.getBonus().getDomain().getName());
                request.setProviderGuid(brir.getProvider());
                log.info("AwardBonusRequest : "+request);
                AwardBonusResponse response = freeRoundBonusController.handleAwardBonus(request, null);

                log.info("AwardBonusResponse : "+response);
                if (response != null && response.getErrorCode() != null) {
                    throw new Status422InvalidGrantBonusException(response.getErrorCode(), response.getDescription());
                }
//				PlayerBonusFreespinHistory pbfh = PlayerBonusFreespinHistory.builder()
//						.playerBonusHistory(pbh)
//						.bonusRulesFreespins(brir)
//						.freespinsRemaining(brf.getFreespins())
//						.extBonusId((response.getBonusId()!=null)?response.getBonusId():-1)
//						.build();
//				log.info("PlayerBonusFreespinHistory : "+pbfh);
//				pbfh = playerBonusFreespinHistoryRepository.save(pbfh);
//				log.info("PlayerBonusFreespinHistory : "+pbfh);

				if (instantBonus) {
					// potentially verify if the bonus should trigger any additional items such as trigger type, bonus free money etc
				}
            }
        } catch (Status422InvalidGrantBonusException e) {
            log.error("Could not grant bonus {}", pbh, e);
            throw e;
        } catch (Exception e) {
            log.error("Could not register instant freespins for :"+pbh, e);
        }
    }

    @Autowired
    BonusRulesInstantRewardRepository bonusRulesInstantRewardRepository;

    @Autowired
    BonusRulesInstantRewardGamesRepository bonusRulesInstantRewardGamesRepository;

    public List<BonusRulesInstantReward> instantRewardRules(Long bonusRevisionId) {
        return bonusRulesInstantRewardRepository.findByBonusRevisionId(bonusRevisionId);
    }

    public List<BonusRulesInstantRewardGames> instantRewardGames(Long bonusRevisionId) {
        return bonusRulesInstantRewardGamesRepository.findByBonusRulesInstantRewardBonusRevisionId(bonusRevisionId);
    }

    public List<BonusRulesInstantRewardGames> instantRewardGamesPerRule(Long bonusRulesCasinoFreeBetId) {
        return bonusRulesInstantRewardGamesRepository.findByBonusRulesInstantRewardId(bonusRulesCasinoFreeBetId);
    }
}
