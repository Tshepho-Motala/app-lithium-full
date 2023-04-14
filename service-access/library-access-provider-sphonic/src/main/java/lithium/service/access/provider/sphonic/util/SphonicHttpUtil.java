package lithium.service.access.provider.sphonic.util;

import org.springframework.http.HttpHeaders;

public class SphonicHttpUtil {
	public static HttpHeaders constructHttpHeaders(String accessToken, String contentType) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-Type", contentType);
		return headers;
	}
}
