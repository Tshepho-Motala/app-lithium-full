package lithium.service.stats.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lithium.service.gateway.client.stream.GatewayExchangeStream;
import lithium.service.stats.client.objects.StatSummaryBatch;
import lithium.service.stats.client.stream.StatsStream;
import lithium.service.stats.client.stream.event.CompletedStatsEventService;
import lithium.service.stats.data.entities.Label;
import lithium.service.stats.data.entities.LabelValue;
import lithium.service.stats.data.entities.Period;
import lithium.service.stats.data.entities.StatLabelValue;
import lithium.service.stats.data.entities.StatSummary;
import lithium.service.stats.data.repositories.StatSummaryRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StatEventService {
	@Autowired private GatewayExchangeStream gatewayExchangeStream;
	@Autowired private ModelMapper modelMapper;
	@Autowired private ObjectMapper objectMapper;
	@Autowired private PeriodService periodService;
	@Autowired private StatLabelValueService statLabelValueService;
	@Autowired private StatsStream statsStream;
	@Autowired private StatSummaryRepository statSummaryRepository;

	@Autowired private RabbitTemplate rabbitTemplate;

	private static final String EVENT_LOGIN_SUCCESS = "login-success";

	private static final String LABEL_CONSECUTIVE_LOGINS = "consecutive-logins";

	public void processEvent(DateTime entryDate, String eventName, List<StatSummary> summaries, Map<String, String> passThroughInfo) throws JsonProcessingException {
		log.debug("processEvent [entryDate="+entryDate+", eventName="+eventName+", summaries="+summaries);

		summaries.stream().forEach(summary -> {
			summary.addLabelValue(LabelValue.builder()
					.label(Label.builder().name("eventTimestamp").build())
					.value(String.valueOf(entryDate.getMillis()))
					.build());
			if (passThroughInfo != null) {
				passThroughInfo.forEach((k, v) -> summary.addLabelValue(LabelValue.builder().label(Label.builder().name(k).build()).value(String.valueOf(v)).build()));
			}
			if (EVENT_LOGIN_SUCCESS.equalsIgnoreCase(eventName)) { //TODO: this needs to be revisited.
				if (summary.getPeriod().isDay()) {
					addConsecutiveLoginStatLabelValue(entryDate, summary);
				}
				log.debug(""+summary.toShortString());
			}
		});

		List<lithium.service.stats.client.objects.StatSummary> summariesCO = mapToStatSummariesCO(summaries);
		statsStream.sendLoginStats(summariesCO);
		//TODO: remove the above completely once comps engine in use and legacy bonus framework retired. (All the dead code associated with this) (Will affect login triggers on current old bonus framework.)
		log.debug("Sending to: "+CompletedStatsEventService.FANOUT_EXCHANGE+"| RK: "+CompletedStatsEventService.ROUTING_KEY_PRE+eventName.toLowerCase().replaceAll("_", ".")+" Summaries: "+summariesCO);
		rabbitTemplate.convertAndSend(
				CompletedStatsEventService.FANOUT_EXCHANGE,
				CompletedStatsEventService.ROUTING_KEY_PRE+eventName.toLowerCase().replaceAll("_", "."),
				StatSummaryBatch.builder().eventName(eventName).statSummaries(summariesCO).build()
		);
		//TODO: Commented out, needs to be re-evaluated.
//		streamToGatewayExchange("playerroom/"+summaries.get(0).getStat().getOwner().guid(), EVENT_LOGIN_SUCCESS,
//			objectMapper.writeValueAsString(summariesCO));
	}

	private void addConsecutiveLoginStatLabelValue(DateTime entryDate, StatSummary summary) {
		StatLabelValue consecutiveLoginStatLabelValue = null;

		if (summary.isUpdating()) {
			consecutiveLoginStatLabelValue = statLabelValueService.findStatLabelValue(summary.getStat(), LABEL_CONSECUTIVE_LOGINS);
		} else {
			Period periodYesterday = periodService.findOrCreatePeriod(entryDate.minusDays(1), summary.getStat().getDomain(),
				summary.getPeriod().getGranularity());
			StatSummary statSummaryYesterday = statSummaryRepository.findByPeriodAndStat(periodYesterday, summary.getStat());

			if (statSummaryYesterday != null) {
				consecutiveLoginStatLabelValue = statLabelValueService.findStatLabelValue(summary.getStat(),
					LABEL_CONSECUTIVE_LOGINS);
				LabelValue consecutiveLabelValue = consecutiveLoginStatLabelValue.getLabelValue();
				Long value = Long.valueOf(consecutiveLabelValue.getValue());
				String newValue = String.valueOf(value + 1);
				consecutiveLoginStatLabelValue = statLabelValueService.updateOrCreateStatLabelValue(summary.getStat(),
					LABEL_CONSECUTIVE_LOGINS, newValue);
			} else {
				consecutiveLoginStatLabelValue = statLabelValueService.updateOrCreateStatLabelValue(summary.getStat(),
					LABEL_CONSECUTIVE_LOGINS, "1");
			}
		}

		summary.addLabelValue(consecutiveLoginStatLabelValue.getLabelValue());
	}

	private List<lithium.service.stats.client.objects.StatSummary> mapToStatSummariesCO(List<StatSummary> summaries) {
		return summaries.stream()
			.map(summary -> modelMapper.map(summary, lithium.service.stats.client.objects.StatSummary.class))
			.toList();
	}

	private void streamToGatewayExchange(String target, String event, String data) throws JsonProcessingException {
		gatewayExchangeStream.process(target, event, data);
	}
}
