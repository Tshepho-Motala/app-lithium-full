package lithium.service.casino.cms.jobs;

import lithium.leader.LeaderCandidate;
import lithium.metrics.LithiumMetricsService;
import lithium.service.casino.cms.services.BannerScheduleService;
import lithium.service.casino.cms.services.BannerService;
import lithium.service.casino.cms.storage.entities.Banner;
import lithium.service.casino.cms.storage.entities.BannerSchedule;
import lombok.extern.slf4j.Slf4j;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BannerScheduleCleanupJob {

    @Autowired
    private BannerScheduleService bannerScheduleService;
    @Autowired
    LithiumMetricsService metrics;
    @Autowired
    private LeaderCandidate leaderCandidate;

    @Value("${lithium.services.casino.provider.cms.jobs.banner-schedules.cleanup.batch-size:500}")
    private Integer batchSize;


    @Scheduled(cron="${lithium.services.casino.provider.cms.jobs.banner-schedules.cleanup.cron:0 * * * * *}")
    public void cleanup() throws Exception {
        if (!leaderCandidate.iAmTheLeader()) {
            log.info("I am not the leader");
        }
        log.info("I am the leader");

        org.joda.time.DateTime now = new org.joda.time.DateTime();
        log.debug("cleanupBannerSchedules :: " + now);

        metrics.timer(log).time("cleanupBannerSchedules", (StopWatch sw) -> {


            PageRequest pageRequest = PageRequest.of(0, batchSize);
            Page<BannerSchedule> bannerSchedules;
            List<BannerSchedule> newBannerSchedules = new ArrayList<>();
            do {
                sw.start("findExpiredBannerSchedules");
                bannerSchedules = bannerScheduleService.getExpired(pageRequest);
                sw.stop();
                bannerSchedules.stream().parallel().forEach(bannerSchedule -> {
                    try {
                        bannerSchedule.setClosed(true);
                        Banner banner = bannerSchedule.getBanner();
                        String pattern = banner.getRecurrencePattern().contains("RRULE:")
                                ? banner.getRecurrencePattern().substring(banner.getRecurrencePattern().indexOf("RRULE:") + 6)
                                : banner.getRecurrencePattern();

                        RecurrenceRule rrule = new RecurrenceRule(pattern);
                        RecurrenceRuleIterator iterator = rrule.iterator(new DateTime(bannerSchedule.getStartDate().getTime()));
                        iterator.skip(2);
                        if (iterator.hasNext()) {
                            Date startDate = new Date(iterator.nextDateTime().startOfDay().getTimestamp());
                            BannerSchedule newBannerSchedule = BannerSchedule.create(banner, startDate);
                            newBannerSchedule.setBanner(banner);
                            newBannerSchedules.add(newBannerSchedule);
                        }
                    } catch (InvalidRecurrenceRuleException e) {
                        log.error("Invalid recurrence rule, pattern: " + bannerSchedule.getBanner().getRecurrencePattern(), e);
                    }
                });
                if (!bannerSchedules.getContent().isEmpty()) {
                    sw.start("Saving closed");
                    bannerScheduleService.saveAll(bannerSchedules.getContent());
                    sw.stop();
                }
                if(!newBannerSchedules.isEmpty()) {
                    sw.start("Saving new schedules");
                    bannerScheduleService.saveAll(newBannerSchedules);
                    sw.stop();
                }
                pageRequest = pageRequest.next();
            } while (bannerSchedules != null && bannerSchedules.hasNext());
        });

    }

}
