package lithium.service.mail;

import lithium.service.client.provider.ProviderConfig;
import lithium.service.mail.data.entities.Provider;
import lithium.service.mail.data.entities.ProviderProperty;
import lithium.service.mail.data.entities.ProviderType;
import lithium.service.mail.data.repositories.ProviderPropertyRepository;
import lithium.service.mail.data.repositories.ProviderRepository;
import lithium.service.mail.data.repositories.ProviderTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ServiceMailInit {

    @Autowired
    ProviderTypeRepository providerTypeRepository;

    void initProviderTypes() {
        ProviderConfig.ProviderType[] mailProviderTypes = new ProviderConfig.ProviderType[]{
                ProviderConfig.ProviderType.DELIVERY,
                ProviderConfig.ProviderType.VERIFICATION
        };

        for (ProviderConfig.ProviderType pt: mailProviderTypes) {
            ProviderType providerType =  providerTypeRepository.findOneByName(pt.type());

            if(providerType == null) {
                providerType = ProviderType.builder().name(pt.type()).build();
                providerTypeRepository.save(providerType);
            }
        }
    }
}
