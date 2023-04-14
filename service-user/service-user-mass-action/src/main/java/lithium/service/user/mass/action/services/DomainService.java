package lithium.service.user.mass.action.services;

import lithium.service.user.mass.action.data.entities.Domain;
import lithium.service.user.mass.action.data.repositories.DomainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainService {
    @Autowired
    private DomainRepository domainRepository;

    public Domain findOrCreate(String name) {
        Domain domain = domainRepository.findByName(name.toLowerCase());
        if (domain == null) {
            domain = Domain.builder().name(name).build();
            domainRepository.save(domain);
        }
        return domain;
    }
}