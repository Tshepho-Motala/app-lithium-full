package lithium.service.affiliate.provider;

import java.util.ArrayList;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

import lithium.modules.ModuleInfoAdapter;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.client.provider.ProviderConfigProperty;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ServiceAffiliateProviderPapModuleInfo extends ModuleInfoAdapter {
	ServiceAffiliateProviderPapModuleInfo() {
		super();
		//Arraylist containing all the relevant properties for the provider
		ArrayList<ProviderConfigProperty> properties= new ArrayList<ProviderConfigProperty>();
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.BASE_URL.getValue())
				.required(true)
				.tooltip("Base URL used for service calls to pap")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.AUTH_TOKEN_USER.getValue())
				.required(true)
				.tooltip("User used to get a session token")
				.dataType(String.class)
				.version(1)
				.build());

		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.AUTH_TOKEN_PASSWORD.getValue())
				.required(true)
				.tooltip("Password used to get a session token")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.REFERRER_URL.getValue())
				.required(false)
				.tooltip("The referrer URL if one is used")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.COMMISSION_TYPE_ID_BET.getValue())
				.required(true)
				.tooltip("The commission type id configured for bet sales in pap")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.COMMISSION_TYPE_ID_WIN.getValue())
				.required(true)
				.tooltip("The commission type id configured for win sales in pap")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.COMMISSION_TYPE_ID_BONUS.getValue())
				.required(true)
				.tooltip("The commission type id configured for bonus sales in pap")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.COMMISSION_TYPE_ID_FIRST_DEPOSIT.getValue())
				.required(true)
				.tooltip("The commission type id configured for first deposit in pap")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.COMMISSION_TYPE_ID_DEPOSIT.getValue())
				.required(true)
				.tooltip("The commission type id configured for deposit in pap")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.COMMISSION_TYPE_ID_SIGNUP.getValue())
				.required(true)
				.tooltip("The commission type id configured for signup in pap")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.CURRENCY.getValue())
				.required(true)
				.tooltip("The currency that this domain will be working with.")
				.dataType(String.class)
				.version(1)
				.build());
		
		properties.add(ProviderConfigProperty.builder()
				.name(ConfigProperties.EXPORT_NO_BONUS_TRANS.getValue())
				.required(false)
				.tooltip("No bonus play transactions are exported. Only transactions affecting real money balance.")
				.dataType(Boolean.class)
				.version(1)
				.build());
		
		//Add the provider to moduleinfo
		addProvider(ProviderConfig.builder()
				.name(getModuleName())
				.type(ProviderType.AFFILIATE)
				.properties(properties)
				.build());
	}
	
	public static enum ConfigProperties {
		BASE_URL ("baseUrl"),
		AUTH_TOKEN_USER ("authTokenUsername"),
		AUTH_TOKEN_PASSWORD ("authTokenPassword"),
		REFERRER_URL ("referrerUrl"),
		COMMISSION_TYPE_ID_BET ("commissionTypeIdBet"),
		COMMISSION_TYPE_ID_WIN ("commissionTypeIdWin"),
		COMMISSION_TYPE_ID_BONUS ("commissionTypeIdBonus"),
		COMMISSION_TYPE_ID_FIRST_DEPOSIT ("commissionTypeIdFirstDeposit"),
		COMMISSION_TYPE_ID_DEPOSIT ("commissionTypeIdDeposit"),
		COMMISSION_TYPE_ID_SIGNUP ("commissionTypeIdSignup"),
		CURRENCY("currency"),
		EXPORT_NO_BONUS_TRANS("affiliateExportNoBonusTrans");;
		
		@Getter
		private final String value;
		
		ConfigProperties(String valueParam) {
			value = valueParam;
		}
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception {
		super.configureHttpSecurity(http);
//		http.authorizeRequests().antMatchers("/affiliate/player/**").access("@lithiumSecurity.hasRole(authentication, 'AFFILIATE_PLAYER_MODIFY, AFFILIATE_PLAYER_VIEW')");
//		http.authorizeRequests().antMatchers("/affiliates/{domainName}/**").access("@lithiumSecurity.hasDomainRole(authentication, {domainName}, 'AFFILIATE_MODIFY, AFFILIATE_VIEW')");
//		http.authorizeRequests().antMatchers("/casino/bonus/manual/**").access("@lithiumSecurity.hasRole(authentication, 'MANUAL_BONUS_ALLOCATION')");
//		http.authorizeRequests().antMatchers("/casino/bonus/**").authenticated();
//		http.authorizeRequests().antMatchers("/casino/**").access("@lithiumSecurity.authenticatedSystem(authentication)");
		http.authorizeRequests().anyRequest().permitAll();
	}
}
