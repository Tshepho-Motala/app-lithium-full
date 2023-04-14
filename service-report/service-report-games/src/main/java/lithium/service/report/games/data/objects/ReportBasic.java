package lithium.service.report.games.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReportBasic {
	private String domainName;
	private String name;
	private boolean enabled;
	private String description;
	private boolean allFiltersApplicable;
	private Integer gameDataPeriod;
	private Integer gameDataPeriodOffset;
	private Integer compareXperiods;
	private String period;
	private Integer dayOfWeek;
	private Integer dayOfMonth;
	private Integer hour;
	private Integer minute;
	private String cron;
	private ReportFilterBasic[] filters;
	private ReportActionBasic[] actions;
}