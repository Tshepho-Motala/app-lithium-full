package lithium.service.casino.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.services.casino")
@Data
public class ServiceCasinoConfigurationProperties {

//	@NotNull
//	@Digits(integer = 8, fraction = 0)
//	@Valid
//	Long avatarMaxSize;
	
	private TransactionGeoDeviceLabels transactionGeoDeviceLabels = new TransactionGeoDeviceLabels();
	
	@Data
	public static class TransactionGeoDeviceLabels {
		private boolean enabled;
	}
}
