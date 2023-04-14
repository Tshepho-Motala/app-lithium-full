package lithium.service.access.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Map;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorizationResult {
	private String providerUrl;
	private boolean successful = false;
	private String message;
	private java.util.List<CheckAuthorizationResult> rawResults = new ArrayList<CheckAuthorizationResult>();
	private String errorMessage;
	private EAuthorizationOutcome rejectOutcome; // The outcome before it was overridden, we need this on the externalValidation API to return a rejectReason when the access rule was rejected based on TIMEOUT or REVIEW
}