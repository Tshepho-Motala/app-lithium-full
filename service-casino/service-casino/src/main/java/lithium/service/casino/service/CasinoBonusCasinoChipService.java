package lithium.service.casino.service;

import lithium.service.casino.client.objects.request.AwardBonusRequest;
import lithium.service.casino.client.objects.response.AwardBonusResponse;
import lithium.service.casino.controllers.FreeRoundBonusController;
import lithium.service.casino.data.entities.BonusRulesCasinoChip;
import lithium.service.casino.data.entities.BonusRulesCasinoChipGames;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.repositories.BonusRulesCasinoChipGamesRepository;
import lithium.service.casino.data.repositories.BonusRulesCasinoChipRepository;
import lithium.service.casino.exceptions.Status422InvalidGrantBonusException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Slf4j
public class CasinoBonusCasinoChipService {
    @Autowired
    private BonusRulesCasinoChipRepository bonusRulesCasinoChipRepository;

    @Autowired
    private BonusRulesCasinoChipGamesRepository bonusRulesCasinoChipGamesRepository;

    @Autowired
    private FreeRoundBonusController freeRoundBonusController;

    @Transactional(propagation= Propagation.REQUIRED)
    public void triggerCasinoChips(PlayerBonusHistory pbh, boolean instantBonus) throws Status422InvalidGrantBonusException {
        log.info("triggerCasinoChips : "+pbh);
        try {
            List<BonusRulesCasinoChip> casinoChipRules = casinoChipRules(pbh.getBonus().getId());
            for (BonusRulesCasinoChip brcc:casinoChipRules) {
                log.info("BonusRulesCasinoChip : "+ brcc);

                if (brcc.getCasinoChipValue() <= 0 || brcc.getProvider().trim().isEmpty()) continue;
                String gameList = "";
                List<BonusRulesCasinoChipGames> brccGames = bonusRulesCasinoChipGamesRepository.findByBonusRulesCasinoChipId(brcc.getId());
                for (BonusRulesCasinoChipGames brccg:brccGames) {
                    gameList += brccg.getGameId()+"|";
                }
                gameList = StringUtils.removeEnd(gameList, "|");

                AwardBonusRequest request = AwardBonusRequest.builder()
                        .userId(pbh.getPlayerBonus().getPlayerGuid())
                        .rounds(0)
                        .roundValueInCents(brcc.getCasinoChipValue())
                        .games(gameList)
                        .comment(CasinoBonusService.PLAYER_BONUS_HISTORY_ID+"-"+pbh.getId())
                        .description(pbh.getBonus().getBonusName())
                        .extBonusId(""+ pbh.getId())
                        .frbBetConfigId(pbh.getBonus().getId().intValue())
                        .expirationHours((pbh.getBonus().getValidDays()!=null)?(pbh.getBonus().getValidDays()*24):null)
                        .rewardType("BONUS_CASH")
                        .bonusCode(pbh.getBonus().getBonusCode())
                        .build();
                request.setDomainName(pbh.getBonus().getDomain().getName());
                request.setProviderGuid(brcc.getProvider());
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
            log.error("Could not register casino freebet for :"+pbh, e);
        }
    }



    public List<BonusRulesCasinoChip> casinoChipRules(Long bonusRevisionId) {
        return bonusRulesCasinoChipRepository.findByBonusRevisionId(bonusRevisionId);
    }

    public List<BonusRulesCasinoChipGames> casinoChipGames(Long bonusRevisionId) {
        return bonusRulesCasinoChipGamesRepository.findByBonusRulesCasinoChipBonusRevisionId(bonusRevisionId);
    }

    public List<BonusRulesCasinoChipGames> casinoChipGamesPerRule(Long bonusRulesCasinoFreeBetId) {
        return bonusRulesCasinoChipGamesRepository.findByBonusRulesCasinoChipId(bonusRulesCasinoFreeBetId);
    }
}
