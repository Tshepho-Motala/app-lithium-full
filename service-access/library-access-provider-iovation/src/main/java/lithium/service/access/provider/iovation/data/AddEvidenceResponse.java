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
public class AddEvidenceResponse {
	/**
	 * Unique identifier for the evidence. This ID is necessary to update or delete the evidence later on.
	 */
	private UUID id;
	/**
	 * The evidence type.
	 */
	private String evidenceType;
	/**
	 * The comment that is stored for the evidence.
	 */
	private String comment;
	/**
	 * An entity that describes whether the evidence was applied to a device or an account,
	 * and provides the unique identifier for the device or account.
	 */
	private AppliedTo appliedTo;
}
