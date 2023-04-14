package lithium.service.stats.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Arrays;
import java.util.Map;
import lithium.service.stats.client.stream.QueueStatEntry;
import lithium.service.stats.data.entities.Domain;
import lithium.service.stats.data.entities.DomainStat;
import lithium.service.stats.data.entities.Event;
import lithium.service.stats.data.entities.Period;
import lithium.service.stats.data.entities.Stat;
import lithium.service.stats.data.entities.StatEntry;
import lithium.service.stats.data.entities.StatSummary;
import lithium.service.stats.data.entities.Type;
import lithium.service.stats.data.entities.User;
import lithium.service.stats.data.repositories.EventRepository;
import lithium.service.stats.data.repositories.StatEntryRepository;
import lithium.service.stats.data.repositories.StatRepository;
import lithium.service.stats.data.repositories.StatSummaryRepository;
import lithium.service.stats.data.repositories.TypeRepository;
import lithium.service.stats.stream.DomainStatsOutputQueue;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
public class StatService {
	@Autowired private DomainService domainService;
	@Autowired private DomainStatService domainStatService;
	@Autowired private EventRepository eventRepository;
	@Autowired private PeriodService periodService;
	@Autowired private StatEntryRepository statEntryRepository;
	@Autowired private StatEventService statEventService;
	@Autowired private StatRepository statRepository;
	@Autowired private StatSummaryRepository statSummaryRepository;
	@Autowired private TypeRepository typeRepository;
	@Autowired private UserService userService;

	@Autowired
	private DomainStatsOutputQueue domainStatsOutputQueue;

	@Transactional(rollbackFor=Exception.class)
	@Retryable(maxAttempts=10, backoff=@Backoff(maxDelay=50, random=true))
	public void registerDomainStats(QueueStatEntry queueStatEntry) {
		lithium.service.stats.client.objects.StatEntry se = queueStatEntry.getEntry();
		Domain domain = domainService.findOrCreate(se.getStat().getDomain().getName());
		Event event = eventRepository.findOrCreateByName(queueStatEntry.getEvent().toLowerCase(), Event::new);
		Type type = typeRepository.findOrCreateByName(queueStatEntry.getType().toLowerCase(), Type::new);
		DomainStat domainStat = domainStatService.process(domain, type, event, se.getEntryDate());
		log.debug("DomainStat: "+domainStat);
	}
	@Transactional(rollbackFor=Exception.class)
	@Retryable(maxAttempts=10, backoff=@Backoff(maxDelay=50, random=true))
	public boolean register(QueueStatEntry queueStatEntry) throws Exception {
		log.debug("register [queueStatEntry="+queueStatEntry+"]");

		//Sending the domain stats processing to a separate queue
		domainStatsOutputQueue.domainStatsOutput()
				.send(MessageBuilder.withPayload(queueStatEntry).build());

		lithium.service.stats.client.objects.StatEntry se = queueStatEntry.getEntry();
		Domain domain = domainService.findOrCreate(se.getStat().getDomain().getName());
		Event event = eventRepository.findOrCreateByName(queueStatEntry.getEvent().toLowerCase(), Event::new);
		Map<String, String> passThroughInfo = queueStatEntry.getPassThroughInfo();

		if (se.getOwnerGuid() != null && !se.getOwnerGuid().isEmpty()) {
			User owner = userService.findOrCreate(se.getOwnerGuid());
			if (ObjectUtils.nullSafeEquals(event.getName(), lithium.service.stats.client.enums.Event.REGISTRATION_SUCCESS.event())) {
				statEventService.processEvent(
						new DateTime(se.getEntryDate().getTime()),
						event.getName(),
						Arrays.asList(StatSummary.builder().count(1L).stat(Stat.builder().owner(owner).domain(domain).build()).build()),
						passThroughInfo
				);
			} else {
				Stat stat = findOrCreateStat(domain, owner, se.getStat().getName());
				StatEntry statEntry = addStatEntry(se.getEntryDate(), stat, se.getIpAddress(), se.getUserAgent(), owner);
				updateSummariesAndProcessEvent(statEntry, event.getName(), passThroughInfo);
			}
		}

		return true;
	}

	private Stat findOrCreateStat(Domain domain, User owner, String name) {
		Stat stat = statRepository.findByName(name);
		if (stat == null) {
			stat = statRepository.save(
				Stat.builder()
				.domain(domain)
				.owner(owner)
				.name(name)
				.build()
			);
		}
		return stat;
	}

	private StatEntry addStatEntry(Date date, Stat stat, String ipAddress, String userAgent, User owner) {
		return statEntryRepository.save(
			StatEntry.builder()
			.entryDate(date)
			.stat(stat)
			.ipAddress(ipAddress)
			.userAgent(userAgent)
			.owner(owner)
			.build()
		);
	}

	private void updateSummariesAndProcessEvent(StatEntry statEntry, String eventName, Map<String, String> passThroughInfo) throws JsonProcessingException {
		Stat stat = statEntry.getStat();
		DateTime entryDate = new DateTime(statEntry.getEntryDate().getTime());

		List<StatSummary> summaries = new ArrayList<>();
		for (int granularity = Period.GRANULARITY_YEAR; granularity <= Period.GRANULARITY_HOUR; granularity++) {
			Period period = periodService.findOrCreatePeriod(entryDate, stat.getDomain(), granularity);
			updateStatSummaryForPeriod(stat, period, summaries);
		}

		statEventService.processEvent(entryDate, eventName, summaries, passThroughInfo);
	}

	private void updateStatSummaryForPeriod(Stat stat, Period period, List<StatSummary> summaries) {
		StatSummary statSummary = statSummaryRepository.findByPeriodAndStat(period, stat);

		if (statSummary == null) {
			statSummary = statSummaryRepository.save(
				StatSummary.builder()
				.stat(stat)
				.count(1L)
				.period(period)
				.build()
			);
		} else {
			statSummary.incrementCount();
			statSummary = statSummaryRepository.save(statSummary);
			statSummary.setUpdating(true);
		}

		summaries.add(statSummary);
	}
}
