package lithium.service.games.provider.google.rge.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.collect.Lists;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Provider;
import lithium.service.games.provider.google.rge.ServiceGamesProviderGoogleRgeModuleInfo;
import lithium.service.games.provider.google.rge.client.objects.response.AuthenticationResponse;
import lithium.service.games.provider.google.rge.data.entities.Authentication;
import lithium.service.games.provider.google.rge.data.entities.Domain;
import lithium.service.games.provider.google.rge.data.objects.ProviderSettingAuth;
import lithium.service.games.provider.google.rge.data.repositories.AuthenticationRepository;
import lithium.service.games.provider.google.rge.data.repositories.DomainRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class GoogleAuthenticationService {

    @Autowired
    private CachingDomainClientService cachingDomainClientService;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private ProviderClientService providerClientService;

    @Autowired
    private ServiceGamesProviderGoogleRgeModuleInfo moduleInfo;

    @Autowired
    private ConfigurationService configurationService;

    private static final String PROVIDER_PROPERTY_AUTHENTICATION_URL = "AuthenticationUrl";
    private static final String PROVIDER_PROPERTY_USERNAME = "Username";
    private static final String PROVIDER_PROPERTY_PASSWORD = "Password";
    private static final String PROVIDER_PROPERTY_EXPIRATION_DELAY = "ExpirationDelay";
    private static final long DEFAULT_EXPIRATION_DELAY_MILLIS = 120000;

    public String getAccessToken(String domainName) throws Status500InternalServerErrorException,
            Status512ProviderNotConfiguredException, Status550ServiceDomainClientException {
        log.trace("Get access token");
        Authentication authentication = authenticationRepository.findByDomainName(domainName);
        if ((authentication == null) ||
                (authentication != null && authentication.getExpirationDate().before(new Date()))) {
            log.warn("No token available or token is expired, fallback to using HTTP for authentication");
            ProviderSettingAuth providerSettingAuth = configurationService.getAuthPropertiesByDomain(domainName);
            return authenticate(domainName, providerSettingAuth, false);
        }
        return authentication.getAccessToken();
    }

    @Transactional
    public void evictExpiredTokens() {
        log.trace("Evict all tokens");
        Iterable<Authentication> authentications = authenticationRepository.findAll();
        Iterator<Authentication> iterator = authentications.iterator();
        while (iterator.hasNext()) {
            Authentication authentication = iterator.next();
            if (authentication.getExpirationDate().before(new Date())) {
                log.trace("Expired token found for domain=" + authentication.getDomain().getName() + ". Evicting now."
                        + " [expirationDate="+authentication.getExpirationDate()+"]");
                authenticationRepository.deleteByDomainName(authentication.getDomain().getName());
            }
        }
    }

    @TimeThisMethod
    public void authenticateAllDomains() {
        log.trace("Authenticate all domains");
        try {
            SW.start("findAllPlayerDomains");
            List<lithium.service.domain.client.objects.Domain> playerDomains = cachingDomainClientService
                    .getDomainClient().findAllPlayerDomains().getData();
            SW.stop();
            if (playerDomains != null) {
                for (lithium.service.domain.client.objects.Domain domain: playerDomains) {
                    SW.start(domain.getName() + "_findByUrlAndDomainName");
                    Provider provider = providerClientService.findProviderByUrlAndDomainNameIfExists(moduleInfo.getModuleName(), domain.getName());
                    SW.stop();
                    if (provider != null && BooleanUtils.isTrue(provider.getEnabled())) {
                        Authentication authentication = authenticationRepository.findByDomainName(domain.getName());
                        if (authentication == null) {
                            log.trace("Provider " + moduleInfo.getModuleName() + " is setup for " + domain.getName()
                                    + " and there is no token stored. Going to authenticate and store the token.");
                            SW.start("getGoogleAuthenticationConfiguration");
                            ProviderSettingAuth configuration = configurationService.getAuthPropertiesByDomain(domain.getName());
                            SW.stop();
                            SW.start("authenticate");
                            authenticate(domain.getName(), configuration, true);
                            SW.stop();

                        }
                    } else {
                        log.trace("Provider " + moduleInfo.getModuleName() + " is not setup for " + domain.getName());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to authenticate all domains | " + e.getMessage(), e);
        }
    }

    private String authenticate(String domainName, ProviderSettingAuth providerSettingAuth, boolean store) throws Status500InternalServerErrorException {
        log.trace("Authenticating " + domainName);
        AuthenticationResponse authenticationResponse = authenticate(providerSettingAuth);
        if (store) {
            // Shave off 2 minutes from token expiration, so that we can preemptively request a new token prior
            // to expiration.
            authenticationResponse.getExpirationDate().setTime(authenticationResponse.getExpirationDate().getTime() - DEFAULT_EXPIRATION_DELAY_MILLIS);
            Domain domain = domainRepository.findOrCreateByName(domainName, () -> new Domain());

            authenticationRepository.save(Authentication.builder()
                    .domain(domain)
                    .accessToken(authenticationResponse.getAccessToken())
                    .expirationDate(authenticationResponse.getExpirationDate())
                    .build());
            log.info("Google access_token is saved [domain=" + domain + "; expiration_time=" + authenticationResponse.getExpirationDate()
                    + "; moduleName=" + moduleInfo.getModuleName() + "]");
        }
        return authenticationResponse.getAccessToken();
    }

    private AuthenticationResponse authenticate(ProviderSettingAuth providerSettingAuth)
            throws Status500InternalServerErrorException {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String json =  mapper.writeValueAsString(providerSettingAuth);
            json =  json.replace("\\\\n", "\\n");

            GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(json.getBytes()))
                    .createScoped(Lists.newArrayList(("https://www.googleapis.com/auth/cloud-platform")));
            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

            Bucket bucket = storage.get(providerSettingAuth.getBucketName());
            log.debug("Bucket: " + bucket.toString());

            String token = String.valueOf(credentials.getAccessToken().getTokenValue());
            return AuthenticationResponse
                    .builder()
                    .accessToken(token)
                    .expirationDate(credentials.getAccessToken().getExpirationTime())
                    .build();
        } catch (Exception e) {
            String msg = "Google authentication failed";
            log.error(msg + " | " + e.getMessage(), e);
            throw new Status500InternalServerErrorException(msg);
        }
    }

}
