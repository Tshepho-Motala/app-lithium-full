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
public class Browser {
	/**
	 * Accepted browser character sets from the HTTP header.
	 */
	private String charset;
	/**
	 * Whether JavaScript cookies are enabled.
	 */
	private Boolean cookiesEnabled;
	/**
	 * Languages from the HTTP header that the browser will accept.
	 */
	private String configuredLanguage;
	/**
	 * Entity describing Flash properties.
	 */
	private Flash flash;
	/**
	 * Browser default language.
	 */
	private String language;
	/**
	 * Browser name.
	 */
	private String type;
	/**
	 * Browser timezone.
	 */
	private String timezone;
	/**
	 * Browser version.
	 */
	private String version;
}
