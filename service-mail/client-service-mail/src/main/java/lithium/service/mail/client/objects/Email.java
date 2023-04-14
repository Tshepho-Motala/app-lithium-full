package lithium.service.mail.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Email {
	private Date createdDate;
	private Date sentDate;
	private String from;
	private String to;
	private String subject;
	private String body;
	private int priority;
	private String userGuid;
	private String bcc;
}