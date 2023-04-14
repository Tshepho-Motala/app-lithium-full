package lithium.service.access.provider.gamstop.services;

import lithium.client.changelog.ChangeLogClient;
import lithium.client.changelog.exceptions.Status551ServiceChangeLogClientException;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.ExclusionClient;
import lithium.service.report.client.players.PlayersReportClient;
import lithium.service.report.client.players.exceptions.Status551ServiceReportClientException;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiClientService {

    @Autowired
    protected LithiumServiceClientFactory services;

    public ExclusionClient getExclusionService() throws Status551ServiceAccessClientException {
        ExclusionClient ec = null;
        try {
            ec = services.target(ExclusionClient.class, "service-limit", true);
            return ec;
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem accessing Exclusion client", e);
            throw new Status551ServiceAccessClientException(e);
        }
    }

    public UserApiInternalClient getUserService() throws UserClientServiceFactoryException {
        UserApiInternalClient cl = null;
        try {
            cl = services.target(UserApiInternalClient.class, "service-user", true);
            return cl;
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting User client", e);
            throw new UserClientServiceFactoryException("Problem getting user client" + e.getMessage());
        }
    }


    public ProviderClient getProviderService() throws Status550ServiceDomainClientException {
        ProviderClient cl = null;
        try {
            cl = services.target(ProviderClient.class, "service-domain", true);
            return cl;
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting provider client ", e);
            throw new Status550ServiceDomainClientException(e);
        }
    }

    public PlayersReportClient playersReportClient() throws Status551ServiceReportClientException {
        PlayersReportClient reportClient = null;
        try {
            reportClient = services.target(PlayersReportClient.class, "service-report-players", true);
            return reportClient;
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem accessing report service", e);
            throw new Status551ServiceReportClientException(e);
        }
    }

    public ChangeLogClient getChangeLogClient() throws Status551ServiceChangeLogClientException {
        ChangeLogClient cl = null;
        try {
            cl = services.target(ChangeLogClient.class, "service-changelog", true);
            return cl;
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting changelog service", e);
            throw new Status551ServiceChangeLogClientException(e);
        }
    }
}
