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
public class System {
	/**
	 * Android only
	 * Whether apps can be installed from sources other than official app stores.
	 */
	private Boolean allowedUnknownStores;
	/**
	 * Android only
	 * Version of the Google Android SDK that the device is using.
	 */
	private String androidSDKLevel;
	/**
	 * Android only
	 * Unique identifier for the system software build.
	 */
	private String buildFingerprint;
	/**
	 * The name of the mobile service provider.
	 */
	private String carrier;
	/**
	 * The country code associated with the mobile service provider.
	 */
	private String carrierCountryCode;
	/**
	 * iOS only
	 * Type of cellular network.
	 */
	private String cellularNetwork;
	/**
	 * Android only
	 * The device's IP hostname.
	 */
	private String hostname;
	/**
	 * Whether the device is jailbroken (iOS) or rooted (Android).
	 */
	private Boolean jailrootDetected;
	/**
	 * The device currency locale.
	 */
	private String localeCurrency;
	/**
	 * System locale and language setting.
	 */
	private String localeLang;
	/**
	 * Android only
	 * The country of the currently active operator, for example for a device that is traveling and connected to a different operator than usual.
	 */
	private String networkOperatorCountry;
	/**
	 * Android only
	 * The name of the currently active operator, for example for a device that is traveling and connected to a different operator than usual.
	 */ 
	private String networkOperatorName;
	/**
	 * Operating system version.
	 */
	private String osVersion;
	/**
	 * User has attempted to hide detection of rooting or jailbreaking.
	 */
	private Boolean rootDetectionDisabled;
	/**
	 * Whether the transaction was initiated from a simulator.
	 */
	private Boolean simulator;
	/**
	 * System uptime since last reboot or power up, in seconds.
	 */
	private String uptime;
	/**
	 * iOS only
	 * Whether the carrier allows voice over IP services for the device.
	 */
	private Boolean voipAllowed;
}
