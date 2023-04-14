package lithium.service.mail.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SystemEmailData {
	private String domainName;
	private String subject;
	private String body;
	private String to;
	private int priority;
	private String userGuid;
	private String attachmentName;
	private byte[] attachmentData;
}