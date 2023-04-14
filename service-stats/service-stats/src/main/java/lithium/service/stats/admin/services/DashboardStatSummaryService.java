package lithium.service.stats.admin.services;

import lithium.service.stats.data.entities.DomainStatSummary;
import lithium.service.stats.data.entities.Period;
import lithium.service.stats.objects.DashboardStatSummary;
import lithium.service.stats.services.DomainStatService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DashboardStatSummaryService {
	@Autowired DomainStatService domainStatService;

	public DashboardStatSummary compile(String domainName, String type, String event, Integer granularity) throws Exception {
		int last = 2;

		DateTime now = new DateTime();
		ArrayList<DateTime> dates = new ArrayList<>();

		if (granularity == Period.GRANULARITY_YEAR) {
			DateTime date = new DateTime(now.getYear(), 1, 1, 0, 0);
			dates.add(date);
			for (int i = 0; i < last; i ++) {
				dates.add(date.minusYears(i + 1));
			}
		} else if (granularity == Period.GRANULARITY_MONTH) {
			DateTime date = new DateTime(now.getYear(), now.getMonthOfYear(), 1, 0, 0);
			dates.add(date);
			for (int i = 0; i < last; i ++) {
				dates.add(date.minusMonths(i + 1));
			}
		} else if (granularity == Period.GRANULARITY_DAY) {
			DateTime date = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0);
			dates.add(date);
			for (int i = 0; i < last; i ++) {
				dates.add(date.minusDays(i + 1));
			}
		} else if (granularity == Period.GRANULARITY_WEEK) {
			DateTime date = new DateTime(now.getYear(), 1, 1, 0, 0).withWeekyear(now.getWeekyear()).withWeekOfWeekyear(now.getWeekOfWeekyear()).withDayOfWeek(1);
			dates.add(date);
			for (int i = 0; i < last; i ++) {
				dates.add(date.minusWeeks(i + 1));
			}
		} else if (granularity == Period.GRANULARITY_HOUR) {
			throw new Exception("Hourly granularity not yet implemented!");
		} else {
			throw new RuntimeException("Invalid granularity: " + granularity);
		}

		Map<Integer, DomainStatSummary> domainStatSummaryMap = new LinkedHashMap<>();

		for (int i = 0; i < last + 1; i ++) {
			List<DomainStatSummary> domainStatSummaryList = domainStatService.find(domainName, type, event, dates.get(i).toDate(), granularity);
			DomainStatSummary domainStatSummary = null;
			if (domainStatSummaryList != null && !domainStatSummaryList.isEmpty()) domainStatSummary = domainStatSummaryList.get(0);
			domainStatSummaryMap.put(i, domainStatSummary);
		}

		// 0 = countcurrent
		// 1 = countlast1
		// 2 = countlast2
		if (log.isDebugEnabled()) {
			domainStatSummaryMap.forEach((k, v) -> {
				log.debug("k: " + k + ", v: " + v);
			});
		}

		DashboardStatSummary dashboardStatSummary = DashboardStatSummary.builder()
		.countcurrent((domainStatSummaryMap.get(0) != null) ? domainStatSummaryMap.get(0).getCount() : 0)
		.countlast1((domainStatSummaryMap.get(1) != null) ? domainStatSummaryMap.get(1).getCount() : 0)
		.countlast2((domainStatSummaryMap.get(2) != null) ? domainStatSummaryMap.get(2).getCount() : 0)
		.build();

		List<DomainStatSummary> domainStatSummaryTotalList = domainStatService.find(domainName, type, event, null, Period.GRANULARITY_TOTAL);
		DomainStatSummary domainStatSummaryTotal = null;
		if (domainStatSummaryTotalList != null && !domainStatSummaryTotalList.isEmpty()) domainStatSummaryTotal = domainStatSummaryTotalList.get(0);

		dashboardStatSummary.setCounttotal((domainStatSummaryTotal != null) ? domainStatSummaryTotal.getCount() : 0);

		return dashboardStatSummary;
	}
}
