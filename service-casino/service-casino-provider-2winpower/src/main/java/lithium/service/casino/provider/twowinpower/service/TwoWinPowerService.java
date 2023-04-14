package lithium.service.casino.provider.twowinpower.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.WebRequest;

import lithium.service.Response;
import lithium.service.casino.client.CasinoClient;
import lithium.service.casino.client.CasinoFrbClient;
import lithium.service.casino.provider.twowinpower.TwoWinPowerModuleInfo.ConfigProperties;
import lithium.service.casino.provider.twowinpower.config.APIAuthentication;
import lithium.service.casino.provider.twowinpower.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.twowinpower.util.HashCalculator;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.games.client.GamesClient;
import lithium.service.mail.client.MailClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TwoWinPowerService {
	@Autowired
	protected LithiumServiceClientFactory services;
	@Autowired
	protected ModelMapper mapper;
	
	public BrandsConfigurationBrand getBrandConfiguration(String providerUrl, String domainName) {
		ProviderClient cl = getProviderService();
		Response<Iterable<ProviderProperty>> pp = cl.propertiesByProviderUrlAndDomainName(providerUrl, domainName);
		log.trace("ProviderProperties : "+pp);
		BrandsConfigurationBrand brandConfiguration = new BrandsConfigurationBrand(); //external system id = providerId as stored in domain config
		for (ProviderProperty p: pp.getData()) {
			if (p.getName().equalsIgnoreCase(ConfigProperties.BASE_URL.getValue())) brandConfiguration.setBaseUrl(p.getValue());
			if (p.getName().equalsIgnoreCase(ConfigProperties.MERCHANT_ID.getValue())) brandConfiguration.setMerchantId(p.getValue());
			if (p.getName().equalsIgnoreCase(ConfigProperties.MERCHANT_KEY.getValue())) brandConfiguration.setMerchantKey(p.getValue());
			if (p.getName().equalsIgnoreCase(ConfigProperties.CURRENCY.getValue())) brandConfiguration.setCurrency(p.getValue());
			if (p.getName().equalsIgnoreCase(ConfigProperties.API_KEY.getValue())) brandConfiguration.setApiKey(p.getValue());
			if (p.getName().equalsIgnoreCase(ConfigProperties.IMAGE_URL.getValue())) brandConfiguration.setImageUrl(p.getValue());
		}
		log.trace("brandConfiguration :: "+brandConfiguration);
		return brandConfiguration;
	}
	
	public String playerGuid(WebRequest webRequest) {
		try {
			return URLDecoder.decode(webRequest.getParameter("player_id"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new UnsupportedOperationException(e);
		}
	}
	
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
	public MultiValueMap<String, String> urlEncodeUTF8Map(MultiValueMap<String, String> map) {
		map.forEach((k,v) -> {
			map.set(k, urlEncodeUTF8(v.get(0)));
		});
		return map;
	}
	
	public String buildSignature(String merchantKey, WebRequest webRequest) {
		Map<String, String[]> parameters = webRequest.getParameterMap();
		Map<String, String> map = new HashMap<>();
		
		map.put(APIAuthentication.HEADER_X_MERCHANT_ID, webRequest.getHeader(APIAuthentication.HEADER_X_MERCHANT_ID));
		map.put(APIAuthentication.HEADER_X_TIMESTAMP, webRequest.getHeader(APIAuthentication.HEADER_X_TIMESTAMP));
		map.put(APIAuthentication.HEADER_X_NONCE, webRequest.getHeader(APIAuthentication.HEADER_X_NONCE));
		
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
		map.put(APIAuthentication.HEADER_X_MERCHANT_ID, headers.getFirst(APIAuthentication.HEADER_X_MERCHANT_ID));
		map.put(APIAuthentication.HEADER_X_TIMESTAMP, headers.getFirst(APIAuthentication.HEADER_X_TIMESTAMP));
		map.put(APIAuthentication.HEADER_X_NONCE, headers.getFirst(APIAuthentication.HEADER_X_NONCE));
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
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8, MediaType.TEXT_PLAIN));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add(APIAuthentication.HEADER_X_MERCHANT_ID, merchantId);
		headers.add(APIAuthentication.HEADER_X_TIMESTAMP, now+"");
		headers.add(APIAuthentication.HEADER_X_NONCE, hc.calculateMd5());
		headers.add(APIAuthentication.HEADER_X_SIGN, buildSignature(merchantKey, headers, parameters));
		
		return headers;
	}
	public HttpHeaders buildHeaders(String merchantId, String merchantKey) {
		return buildHeaders(merchantId, merchantKey, Collections.emptyMap());
	}
	
	public lithium.service.games.client.objects.Game getGame(String domainName, String gameGuid) {
		Response<lithium.service.games.client.objects.Game> game;
		try {
			game = getGamesService().findByGuidAndDomainName(domainName, gameGuid);
			if (game.isSuccessful()) return game.getData();
		} catch (Exception e) {
			log.error("Could not retrieve game details for ("+domainName+") : "+gameGuid, e);
		}
		return null;
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
	
	public GamesClient getGamesService() {
		GamesClient gc = null;
		try {
			gc = services.target(GamesClient.class,"service-games", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting casino frb service", e);
		}
		
		return gc;
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
	
	public ProviderClient getProviderService() {
		ProviderClient cl = null;
		try {
			log.trace("Retrieving ProviderClient from service-domain.");
			cl = services.target(ProviderClient.class, "SERVICE-DOMAIN", true);
			log.trace("Retrieved ProviderClient from service-domain. :: "+cl);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting provider properties", e);
		}
		return cl;
	}
}
