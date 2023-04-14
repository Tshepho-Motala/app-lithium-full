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
public class DeviceRegistrationBody {
	/**
	 * Your iovation subscriber ID code.
	 */
	private String subscriberId;

	/**
	 * Your end-user's account identifier.
	 */
	private String userAccountCode;
	/**
	 * The total length of time, in seconds, that the device will remain registered
	 * with the account. The time this returns depends on the type of time-to-live
	 * (first seen or last seen) as returned by the <code>ttlType</code> attribute.
	 */
	private int ttlSeconds;

	/**
	 * The start point for the length of time that the device remains registered to
	 * a user account.The default time-to-live is 30 days from this start point,
	 * however iovation can set this to any number of days up to a maximum of 180
	 * days.
	 * <code>ttlType</code> can be either of the following:
	 * LAST_SEEN:Time-to- live starts from the time that the registered device was last seen with the account.
	 * For example,if a device was last seen today and time-to- live is set to the default of 30 days,
	 * then the registration will remain in the system for another 30 days or until the device/ account pair
	 * is seen again, whichever comes first.
	 * 
	 * FIRST_SEEN:Time-to- live starts from the time that the device was first registered with the account.
	 * For example,if a device was first paired with an account 15 days ago and time-to-live is set to the default of 30
	 * days, then the registration will remain in the system for another 15 days.
	 */
	private String ttlType;

	/**
	 * The date and time, in GMT, when the device was first registered, in the
	 * following format:
	 */
	private String regDate;

	/**
	 * The type of device, such as MAC.
	 */
	private String deviceType;
}
