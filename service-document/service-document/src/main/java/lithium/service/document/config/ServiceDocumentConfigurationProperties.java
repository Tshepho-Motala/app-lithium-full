package lithium.service.document.config;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "lithium.service.document")
@Data
public class ServiceDocumentConfigurationProperties {
//	
//	@NotNull
//	@Digits(integer = 8, fraction = 0)
//	@Valid
//	Long avatarMaxSize;
}
