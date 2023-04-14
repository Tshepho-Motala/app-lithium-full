package service.casino.provider.cataboom.config;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "service.casino.provider.cataboom")
@Data
public class ServiceCataboomConfigurationProperties {
	
//	@NotNull
//	@Digits(integer = 8, fraction = 0)
///	@Valid
//	Long avatarMaxSize;
}
