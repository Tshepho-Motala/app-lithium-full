package lithium.rest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Allows logging outgoing requests and the corresponding responses. Requires
 * the use of a
 * {@link org.springframework.http.client.BufferingClientHttpRequestFactory} to
 * log the body of received responses.
 */
@Slf4j
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {
	private volatile boolean loggedMissingBuffering;
	private LithiumRestConfigurationProperties properties;

	private static final String REQUEST = "request";
	private static final String RESPONSE = "response";

	public LoggingRequestInterceptor(LithiumRestConfigurationProperties properties) {
		this.properties = properties;
	}
	public LoggingRequestInterceptor() {}
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		logRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		logResponse(request, response);
		return response;
	}
	
	protected void logRequest(HttpRequest request, byte[] body) throws IOException {
		if (log.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			sb.append("\r\n========================================================================================================");
			sb.append("\r\nRaw Request Data:");
			sb.append("\r\n========================================================================================================");
			sb.append("\r\nURI            : "+request.getURI());
			sb.append("\r\nMethod         : "+request.getMethod());
			sb.append("\r\nHeaders        : \r\n\t"+
				request.getHeaders()
				.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.map(e -> e.getKey()+" - "+String.join(", ", e.getValue()))
				.collect(Collectors.joining("\r\n\t"))
			);
			if (body.length > 0 && hasTextBody(request.getHeaders())) {
				String requestBody = new String(body, determineCharset(request.getHeaders()));
				requestBody = obfuscateFields(request.getHeaders(), requestBody, REQUEST);
				sb.append("\r\nRequest body   : "+requestBody);
			}
			log.debug(sb.toString());
		}
	}

	private String resolveHttpStatusCode(int rawHttpStatusCode) {
		try {
			return HttpStatus.valueOf(rawHttpStatusCode).toString();
		} catch (Exception exception) {
			return ""+rawHttpStatusCode;
		}
	}

	protected void logResponse(HttpRequest request, ClientHttpResponse response) {
		if (log.isDebugEnabled()) {
			try {
				StringBuilder sb = new StringBuilder();
				HttpHeaders responseHeaders = response.getHeaders();
				long contentLength = responseHeaders.getContentLength();
				sb.append("\r\n========================================================================================================");
				sb.append("\r\nRaw Response Data:");
				sb.append("\r\n========================================================================================================");
				sb.append("\r\nStatus code  : "+ resolveHttpStatusCode(response.getRawStatusCode()));
				sb.append("\r\nStatus text  : "+response.getStatusText());
				sb.append("\r\nHeaders      : \r\n\t"+
					responseHeaders.entrySet().stream()
					.sorted(Map.Entry.comparingByKey())
					.map(e -> e.getKey()+" - "+String.join(", ", e.getValue()))
					.collect(Collectors.joining("\r\n\t"))
				);
				if (contentLength != 0) {
					// Known issue regarding 4xx responses: https://github.com/spring-projects/spring-framework/issues/14633
					// The !response.getStatusCode().is4xxClientError() check will prevent erroring out here, so that
					// we at least have some logging to indicate the problem.
					if (!response.getStatusCode().is4xxClientError() &&
							hasTextBody(responseHeaders) &&
							isBuffered(response)) {
						String bodyText = StreamUtils.copyToString(response.getBody(), determineCharset(responseHeaders));
						bodyText = obfuscateFields(responseHeaders, bodyText, RESPONSE);
						sb.append("\r\nResponse body: "+bodyText);
					} else {
						if (contentLength == -1) {
							sb.append("\r\nContent Length  : unknown ");
						} else {
							sb.append("\r\nContent Length  : "+contentLength);
						}
						MediaType contentType = responseHeaders.getContentType();
						if (contentType != null) {
							sb.append("\r\nContent Type : "+contentType);
						} else {
							sb.append("\r\nContent Type : unknown");
						}
					}
				}
				sb.append("\r\n========================================================================================================");
				log.debug(sb.toString());
				
			} catch (IOException e) {
				log.warn("Failed to log response for {} request to {}", request.getMethod(), request.getURI(), e);
			}
		}
	}

	// This only works for JSON at the moment. Expand on it if needed.
	private String obfuscateFields(HttpHeaders headers, String body, String type) {
		if (properties != null) {
			List<String> fields = new ArrayList<>();

			switch (type) {
				case REQUEST:
					if (properties.getObfuscateFieldsRequest() != null)
						fields.addAll(properties.getObfuscateFieldsRequest());
					break;
				case RESPONSE:
					if (properties.getObfuscateFieldsResponse() != null)
						fields.addAll(properties.getObfuscateFieldsResponse());
					break;
				default: throw new IllegalArgumentException("Unhandled type " + type);
			}

			if (fields != null && !fields.isEmpty()) {
				String contentSubtype = getContentSubtype(headers);
				if (contentSubtype != null && contentSubtype.contentEquals("json")) {
					for (String field : fields) {
						body = body.replaceAll("(?<=\""+field+"\":)[^,^}]+",
								"****");
					}
				} else {
					type = type.toLowerCase();
					type = StringUtils.capitalize(type);
					log.trace(type + " field obfuscation is not implemented for " + contentSubtype);
				}
			}
		}

		return body;
	}
	
	protected boolean hasTextBody(HttpHeaders headers) {
		String contentSubtype = getContentSubtype(headers);
		if (contentSubtype != null) {
			return "text".equals(contentSubtype) ||
					"xml".equals(contentSubtype) ||
					"json".equals(contentSubtype) ||
					"x-www-form-urlencoded".equals(contentSubtype);
		}
		return false;
	}

	protected String getContentSubtype(HttpHeaders headers) {
		MediaType contentType = headers.getContentType();
		if (contentType != null) {
			return contentType.getSubtype();
		}
		return null;
	}
	
	protected Charset determineCharset(HttpHeaders headers) {
		MediaType contentType = headers.getContentType();
		if (contentType != null) {
			try {
				Charset charSet = contentType.getCharset();
				if (charSet != null) {
					return charSet;
				}
			} catch (UnsupportedCharsetException e) {
				// ignore
			}
		}
		return StandardCharsets.UTF_8;
	}
	
	private boolean isBuffered(ClientHttpResponse response) {
		boolean buffered = "org.springframework.http.client.BufferingClientHttpResponseWrapper".equals(response.getClass().getName());
		if (!buffered && !loggedMissingBuffering) {
			log.warn("Can't log HTTP response bodies, as you haven't configured the RestTemplate with a BufferingClientHttpRequestFactory");
			loggedMissingBuffering = true;
		}
		return buffered;
	}
	
}
