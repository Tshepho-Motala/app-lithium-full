package lithium.service.casino.job;

import lithium.leader.LeaderCandidate;
import lithium.metrics.LithiumMetricsService;
import lithium.metrics.TimeThisMethod;
import lithium.service.casino.data.entities.PlayerBonusToken;
import lithium.service.casino.exceptions.Status424InvalidBonusTokenStateException;
import lithium.service.casino.service.BonusTokenService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PlayerBonusTokenExpiredJob {

	@Autowired @Setter private LithiumMetricsService metrics;
	@Autowired @Setter private LeaderCandidate leaderCandidate;
	@Autowired @Setter private BonusTokenService bonusTokenService;
	
	@Scheduled(cron="${lithium.service.casino.job.bonus.exired.cron:*/10 * * * * *}")
	@TimeThisMethod
	public void expirePlayerBonusTokens() {

		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		int pageSize = 1000;
		PageRequest pageRequest = PageRequest.of(0, pageSize);
		Page<PlayerBonusToken> expiredPlayerBonuses = bonusTokenService.findExpiredPlayerBonusTokens(pageRequest);

		do {
			expiredPlayerBonuses.forEach(playerBonusToken -> {
				try {
					bonusTokenService.expireBonusToken(playerBonusToken.getId());
				} catch (Status424InvalidBonusTokenStateException e) {
					log.debug("Player bonus token could not be expired: " + e.getMessage(), e);
				}
			});
			expiredPlayerBonuses = bonusTokenService.findExpiredPlayerBonusTokens(expiredPlayerBonuses.nextPageable());
		} while (!expiredPlayerBonuses.isLast() && expiredPlayerBonuses.hasContent());
	}
}
