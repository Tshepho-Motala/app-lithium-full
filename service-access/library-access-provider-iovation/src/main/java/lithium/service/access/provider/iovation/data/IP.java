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
public class IP {
	/**
	 * The IP Address.
	 */
	private String address;
	/**
	 * Internet service provider of the stated IP address.
	 * Your account must be configured to return this information. The default is to not return this.
	 */
	private String isp;
	/**
	 * Returns location details for the IP address.
	 * Your account must be configured to return this information. The default is to not return this.
	 */
	private Loc loc;
	/**
	 * ISP Organization that the stated IP address is assigned to.
	 * Your account must be configured to return this information. The default is to not return this.
	 */
	private String org;
	/**
	 * Indicator or special attributes for the IP address. This can be satellite or proxy.
	 * Your account must be configured to return this information. The default is to not return this.
	 */
	private String proxy;
	/**
	 * Whether the IP address is the stated address from the end-user, or discovered by iovation.
	 */
	private String source;
}
