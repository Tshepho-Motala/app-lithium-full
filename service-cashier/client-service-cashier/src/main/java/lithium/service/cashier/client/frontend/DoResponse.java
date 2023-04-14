package lithium.service.cashier.client.frontend;

import java.util.Map;

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
public class DoResponse {
	private Long transactionId;
	private String title;
	private String description;
	private Map<String, DoStateFieldGroup> inputFieldGroups;
	private Map<String, DoStateField> outputFields;
	private Integer stage;
	private String state;
	private Boolean error;
	private String errorMessage;
	private String stacktrace;
	private String iframeUrl;
	private String iframeMethod;
	private Map<String, String> iframePostData;
	private String redirectUrl;
	private Boolean mobile;
	private String methodCode;
	private String processorCode;
	private String declineReason;
	private Long amountCents;
	private String iframeWindowTarget; // Setting this to _blank will cause a form post to a blank window
}
