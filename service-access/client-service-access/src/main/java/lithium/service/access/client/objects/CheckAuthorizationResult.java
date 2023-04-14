package lithium.service.access.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class CheckAuthorizationResult {
	private String listName;
	private int priority;
	private boolean enabled;
	private boolean accept;
//	private String action;
	private String actionSuccess;
	private String actionFailed;
	private boolean found;
	private Boolean passed;
	private Boolean timeout;
	private Boolean review;
	private String message;
	private String listType;
	private String answerMessage;
	private Map<String, String> data;
	private EAuthorizationOutcome initialOutcome; // The outcome before it was overridden, we need this on the externalValidation API to return a rejectReason when the access rule was rejected based on TIMEOUT or REVIEW
}