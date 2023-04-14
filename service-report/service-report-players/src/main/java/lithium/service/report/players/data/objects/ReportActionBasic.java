package lithium.service.report.players.data.objects;

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
public class ReportActionBasic {
	private String actionType;
	private String[] recipients;
	private String emailTemplate;
	private String smsTemplate;
	private String notificationName;
}