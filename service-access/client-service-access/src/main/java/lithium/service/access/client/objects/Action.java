package lithium.service.access.client.objects;

import java.io.Serializable;

/**
 * Holds possible actions for rules outcomes.
 */
public enum Action implements Serializable {
	ACCEPT, // Stop processing and return success
	REJECT, // Stop processing and return failure
	CONTINUE, // Continue processing
	ACCEPT_AND_VERIFY // Stop processing, flag player as externally verified then return success
}
