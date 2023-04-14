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

import lithium.service.cashier.processor.btc.clearcollect.util.HashCalculator;
import lithium.service.cashier.processor.btc.clearcollect.util.PayloadUtil;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString
@Data
@Slf4j
public class RequestOrResponse<T> {

	private String secret;
	private String apiKey;

	private String payload;
	private String signature;
	private T data;
	
	public RequestOrResponse(String secret) {
		this.secret = secret;
	}

	public RequestOrResponse(String secret, String apiKey, T data) {
		this.secret = secret;
		this.apiKey = apiKey;
		this.data = data;
	}
	
	public void validate() throws Exception {
		if (apiKey == null) throw new Exception("API Key is empty");
		if (payload == null) throw new Exception("Payload is empty");
	}

	public void payloadFromHeaders(String providedSignature, Class<T> payloadType) throws Exception {
		log.info("payloadFromHeaders XAPIKEY " + apiKey + " XPAYLOAD " + payload + " XSIGNATURE " + providedSignature);
		T o = PayloadUtil.readObjectFromPayload(payload, payloadType);
		this.data = o;
		validate();
		if (!providedSignature.equals(generateSignature())) throw new Exception("Signature in header does not match calculated signature");
	}
	
	public void payloadFromHeaders(ResponseEntity<?> response, Class<T> payloadType) throws Exception {
		apiKey = response.getHeaders().getFirst("XAPIKEY");
		payload = response.getHeaders().getFirst("XPAYLOAD");
		String XSIGNATURE = response.getHeaders().getFirst("XSIGNATURE");
		payloadFromHeaders(XSIGNATURE, payloadType);
	}
	
	public void payloadFromHeaders(WebRequest request, Class<T> payloadType) throws JsonParseException, JsonMappingException, IOException, Exception {
		apiKey = request.getHeader("XAPIKEY");
		payload = request.getHeader("XPAYLOAD");
		String XSIGNATURE = request.getHeader("XSIGNATURE");
		payloadFromHeaders(XSIGNATURE, payloadType);
	}
	
	public String generateSignature() {
		HashCalculator calculator = new HashCalculator(secret);
		signature = calculator.calculateHash(payload);
		log.info("generateSignature generated " + signature + " using secret " + secret + " with payload " + payload);
		return signature;
	}
	
	public String generatePayload() throws JsonProcessingException {
		this.payload = PayloadUtil.createPayloadFromObject(this.data);
		return this.payload;
	}
	
	public HttpEntity<String> createHttpEntity() throws Exception {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("XAPIKEY", apiKey);
		headers.add("XPAYLOAD", generatePayload());
		headers.add("XSIGNATURE", generateSignature());
		log.info("createHttpEntity " + headers.toString());
		HttpEntity<String> entity = new HttpEntity<>(headers);
		return entity;
	}
	
	public ResponseEntity<Void> createResponseEntity() throws Exception {
		return ResponseEntity.status(HttpStatus.OK).headers(createHttpEntity().getHeaders()).build();
	}
	
}
