package lithium.service.games.services;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ProviderService {

    @Autowired
    private LithiumServiceClientFactory services;

    private ProviderClient getProviderClient() throws LithiumServiceClientFactoryException {
        ProviderClient providerClient = services.target(ProviderClient.class,"service-domain", true);
        return providerClient;
    }

    public List<Provider> getProvidersByDomainAndType(String domainName, String type) throws LithiumServiceClientFactoryException {
        List<Provider> providers = new ArrayList<>();
        getProviderClient().listByDomainAndType(domainName, type).getData()
                .forEach(provider -> providers.add(provider));
        return providers;
    }
}
