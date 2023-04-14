package lithium.service.cashier.processor.btc.clearcollect.data;

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lithium.service.cashier.client.external.DoProcessorCallbackRequest;
import lithium.service.cashier.processor.btc.clearcollect.util.PayloadUtil;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Data
@Slf4j
public class RequestWithoutSignature<T> {

	private String payload;
	private T data;
		
	public void validate() throws Exception {
		if (payload == null) throw new Exception("Payload is empty");
	}
	
	public void payloadFromHeaders(WebRequest request, Class<T> payloadType) throws JsonParseException, JsonMappingException, IOException, Exception {
		payload = request.getHeader("XPAYLOAD");
		data = PayloadUtil.readObjectFromPayload(payload, payloadType);
	}
	
	public void payloadFromHeaders(DoProcessorCallbackRequest request, Class<T> payloadType) throws JsonParseException, JsonMappingException, IOException, Exception {
		payload = request.getHeader("XPAYLOAD");
		data = PayloadUtil.readObjectFromPayload(payload, payloadType);
	}
	
	public String generatePayload() throws JsonProcessingException {
		this.payload = PayloadUtil.createPayloadFromObject(this.data);
		return this.payload;
	}
	
	public HttpEntity<String> createHttpEntity() throws Exception {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("XPAYLOAD", generatePayload());
		log.info("createHttpEntity " + headers.toString());
		HttpEntity<String> entity = new HttpEntity<>(headers);
		return entity;
	}
	
	public ResponseEntity<Void> createResponseEntity() throws Exception {
		return ResponseEntity.status(HttpStatus.OK).headers(createHttpEntity().getHeaders()).build();
	}
	
}
