package lithium.service.access.provider.kycgbg.config;

import lithium.service.access.provider.kycgbg.ws.SoapEndpointInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@Configuration
public class KycGbgClientConfig {
	@Autowired KycGbgConfigurationProperties properties;

	@Bean
	Jaxb2Marshaller jaxb2Marshaller() {
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
		jaxb2Marshaller.setContextPath("com.id3global.id3gws._2013._04");
		return jaxb2Marshaller;
	}

	@Bean
	public WebServiceMessageFactory messageFactory() {
		SaajSoapMessageFactory factory = new SaajSoapMessageFactory();
		factory.setSoapVersion(SoapVersion.SOAP_11);
		return factory;
	}
//
//	@Bean
//	public HttpClient httpClient() {
//		RequestConfig requestConfig = RequestConfig.custom()
//				.setConnectionRequestTimeout(properties.getReadTimeout())
//				.setConnectTimeout(properties.getConnectionTimeout())
//				.setSocketTimeout(properties.getReadTimeout())
//				.setCookieSpec(CookieSpecs.STANDARD)
//				.build();
//
//		CloseableHttpClient httpClient = HttpClientBuilder
//				.create()
//				.setDefaultRequestConfig(requestConfig)
//				.build();
//
//		return httpClient;
//	}
//
//	@Bean
//	public HttpComponentsMessageSender httpComponentsMessageSender() {
//		HttpComponentsMessageSender httpComponentsMessageSender = new HttpComponentsMessageSender(httpClient());
////		httpComponentsMessageSender.setReadTimeout(properties.getReadTimeout());
////		httpComponentsMessageSender.setConnectionTimeout(properties.getConnectionTimeout());
//		return httpComponentsMessageSender;
//	}
//
	@Bean
	public WebServiceTemplate webServiceTemplate() {
		WebServiceTemplate adapter = new WebServiceTemplate();
		adapter.setDefaultUri("https://www.id3global.com/ID3gWS/ID3global.svc?xsd=xsd0");
		adapter.setMarshaller(jaxb2Marshaller());
		adapter.setUnmarshaller(jaxb2Marshaller());

		adapter.setMessageFactory(messageFactory());
		ClientInterceptor[] interceptors = {new SoapEndpointInterceptor()};
		adapter.setInterceptors(interceptors);
//		adapter.setMessageSender(httpComponentsMessageSender());

		return adapter;
	}
}
