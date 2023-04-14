package lithium.service.notifications.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Popup {
	private Long inboxItemId;
	private String templateName;
	private String templateLang;
}
