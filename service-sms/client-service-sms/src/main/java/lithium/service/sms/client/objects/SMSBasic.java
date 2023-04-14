package lithium.service.sms.client.objects;

import java.util.Map;
import java.util.Set;

import lithium.service.client.objects.placeholders.LegacyPlaceholdersCreator;
import lithium.service.client.objects.placeholders.Placeholder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.util.Objects.nonNull;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SMSBasic {
	private String domainName;
	private String smsTemplateName;
	private String smsTemplateLang;
	private String to;
	private Integer priority;
	private String userGuid;
	private Map<String, String> legacyPlaceholders;
	private Set<Placeholder> placeholders;

	public Set<Placeholder> resolvePlaceholders(){
		if (nonNull(this.placeholders)) {
			return this.placeholders;
		}
		if (nonNull(this.legacyPlaceholders) ) {
			return LegacyPlaceholdersCreator.convertMap(this.legacyPlaceholders);
		}
		return null;
	}
}