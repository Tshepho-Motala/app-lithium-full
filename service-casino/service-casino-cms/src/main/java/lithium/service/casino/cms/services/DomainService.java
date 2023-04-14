package lithium.service.casino.cms.services;

import lithium.service.casino.cms.storage.entities.Domain;
import lithium.service.casino.cms.storage.repositories.DomainRepository;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class DomainService {

    @Autowired
    private DomainRepository repository;

    public Domain findOrCreate(String domainName) throws Status550ServiceDomainClientException {
        return repository.findOrCreateByName(domainName, () -> new Domain());
    }

}
