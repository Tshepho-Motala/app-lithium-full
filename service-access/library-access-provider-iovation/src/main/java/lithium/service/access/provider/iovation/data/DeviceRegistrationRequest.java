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
public class DeviceRegistrationRequest {
	/**
	 * A unique identifier for your iovation account. The field supports UTF-8 up to
	 * 80 Required bytes and the value is case-sensitive.
	 * Example: 1000
	 * NOTE You must include authentication details in your request headers.
	 */
	private String subscriberId;
	
	/**
	 * Your end-user's account identifier.
	 * Required This supports UTF-8 up to 80 bytes. This value is case-sensitive.
	 */
	private String userAccountCode;
	
	 /**
	  * The encrypted string that contains all of the device attributes that iovation is Required able to collect
	  * Format: string 
	  */
	private String blackBox;
	
	/**
	 * Your user's IP address. We use the IP address to help ensure that similarity
	 * Optional matching for paired devices is as accurate as possible. We accept IPv4 only.
	 * Format:string
	 */
	private String userIPAddress;

}
