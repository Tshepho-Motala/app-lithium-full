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
public class Mobile {
	/**
	 * Includes properties that describe the app.
	 */
	private App app;
	/**
	 * Android only
	 * The brand of device, as determined by the manufacturer or carrier.
	 */
	private String brand;
	/**
	 * Android only
	 * Includes properties that describe the mobile build.
	 */
	private Build build;
	/**
	 * Whether the device is currently plugged in and charging.
	 */
	private Boolean charging;
	/**
	 * Android only
	 * IMEI identifier.
	 */
	private String imei;
	/**
	 * Describes location information received from the device
	 */
	private Location location;
	/**
	 * Android only
	 * Device manufacturer
	 */
	private String manufacturer;
	/**
	 * Device model name and model version. For Apple devices, this refers to the hardware identifier 
	 * (such as iPhone6.1), not the public product model (such as iPhone 6s).
	 */
	private String model;
	/**
	 * iOS only
	 * Physical orientation of the device at the time of the transaction.
	 */
	private String orientation;
	/**
	 * Device screen resolution, in pixels.
	 */
	private String screenResolution;
	/**
	 * Describes system attributes of the mobile device
	 */
	private System system;
}
