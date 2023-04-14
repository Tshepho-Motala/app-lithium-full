package lithium.service.kyc.provider.paystack.services;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {
    @Autowired
    @Qualifier("lithium.rest")
    private RestTemplateBuilder builder;

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

    public RestTemplate restTemplate(Integer connectTimeout, Integer connectionRequestTimeout, Integer socketTimeout) {
        ClientHttpRequestFactory clientHttpRequestFactory = getClientHttpRequestFactory(
                connectTimeout,
                connectionRequestTimeout,
                socketTimeout);
        RestTemplate restTemplate = builder.build();
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory));
        return restTemplate;
    }
}
