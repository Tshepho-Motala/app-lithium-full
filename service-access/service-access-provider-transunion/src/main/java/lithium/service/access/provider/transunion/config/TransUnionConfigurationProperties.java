package lithium.service.access.provider.transunion.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "lithium.services.access.kyctransunion")
@Configuration
public class TransUnionConfigurationProperties {


    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {

        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setPackagesToScan("lithium.service.access.provider.transunion.shema.response.success", "lithium.service.access.provider.transunion.shema.response.passwordupdate");
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("jaxb.formatted.output", false);
        jaxb2Marshaller.setMarshallerProperties(props);
        return jaxb2Marshaller;
    }
}
