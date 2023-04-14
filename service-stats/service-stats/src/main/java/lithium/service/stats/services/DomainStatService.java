package lithium.service.stats.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.stream.Collectors;
import lithium.service.stats.client.objects.StatSummaryBatch;
import lithium.service.stats.client.stream.event.CompletedStatsEventService;
import lithium.service.stats.data.entities.Domain;
import lithium.service.stats.data.entities.DomainStat;
import lithium.service.stats.data.entities.DomainStatSummary;
import lithium.service.stats.data.entities.Event;
import lithium.service.stats.data.entities.Period;
import lithium.service.stats.data.entities.StatSummary;
import lithium.service.stats.data.entities.Type;
import lithium.service.stats.data.repositories.DomainStatRepository;
import lithium.service.stats.data.repositories.DomainStatSummaryRepository;
import lithium.service.stats.data.repositories.EventRepository;
import lithium.service.stats.data.repositories.TypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class DomainStatService {
	@Autowired DomainService domainService;
	@Autowired DomainStatRepository domainStatRepository;
	@Autowired DomainStatSummaryRepository domainStatSummaryRepository;
	@Autowired EventRepository eventRepository;
	@Autowired PeriodService periodService;
	@Autowired TypeRepository typeRepository;
	@Autowired RabbitTemplate rabbitTemplate;
	@Autowired ModelMapper modelMapper;

	public DomainStat process(Domain domain, Type type, Event event, Date entryDate) {
		DomainStat domainStat = findOrCreateDomainStat(domain, type, event);
		updateDomainSummaries(domainStat, entryDate, event);
		return domainStat;
	}

	private DomainStat findOrCreateDomainStat(Domain domain, Type type, Event event) {
		DomainStat domainStat = domainStatRepository.findByDomainAndTypeAndEvent(domain, type, event);
		if (domainStat == null) {
			domainStat = domainStatRepository.save(
				DomainStat.builder()
				.domain(domain)
				.type(type)
				.event(event)
				.build()
			);
		}
		return domainStat;
	}

	private void updateDomainSummaries(DomainStat domainStat, Date entryDate, Event event) {
		DateTime dtEntryDate = new DateTime(entryDate.getTime());

		List<DomainStatSummary> summaries = new ArrayList<>();
		for (int granularity = Period.GRANULARITY_YEAR; granularity <= Period.GRANULARITY_HOUR; granularity++) {
			Period period = periodService.findOrCreatePeriod(dtEntryDate, domainStat.getDomain(), granularity);
			updateDomainStatSummaryForPeriod(domainStat, period, summaries);
		}

		convertAndSend(event.getName(), summaries);
	}

	private void convertAndSend(String eventName, List<DomainStatSummary> summaries) {
		eventName = eventName.toLowerCase().replaceAll("_", ".");
		List<lithium.service.stats.client.objects.DomainStatSummary> summariesCO = mapToStatSummariesCO(summaries);
		log.debug("Sending to: "+CompletedStatsEventService.FANOUT_EXCHANGE+"| RK: "+CompletedStatsEventService.ROUTING_KEY_PRE+eventName+" Summaries: "+summariesCO);
		rabbitTemplate.convertAndSend(
				CompletedStatsEventService.FANOUT_EXCHANGE,
				CompletedStatsEventService.ROUTING_KEY_PRE+eventName,
				StatSummaryBatch.builder().eventName(eventName).domainStatSummaries(summariesCO).build()
		);
	}

	private List<lithium.service.stats.client.objects.DomainStatSummary> mapToStatSummariesCO(List<DomainStatSummary> summaries) {
		return summaries.stream()
				.map(summary -> modelMapper.map(summary, lithium.service.stats.client.objects.DomainStatSummary.class))
				.collect(Collectors.toList());
	}

	private void updateDomainStatSummaryForPeriod(DomainStat domainStat, Period period, List<DomainStatSummary> summaries) {
		DomainStatSummary domainStatSummary = domainStatSummaryRepository.findByDomainStatAndPeriod(domainStat, period);

		if (domainStatSummary == null) {
			domainStatSummary = domainStatSummaryRepository.save(
				DomainStatSummary.builder()
				.domainStat(domainStat)
				.period(period)
				.count(1L)
				.build()
			);
		} else {
			domainStatSummary.incrementCount();
			domainStatSummary = domainStatSummaryRepository.save(domainStatSummary);
		}
		summaries.add(domainStatSummary);
	}

	public List<DomainStatSummary> find(String domainName, String sType, String sEvent, Date date, Integer granularity) throws Exception {
		Domain domain = domainService.findOrCreate(domainName);
		Type type = typeRepository.findOrCreateByName(sType, () -> new Type());
		Event event = eventRepository.findOrCreateByName(sEvent, () -> new Event());
		DomainStat domainStat = domainStatRepository.findByDomainAndTypeAndEvent(domain, type, event);
		if (domainStat == null) return new ArrayList<>();

		if (date == null) date = new Date();

		List<DomainStatSummary> domainStatSummaries = new ArrayList<>();

		if (granularity == -1) {
			for (int g = Period.GRANULARITY_YEAR; g <= Period.GRANULARITY_HOUR; g++) {
				addDomainStatSummaryToList(domainStat, date, g, domainStatSummaries);
			}
		} else {
			addDomainStatSummaryToList(domainStat, date, granularity, domainStatSummaries);
		}

		return domainStatSummaries;
	}

	private void addDomainStatSummaryToList(DomainStat domainStat, Date date, Integer granularity, List<DomainStatSummary> domainStatSummaries) {
		Period period = periodService.findOrCreatePeriod(new DateTime(date.getTime()), domainStat.getDomain(), granularity);
		DomainStatSummary domainStatSummary = domainStatSummaryRepository.findByDomainStatAndPeriod(domainStat, period);
		if (domainStatSummary != null) domainStatSummaries.add(domainStatSummary);
	}
}
