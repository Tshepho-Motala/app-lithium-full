package lithium.service.access.provider.iovation.data;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

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
public class Location {
	/**
	 * Altitude reported by the device.
	 */
	private Float altitude;
	/**
	 * Whether location services are enabled.
	 */
	private Boolean enabled;
	/**
	 * Latitude reported by the device.
	 */
	private Float latitude;
	/**
	 * Longitude reported by the device.
	 */
	private Float longitude;
	/**
	 * Geolocation timestamp.
	 */
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date timestamp;
	/**
	 * Device timezone as determined by the OS.
	 */
	private String timezone;
}
