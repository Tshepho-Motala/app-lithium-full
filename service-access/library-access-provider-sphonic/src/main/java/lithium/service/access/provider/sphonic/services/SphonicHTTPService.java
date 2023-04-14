package lithium.service.access.provider.sphonic.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.access.provider.sphonic.util.SphonicHttpUtil;
import lithium.util.ObjectToHttpEntity;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpRetryException;

@Slf4j
@Service
public class SphonicHTTPService {

	@Setter
	private RestTemplate rest;

	@Autowired
	public SphonicHTTPService(@Qualifier("lithium.rest") RestTemplateBuilder restTemplateBuilder) {
		// Known issue regarding 4xx responses: https://github.com/spring-projects/spring-framework/issues/14633

		// I would however prefer to keep using the BufferingClientHttpRequestFactory so that we can log response bodies
		// when it is present. It is extremely beneficial in debugging business rule problems.

		// ClientHttpRequestFactory requestFactory = new
		//		HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());

		this.rest = restTemplateBuilder.build();
		// rest.setRequestFactory(requestFactory);
		rest.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				HttpStatus statusCode = response.getStatusCode();
				return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
			}
		});
	}

	public <T> T postForForm(String url, Object request, Class<T> responseType)
			throws Status500InternalServerErrorException {
		try {
			ResponseEntity<T> response = rest.postForEntity(url,
					ObjectToHttpEntity.forPostForm(request), responseType);
			return response.getBody();
		} catch (Exception e) {
			return exceptionForHttp(request, e);
		}
	}

	private String getClientReason(int statusCode) {
		String message = " status = ".concat(String.valueOf(statusCode)).concat(" :");
		switch (statusCode){
			case 400:
				return message + " Bad Request";
			case 401:
				return message + " Unauthorized. Please check credentials on your provider";
			case 403:
				return message + " Forbidden. The credentials or server is not allowed to access Sphonic";
			case 404:
				return message + " Not Found. Please check the URL for the request";
			case 405:
				return message + " Method Not Allowed. Currently only Post methods are allowed";
			case 429:
				return message + " Too Many Requests. The API has hit the limit and should slow down current usage.";
			default:
				return message + " Unknown client error";
		}
	}

	public <T> T postForEntity(String accessToken, String url, Object request, Class<T> responseType)
			throws Status500InternalServerErrorException {
		HttpHeaders headers = SphonicHttpUtil.constructHttpHeaders(accessToken, MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<Object> httpEntity = new HttpEntity<>(request, headers);
		try {
			ResponseEntity<T> responseEntity = rest.postForEntity(url, httpEntity, responseType);
			return responseEntity.getBody();
		} catch (Exception e) {
			return exceptionForHttp(request, e);
		}
	}

	private <T> T exceptionForHttp(Object request, Exception e) throws Status500InternalServerErrorException {
		String msg = "Failed to perform HTTP request to sphonic";
		if (e.getCause() instanceof HttpRetryException) {
			HttpRetryException httpRetryException = (HttpRetryException) e.getCause();
			msg = "Sphonic Client Error: " + getClientReason(httpRetryException.responseCode());
		}
		log.error(msg + " [request=" + request + "] " + e.getMessage(), e);
		throw new Status500InternalServerErrorException(msg);
	}
}
