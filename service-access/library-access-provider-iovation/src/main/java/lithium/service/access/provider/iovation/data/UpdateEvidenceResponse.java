package lithium.service.access.provider.iovation.data;

import java.util.UUID;

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
public class UpdateEvidenceResponse {
	/**
	 * Unique identifier for the evidence.
	 */
	private UUID id;
	/**
	 * The evidence type. This is always in the X-X format, such as 1-4.
	 */
	private String evidenceType;
	/**
	 * The comment that is stored for the evidence.
	 */
	private String comment;
	/**
	 * An entity that describes whether the evidence is associated with a device or an account,
	 * and provides the unique identifier for the device or account.
	 */
	private AppliedTo appliedTo;
}
