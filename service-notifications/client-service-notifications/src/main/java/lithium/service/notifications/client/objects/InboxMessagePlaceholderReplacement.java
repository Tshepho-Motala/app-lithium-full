package lithium.service.notifications.client.objects;

import lithium.service.client.objects.placeholders.Placeholder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InboxMessagePlaceholderReplacement {
	private Long id;
	private Inbox inbox;
	@Setter(AccessLevel.NONE)
	private String key;
	@Setter(AccessLevel.NONE)
	private String value;

	public static InboxMessagePlaceholderReplacement fromPlaceholder(Placeholder placeholder){
		InboxMessagePlaceholderReplacement placeholderReplacement = new InboxMessagePlaceholderReplacement();
		placeholderReplacement.key = placeholder.getKey();
		placeholderReplacement.value = placeholder.getValue();
		return placeholderReplacement;
	}

}
