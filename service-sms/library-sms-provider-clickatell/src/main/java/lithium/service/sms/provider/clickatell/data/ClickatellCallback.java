package lithium.service.sms.provider.clickatell.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClickatellCallback {
	private String integrationName;
	private String messageId;
	private String requestId;
	private String clientMessageId;
	private String to;
	private String from;
	private String statusCode;
	private String statusDescription;
	private String timestamp;
}