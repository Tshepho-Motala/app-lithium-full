package lithium.service.casino.provider.twowinpower;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Allows logging outgoing requests and the corresponding responses. Requires
 * the use of a
 * {@link org.springframework.http.client.BufferingClientHttpRequestFactory} to
 * log the body of received responses.
 */
@Slf4j
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {
	private volatile boolean loggedMissingBuffering;
	
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
				sb.append("\r\nRequest body   : "+new String(body, determineCharset(request.getHeaders())));
			}
			log.debug(sb.toString());
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
				sb.append("\r\nStatus code  : "+response.getStatusCode());
				sb.append("\r\nStatus text  : "+response.getStatusText());
				sb.append("\r\nHeaders      : \r\n\t"+
					responseHeaders.entrySet().stream()
					.sorted(Map.Entry.comparingByKey())
					.map(e -> e.getKey()+" - "+String.join(", ", e.getValue()))
					.collect(Collectors.joining("\r\n\t"))
				);
				if (contentLength != 0) {
					if (hasTextBody(responseHeaders) && isBuffered(response)) {
						String bodyText = StreamUtils.copyToString(response.getBody(), determineCharset(responseHeaders));
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
	
	protected boolean hasTextBody(HttpHeaders headers) {
		MediaType contentType = headers.getContentType();
		if (contentType != null) {
			String subtype = contentType.getSubtype();
			return "text".equals(contentType.getType()) || "xml".equals(subtype) || "json".equals(subtype) || "x-www-form-urlencoded".equals(subtype);
		}
		return false;
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