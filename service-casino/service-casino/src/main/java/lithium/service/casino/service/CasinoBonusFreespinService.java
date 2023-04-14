package lithium.service.casino.service;

import lithium.metrics.LithiumMetricsService;
import lithium.service.Response;
import lithium.service.casino.client.objects.request.AwardBonusRequest;
import lithium.service.casino.client.objects.request.BetRequest;
import lithium.service.casino.client.objects.request.CancelBonusRequest;
import lithium.service.casino.client.objects.request.GetBonusInfoRequest;
import lithium.service.casino.client.objects.response.AwardBonusResponse;
import lithium.service.casino.client.objects.response.CancelBonusResponse;
import lithium.service.casino.client.objects.response.GetBonusInfoResponse;
import lithium.service.casino.controllers.FreeRoundBonusController;
import lithium.service.casino.data.entities.BonusRulesFreespinGames;
import lithium.service.casino.data.entities.BonusRulesFreespins;
import lithium.service.casino.data.entities.BonusRulesInstantReward;
import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.casino.data.entities.PlayerBonusFreespinHistory;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.projection.entities.BonusRulesFreespinGamesProjection;
import lithium.service.casino.data.projection.entities.PlayerBonusFreespinHistoryProjection;
import lithium.service.casino.data.projection.repositories.BonusRulesFreespinGamesProjectionRepository;
import lithium.service.casino.data.projection.repositories.PlayerBonusFreespinHistoryProjectionRepository;
import lithium.service.casino.data.repositories.BonusRulesFreespinGamesRepository;
import lithium.service.casino.data.repositories.BonusRulesFreespinsRepository;
import lithium.service.casino.data.repositories.BonusRulesInstantRewardGamesRepository;
import lithium.service.casino.data.repositories.BonusRulesInstantRewardRepository;
import lithium.service.casino.data.repositories.PlayerBonusFreespinHistoryRepository;
import lithium.service.casino.data.repositories.PlayerBonusHistoryRepository;
import lithium.service.casino.exceptions.Status422InvalidGrantBonusException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.games.client.GamesClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class CasinoBonusFreespinService {
	@Autowired
	private LithiumMetricsService metrics;
	@Autowired
	private FreeRoundBonusController freeRoundBonusController;
	@Autowired
	private PlayerBonusFreespinHistoryRepository playerBonusFreespinHistoryRepository;
	@Autowired
	private PlayerBonusFreespinHistoryProjectionRepository playerBonusFreespinHistoryProjectionRepository;
	@Autowired
	private BonusRulesFreespinGamesProjectionRepository bonusRulesFreespinGamesProjectionRepository;
	@Autowired
	private PlayerBonusHistoryRepository playerBonusHistoryRepository;
	@Autowired
	private BonusRulesFreespinsRepository bonusRulesFreespinsRepository;
	@Autowired
	private BonusRulesInstantRewardRepository bonusRulesInstantRewardRepository;
	@Autowired
	private BonusRulesInstantRewardGamesRepository bonusRulesInstantRewardGamesRepository;
	@Autowired
	private BonusRulesFreespinGamesRepository bonusRulesFreespinGamesRepository;
	@Autowired
	private LithiumServiceClientFactory services;
	
	public List<BonusRulesFreespins> freespinRules(Long bonusRevisionId) {
		return bonusRulesFreespinsRepository.findByBonusRevisionId(bonusRevisionId);
	}
	private List<BonusRulesFreespins> freespinRules(PlayerBonus playerBonus) {
		return bonusRulesFreespinsRepository.findByBonusRevisionId(playerBonus.getCurrent().getBonus().getId());
	}
	public List<BonusRulesFreespinGames> freespinGames(Long bonusRevisionId) {
		return bonusRulesFreespinGamesRepository.findByBonusRulesFreespinsBonusRevisionId(bonusRevisionId);
	}
	public List<BonusRulesFreespinGames> freespinGamesPerRule(Long bonusRulesFreespinsId) {
		return bonusRulesFreespinGamesRepository.findByBonusRulesFreespinsId(bonusRulesFreespinsId);
	}
	
	public int subtractFreespin(String playerGuid, Integer extBonusId) {
		return updateFreespins(playerGuid, extBonusId, -1);
	}
	
	private int updateFreespins(String playerGuid, Integer extBonusId, Integer freespinAdjust) {
		PlayerBonusFreespinHistory pbfh = playerBonusFreespinHistoryRepository.findByPlayerBonusHistoryPlayerBonusPlayerGuidAndExtBonusId(playerGuid, extBonusId);
		if (pbfh == null) return -2;
		int remaining = pbfh.getFreespinsRemaining()+freespinAdjust;
		pbfh.setFreespinsRemaining(remaining);
		playerBonusFreespinHistoryRepository.save(pbfh);
		return remaining;
	}
	
	//TODO
//	private List<BonusRulesFreespinGames> freespinGames(PlayerBonus playerBonus) {
//		return bonusRulesFreespinGamesRepository.findByBonusRulesFreespinsBonusRevisionId(playerBonus.getCurrent().getBonus().getId());
//	}
	
	//key = "{#domainName}", 
	@Cacheable(cacheNames = "lithium.service.games.services.getResponseGame", unless="#result == null")
	public List<lithium.service.games.client.objects.Game> getDomainGameList(String domainName) throws Exception {
		log.debug("Getting List");
		GamesClient gc = null;
		try {
			gc = services.target(GamesClient.class, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.warn("Problem getting GamesClient", e);
		}
		if (gc == null) return null;
		Response<Iterable<lithium.service.games.client.objects.Game>> domainGamesResponse = gc.listDomainGames(domainName);
		if (domainGamesResponse.isSuccessful()) {
			List<lithium.service.games.client.objects.Game> target = new ArrayList<>();
			domainGamesResponse.getData().forEach(target::add);
			return target;
		}
		return null;
	}
	
	public void cancelFreespins(PlayerBonus pb) throws Exception {
		List<PlayerBonusFreespinHistory> playerBonusFreespinHistory = playerBonusFreespinHistoryRepository.findByPlayerBonusHistoryId(pb.getCurrent().getId());
		for (PlayerBonusFreespinHistory pbfh:playerBonusFreespinHistory) {
			if ((pbfh.getExtBonusId()!=null) && (pbfh.getExtBonusId()!=-1)) {
				CancelBonusRequest request = new CancelBonusRequest(pbfh.getExtBonusId(), pbfh.getExtBonusId()+"", pb.getPlayerGuid());
				request.setDomainName(pb.getCurrent().getBonus().getDomain().getName());
				request.setProviderGuid(pbfh.getBonusRulesFreespins().getProvider());
				log.info("CancelBonusRequest : "+request);
				CancelBonusResponse response = freeRoundBonusController.handleCancelBonus(request);
				log.info("CancelBonusResponse : "+response);
			}
		}
	}
	
	public void cancelFreespins(Integer extBonusId, String provider, String domainName, String gameId, String userId) throws Exception {
		CancelBonusRequest request = new CancelBonusRequest(extBonusId, gameId, userId);
		request.setDomainName(domainName);
		request.setProviderGuid(provider);
		log.info("CancelBonusRequest : "+request);
		CancelBonusResponse response = freeRoundBonusController.handleCancelBonus(request);
		log.info("CancelBonusResponse : "+response);
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public void triggerFreeSpins(PlayerBonusHistory pbh, boolean instantBonus) throws Status422InvalidGrantBonusException {
		triggerFreeSpins(pbh, instantBonus, null);
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public void triggerFreeSpins(PlayerBonusHistory pbh, boolean instantBonus, Integer customAmountNotMoney) throws Status422InvalidGrantBonusException {
		log.info("triggerFreeSpins : "+pbh);
		try {
			List<BonusRulesFreespins> freespinRules = freespinRules(pbh.getBonus().getId());
			for (BonusRulesFreespins brf:freespinRules) {
				log.info("BonusRulesFreespins : "+brf);

				if (brf.getFreespins() <= 0 || brf.getProvider().trim().isEmpty()) continue;

				String gameList = "";
				List<BonusRulesFreespinGames> brfGames = bonusRulesFreespinGamesRepository.findByBonusRulesFreespinsId(brf.getId());
				for (BonusRulesFreespinGames brfg:brfGames) {
					gameList += brfg.getGameId()+"|";
				}
				gameList = StringUtils.removeEnd(gameList, "|");

				AwardBonusRequest request = AwardBonusRequest.builder()
						.userId(pbh.getPlayerBonus().getPlayerGuid())
						.rounds((customAmountNotMoney==null)?brf.getFreespins():customAmountNotMoney)
						.roundValueInCents(brf.getFreeSpinValueInCents())
						.games(gameList)
						.comment(CasinoBonusService.PLAYER_BONUS_HISTORY_ID+"-"+pbh.getId())
						.description(pbh.getBonus().getBonusName())
						.extBonusId(""+ pbh.getId())
						.frbBetConfigId(pbh.getBonus().getId().intValue())
						.expirationHours((pbh.getBonus().getValidDays()!=null)?(pbh.getBonus().getValidDays()*24):null)
						.rewardType("FREE_SPIN")
						.bonusCode(pbh.getBonus().getBonusCode())
						.build();
				request.setDomainName(pbh.getBonus().getDomain().getName());
				request.setProviderGuid(brf.getProvider());
				log.info("AwardBonusRequest : "+request);
				AwardBonusResponse response = freeRoundBonusController.handleAwardBonus(request, null);

				log.info("AwardBonusResponse : "+response);
				if (response != null && response.getErrorCode() != null) {
					throw new Status422InvalidGrantBonusException(response.getErrorCode(), response.getDescription());
				}
				PlayerBonusFreespinHistory pbfh = PlayerBonusFreespinHistory.builder()
						.playerBonusHistory(pbh)
						.bonusRulesFreespins(brf)
						.freespinsRemaining((customAmountNotMoney==null)?brf.getFreespins():customAmountNotMoney)
						.extBonusId((response.getBonusId()!=null)?response.getBonusId():-1)
						.build();
				log.info("PlayerBonusFreespinHistory : "+pbfh);
				pbfh = playerBonusFreespinHistoryRepository.save(pbfh);
				log.info("PlayerBonusFreespinHistory : "+pbfh);

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

	public List<PlayerBonusFreespinHistory> playerBonusFreespinHistory(PlayerBonus pb) {
		return playerBonusFreespinHistoryRepository.findByPlayerBonusHistoryId(pb.getCurrent().getId());
	}
	
	public PlayerBonusFreespinHistoryProjection playerBonusFreespinHistoryProjection(Long playerBonusHistoryId) {
		return playerBonusFreespinHistoryProjectionRepository.findByPlayerBonusHistoryId(playerBonusHistoryId);
	}
	
	public List<BonusRulesFreespinGamesProjection> bonusRulesFreespinGamesProjection(Long bonusRevisionId) {
		return bonusRulesFreespinGamesProjectionRepository.findByBonusRulesFreespinsBonusRevisionId(bonusRevisionId);
	}
	
	public boolean hasIncompleteFreespins(PlayerBonus currentBonus) {
		if (currentBonus.getCurrent() == null) return false;
		List<PlayerBonusFreespinHistory> playerBonusFreespinHistory = playerBonusFreespinHistory(currentBonus);
		for (PlayerBonusFreespinHistory pbfh:playerBonusFreespinHistory) {
			log.debug("PlayerBonusFreespinHistory : "+pbfh);
			if (pbfh.getFreespinsRemaining() > 0) {
				return true;
			}
		}
		return false;
	}
	
	public Boolean updatePlayThrough(BetRequest request, PlayerBonus currentBonus) {
		if ((request.getWin() != null) && (request.getWin() != 0)) {
			Integer extBonusId = new Integer(request.getBonusId());
			PlayerBonusFreespinHistory playerBonusFreespinHistory = playerBonusFreespinHistoryRepository.findByPlayerBonusHistoryIdAndExtBonusId(currentBonus.getCurrent().getId(), extBonusId);
			log.info("updatePlayThrough (freespins) : "+playerBonusFreespinHistory);
			if (playerBonusFreespinHistory == null) return false;
			if (playerBonusFreespinHistory.getBonusRulesFreespins() == null) return false;
			if (playerBonusFreespinHistory.getBonusRulesFreespins().getWagerRequirements() == null) return false;
			BigDecimal wagerReq = new BigDecimal(playerBonusFreespinHistory.getBonusRulesFreespins().getWagerRequirements());
			BigDecimal playThroughRequiredCents = new BigDecimal(currentBonus.getCurrent().getPlayThroughRequiredCents());
			BigDecimal adjustment = new BigDecimal(request.getWin());
			adjustment = adjustment.multiply(wagerReq);
			log.info("Adding "+adjustment+" to PlayThrough for "+request.getUserGuid()+" ("+currentBonus.getCurrent().getBonus().getBonusCode()+")");
			Long newPlayThroughRequiredCents = playThroughRequiredCents.add(adjustment).longValue();
			currentBonus.getCurrent().setPlayThroughRequiredCents(newPlayThroughRequiredCents);
			playerBonusHistoryRepository.save(currentBonus.getCurrent());
			return true;
		}
		return false;
	}
	
	@Async
	public CompletableFuture<Boolean> updatePlayerBonusFreespinHistory(BetRequest betRequest, PlayerBonus playerBonus, List<PlayerBonusFreespinHistory> playerBonusFreespinHistory) {
		CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
		Boolean complete = false;
		try {
			complete = metrics.timer(log).time("updatePlayerBonusFreespinHistory", (StopWatch sw) -> {
				PlayerBonusFreespinHistory pbfh = playerBonusFreespinHistory.get(0);
				GetBonusInfoRequest request = GetBonusInfoRequest.builder()
				.userId(playerBonus.getPlayerGuid())
				.build();
				request.setDomainName(playerBonus.getCurrent().getBonus().getDomain().getName());
				request.setProviderGuid(pbfh.getBonusRulesFreespins().getProvider());
				log.info("GetBonusInfoRequest : "+request);
				sw.start("handleGetBonusInfo");
				GetBonusInfoResponse response = freeRoundBonusController.handleGetBonusInfo(request);
				sw.stop();
				sw.start("update.playerBonusFreespinHistory");
				for (lithium.service.casino.client.objects.response.Bonus b:response.getBonus()) {
					if (b.getBonusId().equals(pbfh.getExtBonusId())) {
						pbfh.setFreespinsRemaining(b.getRoundsLeft());
						playerBonusFreespinHistoryRepository.save(pbfh);
					}
				}
				sw.stop();
				log.info("GetBonusInfoResponse : "+response);
				if ((betRequest.getWin() != null) && (betRequest.getWin() != 0)) {
					return updatePlayThrough(betRequest, playerBonus);
				} else {
					return true;
				}
			});
		} catch (Exception e) {
			log.error("Could not check freespins for :"+playerBonus, e);
			complete = false;
		}
		future.complete(complete);
		return future;
	}
	
	BonusRulesFreespins findOrCreate(BonusRulesFreespins bonusRulesFreespins) {
		BonusRulesFreespins bonusRulesFreespins2 = bonusRulesFreespinsRepository.findByBonusRevisionIdAndProvider(bonusRulesFreespins.getBonusRevision().getId(), bonusRulesFreespins.getProvider());
		if (bonusRulesFreespins2 == null) {
			bonusRulesFreespins2 = bonusRulesFreespinsRepository.save(bonusRulesFreespins);
		}
		return bonusRulesFreespins2;
	}
}
