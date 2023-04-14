package lithium.service.access.client.objects;

import java.io.Serializable;

/**
 * Holds possible values the access providers can return to service-access from executing their authorization calls.
 */
public enum EAuthorizationOutcome implements Serializable {
	ACCEPT, // Positive outcome (pass, allow, accept, success)
	REJECT, // Negative outcome (fail, reject, deny, alert)
	REVIEW, // Neutral outcome with possible notification to support staff (Review, refer, notify)
	TIMEOUT,
	NOT_FILLED// When not all user data is filled
}
