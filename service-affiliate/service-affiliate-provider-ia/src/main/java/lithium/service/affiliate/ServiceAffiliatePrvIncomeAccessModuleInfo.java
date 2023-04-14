package lithium.service.affiliate;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfigProperty;
import lithium.service.role.client.objects.Role;
import lithium.service.role.client.objects.Role.Category;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;

@Component
public class ServiceAffiliatePrvIncomeAccessModuleInfo extends ModuleInfoAdapter {

	public ServiceAffiliatePrvIncomeAccessModuleInfo() {
		super();
		Category c = Category.builder().name("Reporting").description("Reporting access.").build();
		addRole(Role.builder().category(c).name("Full income access reports").role("REPORT_IA").description("Report export for income access").build());
	//Arraylist containing all the relevant properties for the provider
		ArrayList<ProviderConfigProperty> properties= new ArrayList<ProviderConfigProperty>();

		properties.add(ProviderConfigProperty.builder()
			.name(ConfigProperties.SFTP_USERNAME.getValue())
			.required(true)
			.tooltip("The SFTP username for session connection to Income Access")
			.dataType(String.class)
			.version(1)
			.build());

		properties.add(ProviderConfigProperty.builder()
							   .name(ConfigProperties.SFTP_PASSWORD.getValue())
							   .required(true)
							   .tooltip("The SFTP password for session connection to Income Access")
							   .dataType(String.class)
							   .version(1)
							   .build());

		properties.add(ProviderConfigProperty.builder()
			.name(ConfigProperties.SFTP_HOST_NAME.getValue())
			.required(true)
			.tooltip("The SFTP hostname to connect to Income Access")
			.dataType(String.class)
			.version(1)
			.build());

		properties.add(ProviderConfigProperty.builder()
			.name(ConfigProperties.SFTP_HOST_PORT.getValue())
			.required(true)
			.tooltip("The SFTP port to connect to Income Access")
			.dataType(String.class)
			.version(1)
			.build());

		properties.add(ProviderConfigProperty.builder()
			.name(ConfigProperties.REGISTRATION_FILE_URL.getValue())
			.required(true)
			.tooltip("The SFTP url to use when sending registration data to Income Access")
			.dataType(String.class)
			.version(1)
			.build());

		properties.add(ProviderConfigProperty.builder()
			.name(ConfigProperties.SALES_FILE_URL.getValue())
			.required(true)
			.tooltip("The SFTP url to use when sending sales data to Income Access")
			.dataType(String.class)
			.version(1)
			.build());

		properties.add(ProviderConfigProperty.builder()
			.name(ConfigProperties.PRIVATE_KEY.getValue())
			.required(false)
			.tooltip("Base64 encoded private key. Type 'generate' in the value field to let the system generate it for you.")
			.dataType(String.class)
			.version(1)
			.build());

		properties.add(ProviderConfigProperty.builder()
			.name(ConfigProperties.PUBLIC_KEY.getValue())
			.required(false)
			.tooltip("Base64 encoded public key. Type 'generate' in the value field to let the system generate it for you.")
			.dataType(String.class)
			.version(1)
			.build());

		//Add the provider to moduleinfo
		addProvider(ProviderConfig.builder()
					.name(getModuleName())
				.type(ProviderConfig.ProviderType.AFFILIATE)
					.properties(properties)
					.build());
	}

	public static enum ConfigProperties {
		SFTP_USERNAME("sftpUsername"),
		SFTP_HOST_NAME("sftpHostName"),
		SFTP_HOST_PORT("sftpPort"),
		SFTP_PASSWORD("sftpPassword"),
		REGISTRATION_FILE_URL("registrationFileUrl"),
		SALES_FILE_URL("salesFileUrl"),
		PRIVATE_KEY("privateKey"),
		PUBLIC_KEY("publicKey");

		@Getter
		private final String value;

		ConfigProperties(String valueParam) {
			value = valueParam;
		}

		@PostConstruct
		public void init() {
		}
	}

	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		// @formatter:off
		http.authorizeRequests().antMatchers(HttpMethod.GET, "/report/ia/**").access("@lithiumSecurity.hasRole(authentication, 'REPORT_IA')");
		http.authorizeRequests().antMatchers(HttpMethod.POST, "/report/ia/**").access("@lithiumSecurity.hasRole(authentication, 'REPORT_IA')");
		// @formatter:on
	}

}
