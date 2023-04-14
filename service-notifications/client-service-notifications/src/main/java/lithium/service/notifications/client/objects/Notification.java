package lithium.service.notifications.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
	private Long id;
	private int version;
	private Domain domain;
	private Date createdDate;
	private String name;
	private String displayName;
	private String description;
	private String message;
	private boolean systemNotification;
	private List<NotificationChannel> channels;
	private NotificationType notificationType;
}
