package lithium.service.mail.client.objects;

import java.util.Map;
import java.util.Set;

import lithium.service.client.objects.placeholders.LegacyPlaceholdersCreator;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.user.client.objects.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailData {
	private String domainName;
	private String emailTemplateName;
	private String emailTemplateLang;
	private String to;
	private int priority;
	private String userGuid;
	private Map<String, String> legacyPlaceholders;
	private Set<Placeholder> placeholders;
	private String attachmentName;
	private byte[] attachmentData;
	private String authorGuid;

	public Set<Placeholder> resolvePlaceholders(){
		if (nonNull(this.placeholders)) {
			return this.placeholders;
		}
		if (nonNull(this.legacyPlaceholders) ) {
			return LegacyPlaceholdersCreator.convertMap(this.legacyPlaceholders);
		}
		return null;
	}

	public static class EmailDataBuilder {
		private String authorGuid;

		public EmailDataBuilder authorSystem(){
		 this.authorGuid = User.SYSTEM_GUID;
		 return this;
		}
	}
}