package lithium.server.security;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.hazelcast.EnableHazelcastClient;
import lithium.metrics.EnableLithiumMetrics;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.translate.client.EnableTranslationsService;
import lithium.service.translate.client.stream.EnableTranslationsStream;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.service.user.client.service.EnableUserPlayTimeClientService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.SessionAttributes;

@SpringBootApplication
@EnableResourceServer
@EnableTranslationsService
@EnableTranslationsStream
@EnableDiscoveryClient
@EnableDomainClient
@EnableLithiumMetrics
@EnableLithiumServiceClients
@EnableLimitInternalSystemClient
@EnableUserApiInternalClientService
@EnableUserPlayTimeClientService
@EnableHazelcastClient
@SessionAttributes("authorizationRequest")
public class ServerSecurityApplication {

    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServerSecurityApplication.class, args);
    }
}
