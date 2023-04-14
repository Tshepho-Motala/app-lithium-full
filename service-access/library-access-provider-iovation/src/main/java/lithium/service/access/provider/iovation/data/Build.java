package lithium.service.access.provider.iovation.data;

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
public class Build {
	/**
	 * Android only
	 * Typically an internal project name, or may be an actual product name depending on the vendors naming standards.
	 */
	private String device;
	/**
	 * Android only
	 * The actual product name for the device.
	 */
	private String product;
}
