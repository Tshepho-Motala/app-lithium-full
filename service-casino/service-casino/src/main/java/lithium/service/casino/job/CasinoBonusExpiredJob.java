package lithium.service.casino.job;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.data.entities.PlayerBonusHistory;
import lithium.service.casino.data.repositories.PlayerBonusHistoryRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lithium.leader.LeaderCandidate;
import lithium.metrics.LithiumMetricsService;
import lithium.service.casino.data.entities.PlayerBonus;
import lithium.service.casino.data.repositories.PlayerBonusRepository;
import lithium.service.casino.service.CasinoBonusService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CasinoBonusExpiredJob {
	
	@Autowired
	private CasinoBonusService casinoBonusService;
	@Autowired 
	private PlayerBonusRepository playerBonusRepository;
	@Autowired
	private PlayerBonusHistoryRepository playerBonusHistoryRepository;
	@Autowired
	private LeaderCandidate leaderCandidate;

	@TimeThisMethod
	@Scheduled(cron="${lithium.service.casino.job.bonus.exired.cron:*/10 * * * * *}")
	public void expiredBonusJob() {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		SW.start("checkCurrentBonuses");
		checkCurrentBonuses();
		SW.stop();
		SW.start("checkSpecificPBHExpires");
		checkSpecificPBHExpires();
		SW.stop();
	}

	//TODO: This was added for expiring bonuses for Squads and will be handled differntly in a future iteration. the logic should not live in bonus, but should be mangeged from promo side.
	private void checkSpecificPBHExpires () {
		try {
			int pageSize = 1000;
			log.trace("Starting checkSpecificPBHExpires");
			DateTime serverTimeLocal = new DateTime();
			PageRequest pageRequest = PageRequest.of(0, pageSize);
			Page<PlayerBonusHistory> playerBonusPage = playerBonusHistoryRepository.findByStartedDateBeforeAndCompletedFalseAndExpiredFalseAndCancelledFalseAndBonusEnabledTrueAndBonusBonusTypeAndBonusBonusTriggerType(serverTimeLocal.toDate(), 2, 5, pageRequest);

			long pageTotal = playerBonusPage.getTotalPages();

			for (int p = 0; p < pageTotal; ++p) {
				SW.start("pagerequest_" + p);
				pageRequest = PageRequest.of(p, pageSize);
				playerBonusPage = playerBonusHistoryRepository.findByStartedDateBeforeAndCompletedFalseAndExpiredFalseAndCancelledFalseAndBonusEnabledTrueAndBonusBonusTypeAndBonusBonusTriggerType(serverTimeLocal.toDate(), 2, 5, pageRequest);
				playerBonusPage.forEach(pbh -> {
					Integer validDays = pbh.getBonus().getValidDays();
					if (validDays == null) return;
					DateTime startDate = new DateTime(pbh.getStartedDate().getTime());

					if (serverTimeLocal.isBefore(startDate.plusDays(validDays))) {
						return;
					}
					pbh.setExpired(true);
					log.info("Expiring: "+pbh);
					playerBonusHistoryRepository.save(pbh);
//					playerBonus.setCurrent(null);
//					playerBonusRepository.save(playerBonus);
				});
				SW.stop();
			}
		} catch (Exception e) {
			log.error("Error in running player bonus expiration job.", e);
		}
	}

	private void checkCurrentBonuses() {
		try {
			int pageSize = 1000;
			PageRequest pageRequest = PageRequest.of(0, pageSize);
			Page<PlayerBonus> playerBonusPage = playerBonusRepository.findByCurrentNotNull(pageRequest);

			long pageTotal = playerBonusPage.getTotalPages();

			for (int p = 0; p < pageTotal; ++p) {
				SW.start("pagerequest_"+p);
				pageRequest = PageRequest.of(p, pageSize);
				playerBonusPage = playerBonusRepository.findByCurrentNotNull(pageRequest);
				playerBonusPage.forEach(playerBonus -> {
					try {
						if (playerBonus.getCurrent() != null) {
							casinoBonusService.checkWithinValidDays(playerBonus);
							//FIXME: Will need to rework this to conform to new bonus expiry guidelines
						}
					} catch (Exception e) {
						log.error("Job checking for bonus valid days error: playerBonus: " + playerBonus +" " + e.getMessage(), e);
					}
				});
				SW.stop();
			}
		} catch (Exception e) {
			log.error("Error in running player bonus expiration job.", e);
		}
	}
}
