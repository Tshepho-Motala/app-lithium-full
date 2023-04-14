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
public class App {
	/**
	 * Application package name.
	 */
	private String bundleId;
	/**
	 * iOS only
	 * Whether a debugger is attached.
	 */
	private Boolean debug;
	/**
	 * The filename of the app that processed the transaction.
	 */
	private String exeName;
	/**
	 * Android only
	 * Unique identifier for the app.
	 */
	private String marketId;
	/**
	 * The external application name.
	 */
	private String name;
	/**
	 * iOS only
	 * The physical orientation of the app (not the device) when the transaction was processed.
	 */
	private String orientation;
	/**
	 * iOS only
	 * The system process name.
	 */
	private String procName;
	/**
	 * Android only
	 * The application signer ID.
	 */
	private String signerId;
	/**
	 * iOS only
	 * ID associated with a specific app producer; the vendor ID should be consistent across all apps by the same vendor.
	 */
	private UUID vendorId;
	/**
	 * Software version of the application.
	 */
	private String version;
}
