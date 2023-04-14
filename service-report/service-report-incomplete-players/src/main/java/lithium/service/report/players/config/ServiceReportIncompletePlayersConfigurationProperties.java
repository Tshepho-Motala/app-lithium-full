package lithium.service.report.players.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "lithium.services.report.players")
@Data
public class ServiceReportIncompletePlayersConfigurationProperties {
	private ReportRunRetry reportRunRetry = new ReportRunRetry();
	private ReportRunRestart reportRunRestart = new ReportRunRestart();
	
	@Data
	public static class ReportRunRetry {
		private Long maxNumberOfRetries = 10L;
	}
	
	@Data
	public static class ReportRunRestart {
		private Long restartAfterHours = 12L;
	}
}