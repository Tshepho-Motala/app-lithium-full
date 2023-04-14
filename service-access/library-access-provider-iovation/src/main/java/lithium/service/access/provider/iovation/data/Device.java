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
public class Device {
	/**
	 * iovation identifier for the device.
	 */
	private Long alias;
	/**
	 * Entity that includes blackbox age properties.
	 */
	private BlackboxMetaData blackboxMetaData;
	/**
	 * Entity that includes browser properties.
	 */
	private Browser browser;
	/**
	 * Date/time the device was first seen by iovation. 
	 */
	@DateTimeFormat(iso=ISO.DATE_TIME)
	private Date firstSeen;
	/**
	 * Whether the device has ever been seen by iovation.
	 */
	private Boolean isNew;
	/**
	 * Contains device characteristics for mobile devices.
	 */
	private Mobile mobile;
	/**
	 * Operating system of the device.
	 */
	private String os;
	/**
	 * For iovation ClearKey auth checks, contains device match results. This is only returned if you are a
	 * ClearKey subscriber and the ruleset includes the Registered Account / Device Pair rule.
	 */
	private RegistrationResult registrationResult;
	/**
	 * The screen resolution.
	 */
	private String screen;
	/**
	 * The hardware device make.
	 */
	private String type;
}
