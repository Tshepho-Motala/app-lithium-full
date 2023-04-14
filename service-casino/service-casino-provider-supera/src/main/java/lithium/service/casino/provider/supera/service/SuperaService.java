package lithium.service.casino.provider.supera.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.casino.client.CasinoClient;
import lithium.service.casino.provider.supera.SuperaModuleInfo.ConfigProperties;
import lithium.service.casino.provider.supera.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.supera.data.seamless.response.SeamlessResponse;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.ProviderProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SuperaService {
	@Autowired
	protected LithiumServiceClientFactory services;
	
	@Autowired
	protected ModelMapper mapper; 
	
	public BrandsConfigurationBrand getBrandConfiguration(String providerUrl, String domainName) {
		ProviderClient cl = getProviderService();
		Response<Iterable<ProviderProperty>> pp = cl.propertiesByProviderUrlAndDomainName(providerUrl, domainName);
		BrandsConfigurationBrand brandConfiguration = new BrandsConfigurationBrand(); //external system id = providerId as stored in domain config
		for(ProviderProperty p: pp.getData()) {
			if(p.getName().equalsIgnoreCase(ConfigProperties.BASE_URL.getValue())) brandConfiguration.setBaseUrl(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.SALT_KEY.getValue())) brandConfiguration.setSaltKey(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.API_USER.getValue())) brandConfiguration.setApiUser(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.API_PASSWORD.getValue())) brandConfiguration.setApiPassword(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.CURRENCY.getValue())) brandConfiguration.setCurrency(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.API_KEY.getValue())) brandConfiguration.setApiKey(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.IMAGE_URL.getValue())) brandConfiguration.setImageUrl(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.GAME_LIST_URL.getValue())) brandConfiguration.setGameListUrl(p.getValue());
			if(p.getName().equalsIgnoreCase(ConfigProperties.MOCK_FLAG.getValue())) brandConfiguration.setMockActive(Boolean.parseBoolean(p.getValue()));
		}
		
		return brandConfiguration;
	}
	
	private String getParamAsString(HttpServletRequest req, String paramName) {
		String paramValue = req.getParameter(paramName);
		if (paramValue == null) return "null&";
		
		return paramName+"="+paramValue+"&";
	}
	
	public boolean validateRequest(HttpServletRequest req, String saltKey) throws Exception {
		String key = req.getParameter("key") != null ? req.getParameter("key") : null;
		String queryString = "";
		String hashedSaltKeyAndQueryString = null;
		boolean okToProceed = false;

		queryString += getParamAsString(req, "action");
		queryString += getParamAsString(req, "action_type");
		queryString += getParamAsString(req, "amount");
		queryString += getParamAsString(req, "remote_id");
		queryString += getParamAsString(req, "transaction_id");
		queryString += getParamAsString(req, "game_id");
		queryString += getParamAsString(req, "round_id");
		queryString += getParamAsString(req, "session_id");
		queryString += getParamAsString(req, "remote_data");

		queryString = queryString.replaceAll("null&", "");
		queryString = queryString.replaceAll("&$", "");


		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update((saltKey + queryString).getBytes());

		byte[] digest = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b : digest) {
			sb.append(String.format("%02x", b & 0xff));
		}

		hashedSaltKeyAndQueryString = sb.toString();
		okToProceed = hashedSaltKeyAndQueryString.equals(key);

		if(!okToProceed) {
			log.warn("Key did not match! | Key: " + key + " | Generated hash: " + hashedSaltKeyAndQueryString
					+ " | Original query string: " + queryString);
		}

		return okToProceed;
	}

	/**
	 * Produce the salted hash for the provided request parameter string and salt key
	 * @param requestParameterString
	 * @param saltKey
	 * @return
	 * @throws Exception
	 */
	public String buildHashKey(final String requestParameterString, String saltKey) {
		String queryString = requestParameterString;
		String hashedSaltKeyAndQueryString = null;

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update((saltKey + queryString).getBytes());

			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xff));
			}

			hashedSaltKeyAndQueryString = sb.toString();
		} catch (Exception ex) {
			log.error("Problem getting the sha1 message diget instance", ex);
		}

		return hashedSaltKeyAndQueryString;
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
	
	public ProviderClient getProviderService() {
		ProviderClient cl = null;
		try {
			cl = services.target(ProviderClient.class,"service-domain", true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting provider properties", e);
		}
		return cl;
	}

	public String setRequestParams(Map<String, String> map, int mapType) throws UnsupportedEncodingException {
		Iterator<String> iterator = map.keySet().iterator();
		StringBuilder sb = new StringBuilder();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			sb.append("args["+mapType+"]["+key.trim()+"]"+"=");
			sb.append(URLEncoder.encode(((String) map.get(key)).trim(), "utf-8"));
			if (iterator.hasNext()) {
				sb.append("&");
			}
		}
		
		return sb.toString();
	}
}
