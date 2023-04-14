package lithium.service.leaderboard.jobs;

import lithium.leader.LeaderCandidate;
import lithium.metrics.LithiumMetricsService;
import lithium.service.casino.client.data.BonusAllocate;
import lithium.service.casino.client.stream.TriggerBonusStream;
import lithium.service.gateway.client.stream.GatewayExchangeStream;
import lithium.service.leaderboard.data.entities.Entry;
import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.LeaderboardHistory;
import lithium.service.leaderboard.data.entities.LeaderboardPlaceNotification;
import lithium.service.leaderboard.data.repositories.EntryRepository;
import lithium.service.leaderboard.data.repositories.LeaderboardHistoryRepository;
import lithium.service.leaderboard.services.LeaderboardHistoryService;
import lithium.service.leaderboard.services.LeaderboardService;
import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.client.stream.NotificationStream;
import lombok.extern.slf4j.Slf4j;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class LeaderboardJobs {
	@Autowired LeaderboardService leaderboardService;
	@Autowired LeaderboardHistoryService leaderboardHistoryService;
	@Autowired LeaderCandidate leaderCandidate;
	@Autowired GatewayExchangeStream gatewayExchangeStream;
	@Autowired LeaderboardHistoryRepository leaderboardHistoryRepository;
	@Autowired EntryRepository entryRepository;
	@Autowired TriggerBonusStream triggerBonusStream;
	@Autowired NotificationStream notificationStream;
	@Autowired LithiumMetricsService metrics;

	@Scheduled(cron="${lithium.services.leaderboard.jobs.cleanup.cron:0/30 * * * * *}")
	public void jobs() throws InvalidRecurrenceRuleException {
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}
		try {
			cleanupLeaderboards();
			log.debug("#############################################################");
			ranking();
		} catch (Exception e) {
			log.error("Could not complete leaderboard job.", e);
		}
	}

	//TODO: This has been taken out of code, db can do it much faster/efficiently, but mariadb does not support update from CTE yet,
	// so results are read, and then sent back to db to save/persist.. Needs to be revisited at a later stage, this is better solution for now.
	// See lithium.service.leaderboard.data.entities.Entry
	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public void ranking() throws Exception {
		metrics.timer(log).time("ranking", (StopWatch sw) -> {
			Date now = new Date();
			log.debug("ranking :: "+now);
			sw.start("findCurrentOpen");
			List<LeaderboardHistory> histories2 = leaderboardHistoryRepository.findCurrentOpen(DateTime.now());
			sw.stop();
			histories2.forEach(lh -> {
				log.debug("--------------"+lh.getLeaderboard().getName()+"--------------");
				sw.start("denseRankEntries");
				List<Entry> entries = entryRepository.denseRankEntries(lh.getId());
				sw.stop();
				sw.start("save");
				entryRepository.saveAll(entries);
				sw.stop();
				sw.start("streamUpdate");
				leaderboardService.streamUpdate(lh.getLeaderboard().getDomain(), lh.getLeaderboard());
				sw.stop();
			});
			log.debug("==================================================================");
		});
	}

	private void cleanupLeaderboards() throws Exception {
		metrics.timer(log).time("cleanupLeaderboards", (StopWatch sw) -> {
			DateTime now = new DateTime();
			log.debug("cleanupLeaderboards :: "+now);
			sw.start("findExpired");
			List<LeaderboardHistory> histories = leaderboardHistoryService.findExpired();
			sw.stop();
			log.debug("Histories :: "+histories);
			for (LeaderboardHistory lbh:histories) {
				log.info("--------------Cleanup "+lbh.getLeaderboard().getName()+"--------------");
				lbh.setClosed(true);
				sw.start("save");
				lbh = leaderboardHistoryService.save(lbh);
				sw.stop();
				leaderboardHistoryService.add(lbh.getLeaderboard());
				log.info("Processing notifications.");
				sw.start("notifications");
				notifications(lbh);
				sw.stop();
			}
		});
	}

	private void notifications(LeaderboardHistory lbh) {
		Leaderboard l = lbh.getLeaderboard();
		String notification = l.getNotification();
		String notificationNonTop = l.getNotificationNonTop();
		List<LeaderboardPlaceNotification> places = l.getLeaderboardPlaceNotifications();
		List<Integer> ranks = new ArrayList<>();
		log.debug("Processing specified places. ");
		for (LeaderboardPlaceNotification lpn:places) {
			String bonusCode = lpn.getBonusCode();
			String placeNotification = lpn.getNotification();
			Integer rank = lpn.getRank();
			ranks.add(rank);
			List<Entry> entries = entryRepository.findByLeaderboardHistoryAndRank(lbh, rank);
			entries.forEach(e -> {
				log.debug("player: "+e.getUser().guid()+" rank: "+rank+" bonus: "+bonusCode+" notification: "+placeNotification);
				if (bonusCode!=null) triggerReward(e.getRank(), e.getUser().guid(), bonusCode);
				if (placeNotification!=null) triggerNotification(e.getRank(), e.getUser().guid(), placeNotification);
			});
		}
		if (ranks.isEmpty()) ranks.add(-1);
		log.debug("Notifications for non specified top x. (skipping: "+ranks+")");
		if (notification!=null) {
			List<Entry> entries = entryRepository.findByLeaderboardHistoryAndRankNotInAndRankLessThanEqual(lbh, ranks, l.getAmount(), PageRequest.of(0, l.getAmount()));
			entries.forEach(e -> {
				triggerNotification(e.getRank(), e.getUser().guid(), notification);
			});
		}
		log.debug("Notifications for non top x.");
		if (notificationNonTop!=null) {
			List<Entry> entries = entryRepository.findByLeaderboardHistoryAndRankGreaterThan(lbh, l.getAmount());
			entries.forEach(e -> {
				triggerNotification(e.getRank(), e.getUser().guid(), notificationNonTop);
			});
		}
	}

	private void triggerNotification(Integer rank, String playerGuid, String notification) {
		log.debug("Notification : "+rank+" : "+playerGuid+" :: "+notification);
		notificationStream.process(
			UserNotification.builder()
			.userGuid(playerGuid)
			.notificationName(notification)
			.build()
		);
	}

	private void triggerReward(Integer rank, String playerGuid, String bonusCode) {
		if (bonusCode != null && !bonusCode.isEmpty()) {
			log.debug("Bonus : "+rank+" : "+playerGuid+" :: "+bonusCode);
			triggerBonusStream.process(
				BonusAllocate.builder()
				.playerGuid(playerGuid)
				.bonusCode(bonusCode)
				.build()
			);
		}
	}
}
