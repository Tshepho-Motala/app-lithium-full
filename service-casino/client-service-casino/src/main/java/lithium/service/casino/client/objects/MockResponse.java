package lithium.service.casino.client.objects;


import lombok.*;

import java.io.Serializable;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Builder
@AllArgsConstructor
/**
 * Object for encapsulating relevant information from the mock implementation to generic mock service.
 */
public class MockResponse implements Serializable {
	public static final long serialVersionUID = -1L;

	private EMockResponseStatus mockResponseStatus;
	private String emulatedRequestToLithium;
	private String responseFromLithium;
	private Long executionTimeMs;
	private Long lithiumTransactionId;
}
