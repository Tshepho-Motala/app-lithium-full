package lithium.service.kyc.provider.onfido;

import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.document.client.EnableDocumentClient;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.notifications.client.stream.EnableNotificationStream;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@LithiumService
@EnableEurekaClient
@EnableLithiumServiceClients
@EnableUserApiInternalClientService
@EnableDocumentClient
@EnableChangeLogService
@EnableNotificationStream
@EnableLimitInternalSystemClient
@EnableCustomHttpErrorCodeExceptions
public class ServiceKycOnfidoProvider extends LithiumServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceKycOnfidoProvider.class, args);
    }
}
