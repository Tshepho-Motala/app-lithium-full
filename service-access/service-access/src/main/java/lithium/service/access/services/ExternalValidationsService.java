package lithium.service.access.services;

import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status404AccessRuleNotFoundException;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.access.client.AccessService;
import lithium.service.access.client.objects.AuthorizationRequest;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.access.client.objects.CheckAuthorizationResult;
import lithium.service.access.controllers.external.schemas.ExternalValidationRequest;
import lithium.service.access.controllers.external.schemas.ExternalValidationResponse;
import lithium.service.access.data.entities.AccessRule;
import lithium.service.access.data.entities.ExternalList;
import lithium.service.access.data.repositories.AccessRuleRepository;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.ProviderClientService;
import lithium.service.domain.client.exceptions.Status474DomainProviderDisabledException;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Provider;
import lithium.service.translate.client.objects.RegistrationError;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.util.HmacSha256HashCalculator;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@Slf4j
public class ExternalValidationsService {

  @Autowired AccessRuleRepository accessRuleRepository;
  @Autowired ProviderClientService providerClientService;
  @Autowired MessageSource messageSource;
  @Autowired AuthorizationService authorizationService;
  @Autowired AccessService accessService;
  @Autowired CachingDomainClientService cachingDomainClientService;
  @Autowired ModelMapper modelMapper;

  public ExternalValidationResponse doExternalValidations(ExternalValidationRequest externalValidationRequest, String domainName, String accessRuleName, String remoteAddr, String userAgent, Boolean test)
      throws Status400BadRequestException, Status404AccessRuleNotFoundException, Status470HashInvalidException, Status474DomainProviderDisabledException, Status512ProviderNotConfiguredException, Status500InternalServerErrorException {

    String firstName = externalValidationRequest.getFirstName();
    String lastName = externalValidationRequest.getLastName();
    String sha256 = externalValidationRequest.getSha256();
    String email = externalValidationRequest.getEmail();
    Map<String, String> additionalData = externalValidationRequest.getAdditionalData();

    if (Stream.of(lastName, sha256, externalValidationRequest.getDobYear(), externalValidationRequest.getDobMonth(), externalValidationRequest.getDobDay(), email).anyMatch(Objects::isNull)) {
      throw new Status400BadRequestException(RegistrationError.DATA_VALIDATION_ERROR.getResponseMessageLocal(messageSource, domainName));
    }

    String preSharedKey = cachingDomainClientService.getDomainSetting(domainName, DomainSettings.ACCESS_HASH_PASSWORD);
    if (preSharedKey.trim().isEmpty()) {
      throw new Status470HashInvalidException(RegistrationError.INVALID_HASH.getResponseMessageLocal(messageSource, domainName));
    }
    validateSha256(firstName, lastName, preSharedKey, sha256, domainName);

    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    PlayerBasic playerBasic = modelMapper.map(externalValidationRequest, PlayerBasic.class);

    AccessRule accessRule = accessRuleRepository.findByDomainNameAndNameIgnoreCase(domainName, accessRuleName);
    if (accessRule == null) {
      throw new Status404AccessRuleNotFoundException(RegistrationError.ACCESS_RULE_NOT_FOUND.getResponseMessageLocal(messageSource, domainName));
    }
    if (accessRule.getExternalList().isEmpty()) {
      throw new Status512ProviderNotConfiguredException(RegistrationError.PROVIDER_NOT_CONFIGURED.getResponseMessageLocal(messageSource, domainName));
    }
    for (ExternalList externalList : accessRule.getExternalList()) {
      try {
        Provider provider = providerClientService.findProviderByUrlAndDomainName(domainName, externalList.getProviderUrl());
        if (!provider.getEnabled()) {
          throw new Status474DomainProviderDisabledException(RegistrationError.PROVIDER_DISABLED.getResponseMessageLocal(messageSource, domainName));
        }
      } catch (Status550ServiceDomainClientException e) {
        throw new Status500InternalServerErrorException(RegistrationError.INTERNAL_SERVER_ERROR.getResponseMessageLocal(messageSource, domainName));
      }
    }

    AuthorizationRequest authorizationRequest = buildAuthorizationRequest(playerBasic, remoteAddr, userAgent);
    AuthorizationResult authorizationResult = authorizationService.checkAuthorization(domainName, accessRuleName, authorizationRequest, test);
    HashMap<String, String> data = new HashMap<>();
    if (authorizationResult.getRawResults() != null && !authorizationResult.getRawResults().isEmpty()) {///refactor
      for (CheckAuthorizationResult rawResult : authorizationResult.getRawResults()) {
       if (rawResult.getData() != null && !rawResult.getData().isEmpty()) {
         data.putAll(rawResult.getData());
       }
      }
    }

    return ExternalValidationResponse.builder()
        .result(authorizationResult.isSuccessful())
        .data(data)
        .message(!authorizationResult.isSuccessful() ? RegistrationError.REGISTRATION_ERROR.getResponseMessageLocal(messageSource, domainName,
            authorizationResult.getErrorMessage(), authorizationResult.getErrorMessage()) : null)
        .rejectReason(authorizationResult.getRejectOutcome() != null ? authorizationResult.getRejectOutcome().name() : null)
        .build();
  }

  private AuthorizationRequest buildAuthorizationRequest(PlayerBasic playerBasic, String remoteAddr, String userAgent) {
    Map<String, String> ipAndUserAgentData = accessService.parseIpAndUserAgent(remoteAddr, userAgent);
    return AuthorizationRequest.builder()
        .additionalData(playerBasic.getAdditionalData())
        .playerBasic(playerBasic)
        .ipAddress((ipAndUserAgentData.get(AccessService.MAP_IP) != null)? ipAndUserAgentData.get(AccessService.MAP_IP): null)
        .country((ipAndUserAgentData.get(AccessService.MAP_COUNTRY) != null)? ipAndUserAgentData.get(AccessService.MAP_COUNTRY): null)
        .state((ipAndUserAgentData.get(AccessService.MAP_STATE) != null)? ipAndUserAgentData.get(AccessService.MAP_STATE): null)
        .city((ipAndUserAgentData.get(AccessService.MAP_CITY) != null)? ipAndUserAgentData.get(AccessService.MAP_CITY): null)
        .os((ipAndUserAgentData.get(AccessService.MAP_OS) != null)? ipAndUserAgentData.get(AccessService.MAP_OS): null)
        .browser((ipAndUserAgentData.get(AccessService.MAP_BROWSER) != null)? ipAndUserAgentData.get(AccessService.MAP_BROWSER): null)
        .build();
  }

  private void validateSha256(String firstName, String lastName, String preSharedKey, String sha256, String domainName)
      throws Status470HashInvalidException {
    HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(preSharedKey);
    if (firstName != null) {
      hasher.addItem(firstName);
    }
    hasher.addItem(lastName);
    String expectedHash = hasher.calculateHash();
    if (!expectedHash.equals(sha256)) {
      log.info("Expected " + expectedHash + " sha256 but got " + sha256 + ", firstName = " + firstName + ", lastName = " + lastName);
      throw new Status470HashInvalidException(RegistrationError.INVALID_HASH.getResponseMessageLocal(messageSource, domainName));
    }
  }
}
