package lithium.service.accounting.domain.summary.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@ConfigurationProperties(prefix = "lithium.services.accounting.domain.summary")
@Configuration
public class Properties {
	private Reconciliation reconciliation = new Reconciliation();

	@Data
	public static class Reconciliation {
		private boolean enabled;
		private String cron;
		private String seedDateFormat;
		private String seedDateValue;
		private boolean logErrorOnMismatchedDataEnabled;
		private boolean updateMismatchedDataEnabled;
		private int dataFetchSizePerType;
	}
}
