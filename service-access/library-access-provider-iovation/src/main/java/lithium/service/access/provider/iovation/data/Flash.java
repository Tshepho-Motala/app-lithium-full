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
public class Flash {
	/**
	 * Whether Flash is enabled.
	 */
	private Boolean enabled;
	/**
	 * Whether Flash is installed.
	 */
	private Boolean installed;
	/**
	 * Whether Flash storage is enabled.
	 */
	private Boolean storageEnabled;
	/**
	 * Version of Flash that's installed.
	 */
	private String version;
}
