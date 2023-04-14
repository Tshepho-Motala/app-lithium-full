package lithium.service.translate.client.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum Module {
	GLOBAL("GLOBAL", "GLOBAL"),
	UI_NETWORK_ADMIN("UI_NETWORK_ADMIN", "UI-NETWORK-ADMIN"),
	ERROR_DICTIONARY("ERROR_DICTIONARY", "ERROR-DICTIONARY"),
	CASHIER_METHOD_BANK("CASHIER_METHOD_BANK", "CASHIER-METHOD-BANK"),
	CASHIER_METHOD_CHEQUE("CASHIER_METHOD_CHEQUE", "CASHIER-METHOD-CHEQUE"),
	CASHIER_METHOD_IDS("CASHIER_METHOD_IDS", "CASHIER-METHOD-IDS"),
	CASHIER_METHOD_NEOSURF("CASHIER_METHOD_NEOSURF", "CASHIER-METHOD-NEOSURF"),
	CASHIER_METHOD_NETAXEPT("CASHIER_METHOD_NETAXEPT", "CASHIER-METHOD-NETAXEPT"),
	SERVICE_CASHIER("SERVICE_CASHIER", "SERVICE-CASHIER"),
	SERVICE_CASINO_PROVIDER_INCENTIVE("SERVICE_CASINO_PROVIDER_INCENTIVE", "SERVICE-CASINO-PROVIDER-INCENTIVE"),
	SERVICE_CASINO_PROVIDER_SLOTAPI("SERVICE_CASINO_PROVIDER_SLOTAPI", "SERVICE-CASINO-PROVIDER-SLOTAPI"),
	SERVICE_CASINO("SERVICE_CASINO", "SERVICE-CASINO"),
	CASHIER_METHOD_FEDEX_CHEQUE("CASHIER_METHOD_FEDEX_CHEQUE", "CASHIER-METHOD-FEDEX-CHEQUE"),
	SERVICE_DOMAIN("SERVICE_DOMAIN", "SERVICE-DOMAIN"),
	SERVICE_GAMES("SERVICE_GAMES", "SERVICE-DOMAIN"),
	SERVICE_KYC("SERVICE_KYC", "SERVICE-KYC"),
	SERVICE_LIMIT("SERVICE_LIMIT", "SERVICE-LIMIT"),
	SERVICE_USER("SERVICE_USER", "SERVICE-USER");

	@Setter
	@Accessors(fluent = true)
	private String name;

	@Setter
	@Accessors(fluent = true)
	private String name2;

	@JsonCreator
	public static Module fromName(String name) {
		for (Module c : Module.values()) {
			if (c.name == name || c.name2 == name) {
				return c;
			}
		}
		return null;
	}

	public static boolean hasModule(String translationCode) {
		for (Module c : Module.values()) {
			if (translationCode.contains(c.name)  || translationCode.contains(c.name2)) {
				return true;
			}
		}
		return false;
	}

	public String getResponseMessageLocal(MessageSource messageSource, String domainName, String fullTranslationCode, String defaultMessage) {
		return messageSource.getMessage(fullTranslationCode, new Object[]{new Domain(domainName)}, defaultMessage, LocaleContextHolder.getLocale());
	}

	public String getResponseMessageLocal(MessageSource messageSource, String domainName, String fullTranslationCode, String defaultMessage, Object[] args) {
		List<Object> objectList = new ArrayList<>();
		objectList.add(new Domain(domainName));
		objectList.addAll(Arrays.asList(args));
		return messageSource.getMessage(fullTranslationCode, objectList.toArray(), defaultMessage, LocaleContextHolder.getLocale());
	}
}