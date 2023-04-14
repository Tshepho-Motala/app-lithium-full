package lithium.service.casino.provider.betsoft.config;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.service.casino.provider.betsoft")
@Data
public class ServiceBetsoftConfigurationProperties {
	
//	@NotNull
//	@Digits(integer = 8, fraction = 0)
///	@Valid
//	Long avatarMaxSize;
}
