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
public class Loc {
	/**
	 * City associated with the IP address.
	 */
	private String city;
	/**
	 * Country associated with the IP address.
	 */
	private String country;
	/**
	 * Country code associated with the IP address.
	 */
	private String countryCode;
	/**
	 * Lattitude associated with the IP address.
	 */
	private Float latitude;
	/**
	 * Longitude associated with the IP address.
	 */
	private Float longitude;
	/**
	 * State/region name associated with the IP address.
	 * To ensure that regions work as expected in business rules, do not abbreviate them.
	 */
	private String region;
}
