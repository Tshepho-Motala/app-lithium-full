package lithium.service.casino.provider.nucleus.config;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.service.casino.provider.nucleus")
@Data
public class ServiceNucleusConfigurationProperties {
	
//	@NotNull
//	@Digits(integer = 8, fraction = 0)
///	@Valid
//	Long avatarMaxSize;
}
