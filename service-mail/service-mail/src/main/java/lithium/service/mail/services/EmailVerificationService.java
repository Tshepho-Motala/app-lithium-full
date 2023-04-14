package lithium.service.mail.services;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.mail.client.EmailVerifyClient;
import lithium.service.mail.client.exceptions.Status403InvalidProviderCredentials;
import lithium.service.mail.client.exceptions.Status500ProviderNotConfiguredException;
import lithium.service.mail.client.objects.EmailVerificationResult;
import lithium.service.mail.client.objects.VerifyEmailRequest;
import lithium.service.mail.data.entities.DomainProvider;
import lithium.service.mail.data.entities.DomainProviderProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class EmailVerificationService {

    @Autowired
    DomainProviderService domainProviderService;

    @Autowired
    LithiumServiceClientFactory serviceClientFactory;

    public Response<EmailVerificationResult> verify(VerifyEmailRequest request)
            throws LithiumServiceClientFactoryException,
            Status500ProviderNotConfiguredException, Status401UnAuthorisedException, Status403InvalidProviderCredentials {

        validateRequest();

        DomainProvider domainProvider = provider(request.getDomain());

        List<DomainProviderProperty> providerProperties = domainProviderService.propertiesWithDefaults(domainProvider.getId());
        Map<String,String> props = new HashMap<>();

        for(DomainProviderProperty prop: providerProperties) {
            props.put(prop.getProviderProperty().getName(), prop.getValue());
        }

        request.setProperties(props);

        EmailVerifyClient emailVerifyClient = serviceClientFactory.target(EmailVerifyClient.class, domainProvider.getProvider().getUrl(), true);
        Response<EmailVerificationResult> verificationResponse = emailVerifyClient.verify(request);

        return verificationResponse;
    }

    DomainProvider provider(String domain) throws Status500ProviderNotConfiguredException{
        Iterable<DomainProvider> domainProviders = domainProviderService.findByDomainNameAndProviderType(domain, ProviderConfig.ProviderType.VERIFICATION.type());

        Optional<DomainProvider> domainProvider = StreamSupport.stream(domainProviders.spliterator(), false)
                .sorted(Comparator.comparingLong(DomainProvider::getPriority).reversed())
                .findFirst();


        if(!domainProvider.isPresent()) {
            throw new Status500ProviderNotConfiguredException();
        }
        return domainProvider.get();
    }

    void validateRequest() throws Status401UnAuthorisedException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            oauthApiInternalClient().validateClientAuth(auth);
        }
        catch (Exception e) {
            throw new Status401UnAuthorisedException("Unauthorized");
        }
    }

    OauthApiInternalClient oauthApiInternalClient() throws Status500InternalServerErrorException {
        try {
            return serviceClientFactory.target(OauthApiInternalClient.class, "server-oauth2",false);
        } catch (LithiumServiceClientFactoryException e) {
            throw new Status500InternalServerErrorException(e.getMessage());
        }
    }

}
