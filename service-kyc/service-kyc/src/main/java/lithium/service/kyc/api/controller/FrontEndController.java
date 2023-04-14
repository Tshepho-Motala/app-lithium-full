package lithium.service.kyc.api.controller;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.provider.ProviderConfig;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Provider;
import lithium.service.kyc.api.schema.ProviderMethods;
import lithium.service.kyc.exceptions.Status405KycWrongProviderName;
import lithium.service.kyc.exceptions.Status406KycWrongMethodName;
import lithium.service.kyc.provider.exceptions.Status400BadRequestException;
import lithium.service.kyc.provider.exceptions.Status406InvalidVerificationNumberException;
import lithium.service.kyc.provider.exceptions.Status407InvalidVerificationIdException;
import lithium.service.kyc.provider.exceptions.Status424KycVerificationUnsuccessfulException;
import lithium.service.kyc.provider.exceptions.Status425IllegalUserStateException;
import lithium.service.kyc.provider.exceptions.Status427UserKycVerificationLifetimeAttemptsExceeded;
import lithium.service.kyc.provider.exceptions.Status428KycMismatchLastNameException;
import lithium.service.kyc.provider.exceptions.Status429KycMismatchDobException;
import lithium.service.kyc.provider.exceptions.Status504KycProviderEndpointUnavailableException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.exceptions.Status515SignatureCalculationException;
import lithium.service.kyc.provider.exceptions.Status520KycProviderEndpointException;
import lithium.service.kyc.provider.objects.VerifyRequest;
import lithium.service.kyc.schema.KycAttemptsCheckResponse;
import lithium.service.kyc.schema.VerificationStatusResponse;
import lithium.service.kyc.service.KycAttemptsService;
import lithium.service.kyc.service.UpdateVerificationStatusService;
import lithium.service.stats.client.exceptions.Status513StatsServiceUnavailableException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@AllArgsConstructor
@RequestMapping("/frontend/kyc")
@EnableUserApiInternalClientService
@Slf4j
public class FrontEndController {
    @Autowired
    private LithiumServiceClientFactory services;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UpdateVerificationStatusService updateVerificationStatusService;
    @Autowired
    private KycAttemptsService kyCAttemptsService;


    @PostMapping(value = "/verify")
    public ResponseEntity<VerificationStatusResponse> verify(@RequestBody VerifyRequest verifyRequest, LithiumTokenUtil token)
            throws LithiumServiceClientFactoryException, UserNotFoundException, UserClientServiceFactoryException,
            Status520KycProviderEndpointException, Status504KycProviderEndpointUnavailableException, Status515SignatureCalculationException,
            Status407InvalidVerificationIdException, Status406InvalidVerificationNumberException, Status424KycVerificationUnsuccessfulException,
            Status512ProviderNotConfiguredException, Status425IllegalUserStateException, Status400BadRequestException, Status550ServiceDomainClientException,
            Status513StatsServiceUnavailableException, Status427UserKycVerificationLifetimeAttemptsExceeded, Status500InternalServerErrorException,
            Status428KycMismatchLastNameException, Status429KycMismatchDobException {

        VerificationStatusResponse response = updateVerificationStatusService.verify(verifyRequest, token.guid());
        return ResponseEntity.ok(response);
    }


    @GetMapping(value = "/method-list")
    @Cacheable(value = "lithium.service.kyc.api.controller.method-list", key = "#root.args[0]", unless = "#result.isEmpty()")
    public List<ProviderMethods> methodList(Locale locale, LithiumTokenUtil token) throws LithiumServiceClientFactoryException, Status550ServiceDomainClientException {
        ProviderClient pc = services.target(ProviderClient.class, "service-domain", true);
        Iterable<Provider> providers = pc.listAllProvidersByType(ProviderConfig.ProviderType.KYC.type()).getData();

        String domainName = token.domainName();
        List<String> methodComparator = updateVerificationStatusService.getKycMethodOrder(domainName);

        return StreamSupport.stream(providers.spliterator(), false)
                .filter(provider -> provider.getDomain().getName().equals(domainName))
                .filter(Provider::getEnabled)
                .map(provider ->
                        provider.getProperties()
                                .stream()
                                .filter(property -> property.getName().startsWith("METHOD_"))
                                .filter(property -> Boolean.parseBoolean(property.getValue()))
                                .map(providerProperty -> new ProviderMethods(messageSource.getMessage("SERVICE_KYC.KYC_METHOD_NAMES." + providerProperty.getName(), null, locale), providerProperty.getName()))
                                .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .distinct()
                .sorted(Comparator.<ProviderMethods>comparingInt(method -> !methodComparator.contains(method.getCode()) ? Integer.MAX_VALUE : methodComparator.indexOf(method.getCode())).thenComparing(ProviderMethods::getCode))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/method-list-reset")
    @CacheEvict(value = "lithium.service.kyc.api.controller.method-list", allEntries = true)
    public void methodListResetCache() {
        log.info("Method list cache cleaned");
    }

    @GetMapping(value = "/check-for-kyc-attempt")
    public KycAttemptsCheckResponse methodListResetCache(@RequestParam("providerName") String providerName, @RequestParam("methodName") String methodName, LithiumTokenUtil token)
            throws Status405KycWrongProviderName, Status406KycWrongMethodName {
        log.debug("User kyc attempt check requested for user:" + token.guid() + " for provider:" + providerName + " method:" + methodName);
        return kyCAttemptsService.checkUserAttempts(token.guid(), providerName, methodName);
    }
}
