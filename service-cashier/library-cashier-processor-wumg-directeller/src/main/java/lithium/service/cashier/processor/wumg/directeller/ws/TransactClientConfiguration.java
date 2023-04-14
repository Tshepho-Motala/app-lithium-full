package lithium.service.cashier.processor.wumg.directeller.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import lithium.service.cashier.processor.ws.SoapEndpointInterceptor;

@Configuration
public class TransactClientConfiguration {
	
	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setContextPath("lithium.service.cashier.processor.wumg.directeller.wsdl");
		return marshaller;
	}
	
	@Bean
	public WebServiceMessageFactory messageFactory() {
		SaajSoapMessageFactory factory = new SaajSoapMessageFactory();
		factory.setSoapVersion(SoapVersion.SOAP_12);
		return factory;
	}
	
	@Bean
	public TransactClient transactClient(Jaxb2Marshaller marshaller) {
		TransactClient client = new TransactClient();
		client.setDefaultUri("https://www.transact.ag/application/services/dtapi2.asmx");
		client.setMarshaller(marshaller());
		client.setUnmarshaller(marshaller());
		client.setMessageFactory(messageFactory());
		ClientInterceptor[] interceptors = {new SoapEndpointInterceptor()};
		client.setInterceptors(interceptors);
		return client;
	}
}
