package lithium.service.domain.client.objects;

import java.io.Serializable;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Domain implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String displayName;
	private String description;
	private Boolean enabled;
	private Boolean deleted;
	private String url;
	private String supportUrl;
	private String supportEmail;
	private Domain parent;
	private Long superId;
	private String superName;
	private Boolean players;
	private Boolean playerDepositLimits;
	private Boolean playerTimeSlotLimits;
	private Boolean playerBalanceLimit;
	private boolean playtimeLimit;
	private String loginAccessRule;
	private String preLoginAccessRule;
	private String signupAccessRule;
	private String firstDepositAccessRule;
	private String preSignupAccessRule;
	private String userDetailsUpdateAccessRule;
	private String ipblockList;
	private String failedLoginIpList;
	private String currency;
	private String currencySymbol;
	private Address physicalAddress;
	private Address postalAddress;
	private BankingDetails bankingDetails;
	private String defaultLocale;
	private Boolean bettingEnabled;
	private DomainRevision current;
	private String defaultCountry;
	private String defaultTimezone;
	
	public String getDefaultLocale() {
		return defaultLocale.replace("_", "-");
	}


	/**
	 * Convenience method for domain setting value lookup
	 * @param settingName The setting name as it appears in UNA
	 * @return The setting value if it exists
	 */
	public Optional<String> findDomainSettingByName(final String settingName) {
		if (getCurrent() == null || getCurrent().getLabelValueList() == null) {
			return Optional.ofNullable(null);
		}
		Optional<DomainRevisionLabelValue> setting = getCurrent().getLabelValueList().stream().filter(dlv -> {
			return dlv.getLabelValue().getLabel().getName().equalsIgnoreCase(settingName);
		}).findFirst();

		if (setting.isPresent()) {
			return Optional.of(setting.get().getLabelValue().getValue());
		} else {
			return Optional.ofNullable(null);
		}
	}
}
