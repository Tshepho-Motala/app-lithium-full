package lithium.service.cashier.processor.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor, HttpRequestBodyExtractor {
	private static final String REQUEST = "req";
	private static final String RESPONSE = "res";
	private static final ThreadLocal<Map<String, String>> reqDataThreadLocal = ThreadLocal.withInitial(HashMap::new);
	
	public static String getRequestData() {
		return reqDataThreadLocal.get().get(REQUEST);
	}
	public static String getResponseData() {
		return reqDataThreadLocal.get().get(RESPONSE);
	}
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		traceRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		traceResponse(response);
		return response;
	}
	
	private void traceRequest(HttpRequest request, byte[] body) throws IOException {
		StringBuffer sb = new StringBuffer();
		sb.append("\r\n========================================================================================================");
		sb.append("\r\nRaw Request Data:");
		sb.append("\r\n========================================================================================================");
		sb.append("\r\nURI            : "+request.getURI());
		sb.append("\r\nMethod         : "+request.getMethod());
		sb.append("\r\nHeaders        : \r\n\t"+
			request.getHeaders()
			.entrySet().stream()
			.map(e -> e.getKey()+" - "+String.join(", ", e.getValue()))
			.collect(Collectors.joining("\r\n\t"))
		);
		sb.append("\r\nRequest body   : "+new String(body, "UTF-8"));
		reqDataThreadLocal.get().put(REQUEST, sb.toString());
		log.debug(sb.toString());
	}
	
	private void traceResponse(ClientHttpResponse response) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n========================================================================================================");
		sb.append("\r\nRaw Response Data:");
		sb.append("\r\n========================================================================================================");
		sb.append("\r\nStatus code  : "+response.getRawStatusCode());
		sb.append("\r\nStatus text  : "+response.getStatusText());
		sb.append("\r\nHeaders      : \r\n\t"+
			response.getHeaders()
			.entrySet().stream()
			.map(e -> e.getKey()+" - "+String.join(", ", e.getValue()))
			.collect(Collectors.joining("\r\n\t"))
		);
		extractBody(response).ifPresent(body -> sb.append("\r\nResponse body: ").append(body));
		reqDataThreadLocal.get().put(RESPONSE, sb.toString());
		log.debug(sb.toString());
	}
}
