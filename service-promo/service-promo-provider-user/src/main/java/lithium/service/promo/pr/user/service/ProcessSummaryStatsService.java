package lithium.service.promo.pr.user.service;

import lithium.service.client.objects.Granularity;
import lithium.service.promo.client.objects.PromoActivityBasic;
import lithium.service.promo.client.stream.MissionStatsStream;
import lithium.service.promo.pr.user.dto.Activity;
import lithium.service.promo.pr.user.dto.Category;
import lithium.service.promo.pr.user.dto.ExtraFieldType;
import lithium.service.stats.client.objects.LabelValue;
import lithium.service.stats.client.objects.StatSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcessSummaryStatsService {
    private final MissionStatsStream missionStatsStream;

    @Value("${spring.application.name}")
    private String applicationName;

    public void processRegistrationStat(StatSummary statSummary) {
        String playerGuid = statSummary.getStat().getOwner().guid();
        String domainName = statSummary.getStat().getDomain().getName();

        PromoActivityBasic mab = PromoActivityBasic.builder()
            .category(Category.USER)
            .activity(Activity.REGISTRATION)
            .ownerGuid(playerGuid)
            .domainName(domainName)
            .provider(applicationName)
            .labelValues(
                Stream.of(new String[][] {
                    { ExtraFieldType.DAYS_OF_WEEK.getType(), String.valueOf(getDayOfTheWeek(statSummary)) },
                    { ExtraFieldType.REFERRER_GUID.getType(), String.valueOf(referrerGuid(statSummary)) },
                    { ExtraFieldType.PROMO_CODE.getType(), String.valueOf(promoCode(statSummary)) }
                }).collect(Collectors.toMap(data -> data[0], data -> data[1]))
            )
            .value(statSummary.getCount()).build();
        log.debug("Sending: " + mab);
        missionStatsStream.registerActivity(mab);
    }
    public void processLoginStats(StatSummary statSummary) {
        String playerGuid = statSummary.getStat().getOwner().guid();
        String domainName = statSummary.getStat().getDomain().getName();
        log.trace("StatSummary : " + statSummary);
        log.debug("Domain : " + domainName);
        log.debug("Player : " + playerGuid);
        log.debug("Granularity : " + Granularity.fromGranularity(statSummary.getPeriod().getGranularity()));
        log.debug("Count : " + statSummary.getCount());
        log.debug("LabelValues : " + statSummary.getLabelValues());
        log.debug("================================================");
        long consecutiveLogins = -1L;

        if (Granularity.fromGranularity(statSummary.getPeriod().getGranularity()).equals(Granularity.GRANULARITY_DAY)) {
            LabelValue labelValue = statSummary.getLabelValues()
                    .stream()
                    .filter(lv -> "consecutive-logins".equalsIgnoreCase(lv.getLabel().getName()))
                    .findFirst()
                    .orElse(null);
            consecutiveLogins = (labelValue != null) ? Long.valueOf(labelValue.getValue()) : -1L;
        }


        //statSummary.getPeriod().
        PromoActivityBasic mab = PromoActivityBasic.builder()
            .category(Category.USER)
            .activity(Activity.LOGIN)
            .ownerGuid(playerGuid)
            .domainName(domainName)
            .provider(applicationName)
            .labelValues(
                Stream.of(new String[][] {
                        { ExtraFieldType.GRANULARITY.getType(), String.valueOf(statSummary.getPeriod().getGranularity())},
                        { ExtraFieldType.CONSECUTIVE_LOGINS.getType(), String.valueOf(consecutiveLogins)},
                        { ExtraFieldType.DAYS_OF_WEEK.getType(), String.valueOf(getDayOfTheWeek(statSummary)) },
                }).collect(Collectors.toMap(data -> data[0], data -> data[1])))
            .value(statSummary.getCount())
            .build();
        log.debug("Sending: " + mab);
        missionStatsStream.registerActivity(mab);
    }

    private String labelValue(StatSummary summary, String label) {
        Optional<LabelValue> labelValue = summary.getLabelValues().stream().filter(lv -> label.equalsIgnoreCase(lv.getLabel().getName()))
            .findFirst();

        if (!labelValue.isPresent()) {
            return null;
        }

        return labelValue.get().getValue();
    }
    private String promoCode(StatSummary summary) {
        return labelValue(summary, "bonusCode");
    }

    private String referrerGuid(StatSummary summary) {
        return labelValue(summary, "referrerGuid");
    }

    private Integer getDayOfTheWeek(StatSummary summary) {
        String labelValue = labelValue(summary, "eventTimestamp");
        if (labelValue != null) {
            long timestamp = Long.parseLong(labelValue);
            DateTime dateTime = new DateTime(timestamp);

            return dateTime.dayOfWeek().get();
        }
        return null;
    }
}
