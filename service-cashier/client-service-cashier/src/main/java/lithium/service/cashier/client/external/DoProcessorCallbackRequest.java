package lithium.service.cashier.client.external;

import java.util.Locale;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoProcessorCallbackRequest {
	private Map<String, String> parameterMap;
	private Map<String, String[]> headerMap;
	private Object requestBody;
	private Locale locale;
	private String contextPath;
	private String processorCode;
	private String hash;
	
	public String getHeader(String headerName) {
		return (headerMap.get(headerName.toLowerCase())!=null)?headerMap.get(headerName.toLowerCase())[0]:"";
	}
	public String getParameter(String name) {
		return parameterMap.get(name);
	}
}
