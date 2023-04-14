package lithium.service.cdn.cms.services;

import lithium.service.cdn.cms.data.repositories.DomainRepository;
import lithium.service.domain.client.objects.Domain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ServiceCdnCmsInit {
    @Autowired
    private ExternalDomainService externalDomainService;

    @Autowired
    private DomainRepository domainRepository;

    public void initDomains() {
        List<Domain> domains =  externalDomainService.findAllDomain();

        for(Domain domain: domains) {
            lithium.service.cdn.cms.data.entities.Domain localDomain = domainRepository.findByName(domain.getName());

            if(localDomain != null) {
                continue;
            }

            localDomain = lithium.service.cdn.cms.data.entities.Domain.builder()
                    .name(domain.getName())
                    .build();

            domainRepository.save(localDomain);
        }
    }
}
