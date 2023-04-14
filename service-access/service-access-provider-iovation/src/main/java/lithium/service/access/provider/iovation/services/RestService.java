package lithium.service.access.provider.iovation.services;

import java.util.Map;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.service.access.provider.iovation.config.Config;
import lithium.service.access.provider.iovation.config.IovationConfigurationProperties;

@Service
public class RestService {
	@Autowired @Qualifier("lithium.service.access.povider.iovation.RestTemplate") RestTemplate restTemplate;
	@Autowired AccessProviderIovationService accessProviderIovationService;
	@Autowired IovationConfigurationProperties properties;
	
	private ClientHttpRequestFactory getClientHttpRequestFactory(Integer connectTimeout, Integer connectionRequestTimeout, Integer socketTimeout) {
		RequestConfig config = RequestConfig.custom()
		.setConnectTimeout(connectTimeout)
		.setConnectionRequestTimeout(connectionRequestTimeout)
		.setSocketTimeout(socketTimeout)
		.build();
		CloseableHttpClient client = HttpClientBuilder
		.create()
		.setDefaultRequestConfig(config)
		.build();
		return new HttpComponentsClientHttpRequestFactory(client);
	}
	
	public RestTemplate restTemplate(String domainName) {
		Map<String, String> domainProperties = accessProviderIovationService.getProviderPropertiesMap("service-access-provider-iovation", domainName);
		Integer connectTimeout = Integer.parseInt(domainProperties.getOrDefault(Config.CONNECT_TIMEOUT.property(), String.valueOf(properties.getConnectTimeout())));
		Integer connectionRequestTimeout = Integer.parseInt(domainProperties.getOrDefault(Config.CONNECTION_REQUEST_TIMEOUT.property(), String.valueOf(properties.getConnectionRequestTimeout())));
		Integer socketTimeout = Integer.parseInt(domainProperties.getOrDefault(Config.SOCKET_TIMEOUT.property(), String.valueOf(properties.getSocketTimeout())));
		
		ClientHttpRequestFactory clientHttpRequestFactory = getClientHttpRequestFactory(connectTimeout, connectionRequestTimeout, socketTimeout);
		
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory));
		
		return restTemplate;
	}
}
