package lithium.service.kyc.repositories;

import lithium.service.kyc.entities.Provider;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProviderRepository extends PagingAndSortingRepository<Provider, Long> {

    default  Provider findOrCreateProvider(String name) {
        Provider provider = findByGuid(name);
        if (provider!= null) return provider;
        provider = lithium.service.kyc.entities.Provider.builder().guid(name).build();
        return save(provider);
    }

    Provider findByGuid(String guid);
}
