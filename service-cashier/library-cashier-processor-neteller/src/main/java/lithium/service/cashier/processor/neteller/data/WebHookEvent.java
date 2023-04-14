package lithium.service.cashier.processor.neteller.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.service.cashier.processor.neteller.data.enums.WebHookEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class WebHookEvent {
	private WebHookPayload payload;
	private WebHookEventType eventType;
	private String attemptNumber;
	private String resourceId;
	private Date eventDate;
	private List<Link> links;
	private String mode;
	private String eventName;
}
