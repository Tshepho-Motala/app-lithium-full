package lithium.service.cashier.processor.cc.trustspay.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;

import lithium.service.cashier.processor.ws.SoapEndpointInterceptor;

@Configuration
public class QueryClientConfiguration {
	
	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		// this package must match the package in the <generatePackage> specified in
		// pom.xml
		marshaller.setContextPath("lithium.service.cashier.processor.cc.trustspay.wsdl");
		return marshaller;
	}
	
	@Bean
	public QueryClient quoteClient(Jaxb2Marshaller marshaller) {
		QueryClient client = new QueryClient();
		client.setDefaultUri("https://saferconnectdirect.com/services/customerCheckWS");
		client.setMarshaller(marshaller());
		client.setUnmarshaller(marshaller());
		ClientInterceptor[] interceptors = {new SoapEndpointInterceptor()};
		client.setInterceptors(interceptors);
		return client;
	}

}
