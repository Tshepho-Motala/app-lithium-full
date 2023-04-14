package lithium.service.cdn.cms.services;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class ExternalDomainService {
    @Autowired
    LithiumServiceClientFactory lithiumServiceClientFactory;

    List<Domain> findAllDomain() {
        Iterable<Domain> domains = getDomainClient().findAllDomains().getData();
        return StreamSupport.stream(domains.spliterator(), false)
                .collect(Collectors.toList());
    }

    private DomainClient getDomainClient() {
        try {
            return lithiumServiceClientFactory.target(DomainClient.class, true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Could not load DomainClient", e);
        }

        return null;
    }
}
