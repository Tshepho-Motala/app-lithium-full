package lithium.service.domain.data.objects;

import lombok.Data;

@Data
public class DomainBasic {
	String name;
	String displayName;
	String description;
	Boolean players;
  Long parentId;
	String supportUrl;
	String supportEmail;
	String url;
	String preSignupAccessRule;
	String signupAccessRule;
	String loginAccessRule;
	String userDetailsUpdateAccessRule;
	String firstDepositAccessRule;
	String currency;
	String currencySymbol;
	String defaultLocale;
	String defaultCountry;
	String timeout;
}
