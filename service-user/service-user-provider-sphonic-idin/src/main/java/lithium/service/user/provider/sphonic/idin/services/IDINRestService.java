package lithium.service.user.provider.sphonic.idin.services;

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
public class IDINRestService {

    private final RestTemplateBuilder restTemplateBuilder;

    @Autowired
    public IDINRestService(@Qualifier("lithium.rest") RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    private ClientHttpRequestFactory getHttpClientRequestFactory(int connectionRequestTimeout, int connectionTimeout, int socketTimeout) {
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        CloseableHttpClient closeableHttpClient = HttpClientBuilder
                .create()
                .setDefaultRequestConfig(requestConfig)
                .build();
        return new HttpComponentsClientHttpRequestFactory(closeableHttpClient);
    }

    public RestTemplate createRestTemplate(int connectionRequestTimeout, int connectionTimeout, int socketTimeout) {
        ClientHttpRequestFactory clientHttpRequestFactory = getHttpClientRequestFactory(connectionRequestTimeout, connectionTimeout, socketTimeout);
        RestTemplate restTemplate = restTemplateBuilder.build();
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory));
        return restTemplate;
    }
}
