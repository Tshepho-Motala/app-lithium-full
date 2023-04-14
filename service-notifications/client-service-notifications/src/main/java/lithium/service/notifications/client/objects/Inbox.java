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
public class Inbox {
	private Long id;
	private int version;
	private Domain domain;
	private User user;
	private Notification notification;
	private Date createdDate;
	private String message;
	private Date sentDate;
	private Boolean read;
	private Date readDate;
	private Date lastReadDate;
	private List<InboxMessagePlaceholderReplacement> phReplacements;
}
