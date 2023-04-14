package lithium.service.casino.provider.livedealer.config;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.service.casino.provider.livedealer")
@Data
public class ServiceLivedealerConfigurationProperties {
	
//	@NotNull
//	@Digits(integer = 8, fraction = 0)
///	@Valid
//	Long avatarMaxSize;
}
