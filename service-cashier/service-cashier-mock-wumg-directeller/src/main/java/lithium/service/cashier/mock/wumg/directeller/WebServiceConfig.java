package lithium.service.cashier.mock.wumg.directeller;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EnableWs
@Configuration
@EqualsAndHashCode(callSuper=true)
@ConfigurationProperties(prefix = "lithium.service.cashier.mock.wumg.directeller")
public class WebServiceConfig extends WsConfigurerAdapter {
	private String apiUser;
	private String apiPassword;
	
	@Bean
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		servlet.setTransformSchemaLocations(true);
		
		return new ServletRegistrationBean(servlet, "/dtapi/ws/*");
	}
	
	@Bean
	public WebServiceMessageFactory messageFactory() {
		SaajSoapMessageFactory factory = new SaajSoapMessageFactory();
		factory.setSoapVersion(SoapVersion.SOAP_12);
		return factory;
	}
	
	@Bean(name = "helloworld")
	public Wsdl11Definition defaultWsdl11Definition() {
		SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
		wsdl11Definition.setWsdl(new ClassPathResource("/wsdl/dtapi2.wsdl"));
		
		return wsdl11Definition;
	}
}
