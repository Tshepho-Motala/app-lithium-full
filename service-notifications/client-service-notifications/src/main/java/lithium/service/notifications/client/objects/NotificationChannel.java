package lithium.service.notifications.client.objects;

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
public class NotificationChannel {
	private Long id;
	private int version;
	private Notification notification;
	private Channel channel;
	private Boolean forced;
	private String templateName;
	private String templateLang;
}
