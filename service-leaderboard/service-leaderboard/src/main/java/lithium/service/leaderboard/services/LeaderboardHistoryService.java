package lithium.service.leaderboard.services;

import lithium.service.leaderboard.data.entities.Leaderboard;
import lithium.service.leaderboard.data.entities.LeaderboardHistory;
import lithium.service.leaderboard.data.repositories.LeaderboardHistoryRepository;
import lithium.service.leaderboard.data.specifications.LeaderboardHistorySpecifications;
import lombok.extern.slf4j.Slf4j;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Slf4j
@Service
public class LeaderboardHistoryService {
	@Autowired LeaderboardHistoryRepository leaderboardHistoryRepository;
	
	public LeaderboardHistory save(LeaderboardHistory leaderboardHistory) {
		return leaderboardHistoryRepository.save(leaderboardHistory);
	}
	
	private RecurrenceRule rule(String pattern) {
		log.debug("Parsing RRULE: "+pattern);
		RecurrenceRule rule = null;
		try {
			String recurrence = pattern.split("\n")[1];
			String ruleStr = recurrence.split(":")[1];
			rule = new RecurrenceRule(ruleStr);
		} catch (InvalidRecurrenceRuleException e) {
			log.error("Could not determine rule.", e);
		}
		return rule;
	}
	
	private DateTime nextInstance(String rrule, DateTime startDate) {
		DateTime nextInstance = null;
		RecurrenceRule rule = rule(rrule);
		org.dmfs.rfc5545.DateTime start = new org.dmfs.rfc5545.DateTime(TimeZone.getDefault(), startDate.getMillis());
		log.debug("Start RRULE: "+start+ " ::: "+rule.toString());

		RecurrenceRuleIterator it = rule.iterator(start);
		DateTime now = DateTime.now();
		if (startDate.withTimeAtStartOfDay().isBefore(now.withTimeAtStartOfDay())) {
			it.fastForward(now.getMillis());
		}
		if (it.hasNext()) nextInstance = new DateTime(it.nextMillis(), DateTimeZone.getDefault());
		log.debug("nextInstance :: "+nextInstance);
		return nextInstance;
	}
	
	public List<LeaderboardHistory> nextInstances(Leaderboard leaderboard) {
		List<LeaderboardHistory> instanceList = new ArrayList<>();
		RecurrenceRule rule = rule(leaderboard.getRecurrencePattern());
		org.dmfs.rfc5545.DateTime start = new org.dmfs.rfc5545.DateTime(TimeZone.getDefault(), leaderboard.getStartDate().getMillis());
		log.debug("Start RRULE: "+rule.toString()+"  start: "+start+" ("+TimeZone.getDefault().getID()+")");
		
		RecurrenceRuleIterator it = rule.iterator(start);
		Integer instances = rule.getCount();
		if (instances == null) {
			instances = 10;
		} else if (instances > 10) instances = 10;
		
		while (it.hasNext() && (--instances >= 0)) {
			DateTime startDate = new DateTime(it.nextMillis(), DateTimeZone.getDefault());
			startDate = startDate.withTimeAtStartOfDay();
			DateTime endDate = null;
			switch (leaderboard.getDurationGranularity()) {
				case GRANULARITY_DAY:
					endDate = startDate.plusDays(leaderboard.getDurationPeriod());
					break;
				case GRANULARITY_WEEK:
					endDate = startDate.plusWeeks(leaderboard.getDurationPeriod());
					break;
				case GRANULARITY_MONTH:
					endDate = startDate.plusMonths(leaderboard.getDurationPeriod());
					break;
				case GRANULARITY_YEAR:
					endDate = startDate.plusYears(leaderboard.getDurationPeriod());
					break;
				default:
					break;
			}
//			endDate = endDate.minusMillis(1);
			if (startDate.isAfter(DateTime.now())) {
				instanceList.add(
					LeaderboardHistory.builder()
					.startDate(startDate)
					.endDate(endDate)
					.leaderboard(leaderboard)
					.build()
				);
			}
		}
		log.debug("instanceList :: "+instanceList);
		return instanceList;
	}

	public LeaderboardHistory findCurrentOpen(Leaderboard leaderboard) {
		DateTime now = DateTime.now();
		return leaderboardHistoryRepository.findByLeaderboardAndClosedFalseAndLeaderboardEnabledTrueAndStartDateBeforeAndEndDateAfter(leaderboard, now, now);
	}
	
	public LeaderboardHistory add(Leaderboard leaderboard) {
		DateTime nextInstance = nextInstance(leaderboard.getRecurrencePattern(), leaderboard.getStartDate());
		DateTime endDate = DateTime.now();
		
		switch (leaderboard.getDurationGranularity()) {
			case GRANULARITY_DAY:
				endDate = nextInstance.plusDays(leaderboard.getDurationPeriod());
				break;
			case GRANULARITY_WEEK:
				endDate = nextInstance.plusWeeks(leaderboard.getDurationPeriod());
				break;
			case GRANULARITY_MONTH:
				endDate = nextInstance.plusMonths(leaderboard.getDurationPeriod());
				break;
			case GRANULARITY_YEAR:
				endDate = nextInstance.plusYears(leaderboard.getDurationPeriod());
				break;
			default:
				break;
		}
		endDate = endDate.minusMillis(1);
		LeaderboardHistory lbh = exists(leaderboard, nextInstance, endDate);
		if (lbh == null) {
			lbh = create(leaderboard, nextInstance, endDate);
		}
		return lbh;
	}
	
	private LeaderboardHistory exists(Leaderboard leaderboard, DateTime startDate, DateTime endDate) {
		return leaderboardHistoryRepository.findOne(Specification.where(LeaderboardHistorySpecifications.exists(leaderboard, startDate, endDate))).orElse(null);
	}
	public List<LeaderboardHistory> findExpired() {
		return leaderboardHistoryRepository.findByClosedFalseAndLeaderboardEnabledTrueAndEndDateBefore(DateTime.now());
	}
	private LeaderboardHistory create(Leaderboard leaderboard, DateTime startDate, DateTime endDate) {
		return leaderboardHistoryRepository.save(
			LeaderboardHistory.builder()
			.leaderboard(leaderboard)
			.startDate(startDate)
			.endDate(endDate)
			.build()
		);
	}
	
	
	//SEARCHING
	public Page<LeaderboardHistory> searchHistory(Leaderboard leaderboard, String searchValue, Pageable pageable) {
		Specification<LeaderboardHistory> spec = Specification.where(LeaderboardHistorySpecifications.leaderboard(leaderboard.getId()));
//		if ((searchValue != null) && (searchValue.length() > 0)) {
//			Specifications<Leaderboard> s = Specifications.where(LeaderboardSpecifications.any(searchValue));
//			spec = (spec == null)? s: spec.and(s);
//		}
		Page<LeaderboardHistory> result = leaderboardHistoryRepository.findAll(spec, pageable);
		return result;
	}
}