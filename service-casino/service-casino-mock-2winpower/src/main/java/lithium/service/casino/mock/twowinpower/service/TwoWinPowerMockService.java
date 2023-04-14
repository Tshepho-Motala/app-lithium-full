package lithium.service.casino.mock.twowinpower.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

import lithium.service.casino.client.CasinoClient;
import lithium.service.casino.client.CasinoFrbClient;
import lithium.service.casino.provider.twowinpower.util.HashCalculator;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.mail.client.MailClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TwoWinPowerMockService {
	public static final String HEADER_X_MERCHANT_ID = "X-Merchant-Id";
	public static final String HEADER_X_TIMESTAMP = "X-Timestamp";
	public static final String HEADER_X_NONCE = "X-Nonce";
	public static final String HEADER_X_SIGN = "X-Sign";
	
	@Autowired
	protected LithiumServiceClientFactory services;
	@Autowired
	protected ModelMapper mapper; 
	
	private String urlEncodeUTF8(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(e);
		}
	}
	
	private String urlEncodeUTF8(Map<?, ?> map) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			if (sb.length() > 0) {
				sb.append("&");
			}
			sb.append(String.format("%s=%s", urlEncodeUTF8(entry.getKey().toString()), urlEncodeUTF8(entry.getValue().toString())));
		}
		return sb.toString();
	}
	
	public String buildSignature(String merchantKey, WebRequest webRequest) {
		Map<String, String[]> parameters = webRequest.getParameterMap();
		Map<String, String> map = new HashMap<>();
		
		map.put(HEADER_X_MERCHANT_ID, webRequest.getHeader(HEADER_X_MERCHANT_ID));
		map.put(HEADER_X_TIMESTAMP, webRequest.getHeader(HEADER_X_TIMESTAMP));
		map.put(HEADER_X_NONCE, webRequest.getHeader(HEADER_X_NONCE));
		
		parameters.forEach((key, value) -> {
			map.put(key, value[0]);
		});
		
		Map<String, String> mapSorted = map.entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		String url = urlEncodeUTF8(mapSorted);
		
		HashCalculator hc = new HashCalculator(merchantKey);
		
		return hc.calculateSha1(url);
	}
	
	public String buildSignature(String merchantKey, HttpHeaders headers, Map<String, String> parameters) {
		Map<String, String> map = new HashMap<>();
		map.put(HEADER_X_MERCHANT_ID, headers.getFirst(HEADER_X_MERCHANT_ID));
		map.put(HEADER_X_TIMESTAMP, headers.getFirst(HEADER_X_TIMESTAMP));
		map.put(HEADER_X_NONCE, headers.getFirst(HEADER_X_NONCE));
		map.putAll(parameters);
		
		Map<String, String> mapSorted = map.entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		String url = urlEncodeUTF8(mapSorted);
		
		HashCalculator hc = new HashCalculator(merchantKey);
		
		return hc.calculateSha1(url);
	}
	
	public HttpHeaders buildHeaders(String merchantId, String merchantKey, Map<String, String> parameters) {
		long now = DateTime.now().getMillis();
		HashCalculator hc = new HashCalculator(merchantKey);
		hc.addItem(now);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("X-Merchant-Id", merchantId);
		headers.add("X-Timestamp", now+"");
		headers.add("X-Nonce", hc.calculateMd5());
		headers.add("X-Sign", buildSignature(merchantKey, headers, parameters));
		
		return headers;
	}
	public HttpHeaders buildHeaders(String merchantId, String merchantKey) {
		return buildHeaders(merchantId, merchantKey, Collections.emptyMap());
	}
	
	public CasinoClient getCasinoService() {
		CasinoClient cl = null;
		try {
			cl = services.target(CasinoClient.class,"service-casino", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting casino service", e);
		}
		
		return cl;
	}
	
	public CasinoFrbClient getCasinoFrbService() {
		CasinoFrbClient cl = null;
		try {
			cl = services.target(CasinoFrbClient.class,"service-casino", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting casino frb service", e);
		}
		
		return cl;
	}
	
	public MailClient getMailService() {
		MailClient cl = null;
		try {
			cl = services.target(MailClient.class,"service-mail", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting mail service", e);
		}
		
		return cl;
	}
}