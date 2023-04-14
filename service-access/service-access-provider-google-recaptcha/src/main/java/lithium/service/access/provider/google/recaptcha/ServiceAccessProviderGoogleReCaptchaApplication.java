package lithium.service.access.provider.google.recaptcha;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@EnableDomainClient
@EnableUserApiInternalClientService
@LithiumService
public class ServiceAccessProviderGoogleReCaptchaApplication extends LithiumServiceApplication {
    public static void main(String[] args) {
        LithiumShutdownSpringApplication.run(ServiceAccessProviderGoogleReCaptchaApplication.class, args);
    }
}
