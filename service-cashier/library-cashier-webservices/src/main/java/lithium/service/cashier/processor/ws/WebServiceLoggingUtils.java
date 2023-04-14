package lithium.service.cashier.processor.ws;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ws.WebServiceMessage;
import org.springframework.xml.transform.TransformerObjectSupport;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class WebServiceLoggingUtils extends TransformerObjectSupport {
	private static final ThreadLocal<Map<String, String>> wsDataThreadLocal = ThreadLocal.withInitial(HashMap::new);
	
	private static final String REQUEST = "WebServiceRequest";
	private static final String RESPONSE = "WebServiceResponse";
	
	public static String getRequestData() {
		return wsDataThreadLocal.get().get(REQUEST);
	}
	public static String getResponseData() {
		return wsDataThreadLocal.get().get(RESPONSE);
	}
	
	public static void logRequestMessage(WebServiceMessage webServiceMessage) {
		logMessage(REQUEST, webServiceMessage);
	}
	public static void logResponseMessage(WebServiceMessage webServiceMessage) {
		logMessage(RESPONSE, webServiceMessage);
	}
	
	private static void logMessage(String id, WebServiceMessage webServiceMessage) {
		try {
			ByteArrayTransportOutputStream byteArrayTransportOutputStream = new ByteArrayTransportOutputStream();
			webServiceMessage.writeTo(byteArrayTransportOutputStream);
			
			String httpMessage = new String(byteArrayTransportOutputStream.toByteArray());
			StringBuffer sb = new StringBuffer();
			sb.append("\r\n========================================================================================================");
			sb.append("\r\nRaw "+id+" Data:");
			sb.append("\r\n========================================================================================================");
			sb.append("\r\n"+httpMessage);
			
			wsDataThreadLocal.get().put(id, sb.toString());
			log.debug(sb.toString());
		} catch (Exception e) {
			log.error("Unable to log WebService Message.", e);
		}
	}
}