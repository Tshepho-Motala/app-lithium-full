package lithium.service.user.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lithium.exceptions.Status400BadRequestException;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.exceptions.Status463IncompleteUserRegistrationException;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.exceptions.Status555ServerTimeoutException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.geo.client.GeoClient;
import lithium.service.translate.client.objects.RegistrationError;
import lithium.service.user.client.objects.ExternalUserDetailsRequest;
import lithium.service.user.client.objects.ExternalUserDetailsResponse;
import lithium.service.user.client.objects.IncompleteUserBasic;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.PostRegistrationSteps;
import lithium.service.user.client.objects.PubSubEventType;
import lithium.service.user.client.objects.ValidatePreRegistration;
import lithium.service.user.client.objects.ValidatePreRegistrationResponse;
import lithium.service.user.client.system.UserAPIExternalSystemClient;
import lithium.service.user.data.entities.Address;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.IncompleteUser;
import lithium.service.user.data.entities.IncompleteUserLabelValue;
import lithium.service.user.data.entities.LabelValue;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.AddressRepository;
import lithium.service.user.data.repositories.IncompleteUserLabelValueRepository;
import lithium.service.user.data.repositories.IncompleteUserRepository;
import lithium.service.user.data.repositories.LabelRepository;
import lithium.service.user.data.repositories.LabelValueRepository;
import lithium.service.user.provider.sphonic.idin.objects.IncompleteUserStatus;
import lithium.service.user.services.oauthClient.OauthApiInternalClientService;
import lithium.services.ExternalApiAuthenticationService;
import lithium.util.ExceptionMessageUtil;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
public class IncompleteUserService {

  @Autowired IncompleteUserRepository incompleteUserRepository ;
  @Autowired IncompleteUserLabelValueRepository incompleteUserLabelValueRepository;
  @Autowired IncompleteUserLabelValueService incompleteUserLabelValueService;
  @Autowired OauthApiInternalClientService oauthApiInternalClientService;
  @Autowired ModelMapper modelMapper;
  @Autowired LithiumServiceClientFactory factory;
  @Autowired DomainService domainService;
  @Autowired LabelRepository labelRepository;
  @Autowired LabelValueRepository labelValueRepository;
  @Autowired ExternalApiAuthenticationService externalApiAuthenticationService;
  @Autowired LabelValueService labelValueService;
  @Autowired LabelService labelService;
  @Autowired RegistrationSynchronizeService synchronizeService;
  @Autowired AddressRepository addressRepository;
  @Autowired MessageSource messageSource;
  @Autowired PubSubUserService pubSubUserService;

  private final Integer STAGE_ONE = 1;
  private final Integer STAGE_TWO = 2;

  public IncompleteUser findFromEmail(String email) {
    return incompleteUserRepository.findByEmail(email);
  }

  public IncompleteUser addOrUpdateDomainSpecificUserLabelValues(IncompleteUser icu, Map<String, String> additionalData) {
    return additionalData.size() > 0 ? incompleteUserLabelValueService.updateOrAddUserLabelValues(icu, additionalData) : icu;
  }

  public void delete(PlayerBasic playerBasic, String labelName) {
    IncompleteUser incompleteUser;
    if (playerBasic.getAdditionalData() != null && containsApplicantHash(playerBasic.getAdditionalData(), getApplicantHashKey(
        playerBasic.getDomainName()))) {
      incompleteUser = findIncompleteUserFromLabelValue(getApplicantHash(playerBasic), labelName);
    } else {
      incompleteUser = incompleteUserRepository.findOne(playerBasic.getId());
    }
    log.debug("{ Removing incomplete user : {} }", incompleteUser);
    try {
      if (incompleteUser != null) {
        for (IncompleteUserLabelValue incompleteUserLabelValue : incompleteUser.getIncompleteUserLabelValueList()) {
          incompleteUserLabelValueRepository.delete(incompleteUserLabelValue);
        }
        incompleteUser.setResidentialAddress(null);
        incompleteUserRepository.delete(incompleteUser);
      }
    } catch (Exception e) {
      log.error("Unable to remove IncompleteUser on registration {}", incompleteUser, e);
    }
  }

  public Response<ValidatePreRegistrationResponse> postRegistrationStepsIncompleteUser(PlayerBasic pb, User user) {
    IncompleteUser incompleteUser = incompleteUserRegistrationChecks(pb);
    Provider provider = getProviderWhenEnabled(pb.getDomainName(), ProviderType.REGISTER);
    Response<ValidatePreRegistrationResponse> res = null;
    if(!ObjectUtils.isEmpty(incompleteUser) && !ObjectUtils.isEmpty(provider)) {
      res = incompleteUserPostRegistrationSteps(pb, user, provider);
      if(!ObjectUtils.isEmpty(res) && res.isSuccessful()) {
        sendAddressVerifiedPubSub(user);
      }
      //Remove incomplete user
      removeIncompleteUser(pb, getApplicantHashKey(pb.getDomainName()));
    }
    return res;
  }

  /**
   * Method That initiates the external verification process via configurable provider settings
   * @param domainName
   * @param incompleteUserBasic
   * @param sha
   * @param apiAuthorizationId
   * @param authorization
   * @return
   * @throws Status400BadRequestException
   * @throws Status512ProviderNotConfiguredException
   * @throws Status500InternalServerErrorException
   * @throws Status550ServiceDomainClientException
   * @throws LithiumServiceClientFactoryException
   * @throws Status470HashInvalidException
   * @throws Status401UnAuthorisedException
   * @throws Status426InvalidParameterProvidedException
   * @throws Status463IncompleteUserRegistrationException
   */
  public Response<PlayerBasic> registerIncompleteUser(String domainName, IncompleteUserBasic incompleteUserBasic, String sha, String apiAuthorizationId, String authorization)
      throws Status400BadRequestException, Status512ProviderNotConfiguredException, Status500InternalServerErrorException, Status550ServiceDomainClientException, LithiumServiceClientFactoryException, Status470HashInvalidException, Status401UnAuthorisedException, Status426InvalidParameterProvidedException, Status463IncompleteUserRegistrationException {
    try {
      oauthApiInternalClientService.validateClientAuth(authorization);
    } catch (Exception ex) {
      log.debug("Invalidated client auth : " + ex.getMessage());
      throw new Status401UnAuthorisedException(RegistrationError.INVALID_CLIENT_AUTH.getResponseMessageLocal(messageSource, domainName), ex.getStackTrace());
    }
    Integer stage;
    PlayerBasic icPlayerBasic = null;
    Response<PlayerBasic> build = null;
    stage = incompleteUserBasic.getStage();
    Provider provider = getProviderWhenEnabled(domainName, ProviderType.REGISTER);
    String reqApplicantHash = getApplicantHash(incompleteUserBasic, domainName);
    if(!ObjectUtils.isEmpty(reqApplicantHash)) {
      validateHash(apiAuthorizationId, reqApplicantHash, sha);
    }
    if(!ObjectUtils.isEmpty(provider) && !StringUtil.isEmpty(provider.getUrl())) {
      UserAPIExternalSystemClient userAPIExternalSystemClient = factory.target(UserAPIExternalSystemClient.class, provider.getUrl(), true);
      switch (stage) {
        case 1:
          ExternalUserDetailsRequest externalUserDetailsRequest = new ExternalUserDetailsRequest();
          externalUserDetailsRequest.setStage(STAGE_ONE);
          externalUserDetailsRequest.setIncompleteUserBasic(incompleteUserBasic);
          externalUserDetailsRequest.setDomainName(domainName);
          externalUserDetailsRequest.setApiAuthorizationId(apiAuthorizationId);
          Response<ExternalUserDetailsResponse> externalUserDetailsResponseResponse = userAPIExternalSystemClient.externalRegister(
              externalUserDetailsRequest);
          if (externalUserDetailsResponseResponse.isSuccessful() && externalUserDetailsResponseResponse.getData() != null) {
            String resApplicantHash = getApplicantHash(externalUserDetailsResponseResponse.getData().getPlayerBasic());
            String applicantHashKey = getApplicantHashKey(domainName);
            PlayerBasic tempPlayerBasic = externalUserDetailsResponseResponse.getData().getPlayerBasic();
            tempPlayerBasic.setStage(STAGE_ONE);
            findOrCreateIncompleteUserWithLabelValue(tempPlayerBasic, resApplicantHash, externalUserDetailsResponseResponse.getData().getStatus(), applicantHashKey);
            tempPlayerBasic.setId(null);
            icPlayerBasic = tempPlayerBasic;
            log.info("IncompleteUser registration for user {} request stage {}", domainName + "/" + icPlayerBasic.getUsername(), stage);
            build = Response.<PlayerBasic>builder().status(Status.OK_SUCCESS).data(icPlayerBasic).build();
            log.info("IncompleteUser registration for user {} response stage {} was {}", domainName + "/" + icPlayerBasic.getUsername(), stage, build.isSuccessful());
          } else if (externalUserDetailsResponseResponse.getStatus() == null || externalUserDetailsResponseResponse.getStatus().equals(Status.SERVER_TIMEOUT)) {
            throw new Status555ServerTimeoutException(messageSource.getMessage("ERROR_DICTIONARY.IDIN_REGISTRATION.TIMEOUT",
                new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, null, LocaleContextHolder.getLocale()));
          }
          break;
        case 2:
          try {
            String applicantHashKey = getApplicantHashKey(domainName);
            IncompleteUser incompleteUser = findIncompleteUserFromLabelValue(reqApplicantHash, applicantHashKey);
            String parameter = applicantHashKey + " = " + reqApplicantHash;
            if (incompleteUser == null) {
              throw new Status426InvalidParameterProvidedException(RegistrationError.INVALID_PARAMETER.getResponseMessageLocal(messageSource,
                  domainName, new Object[] { parameter }));
            }
            ExternalUserDetailsRequest stageTwoExternalDetailsRequest = new ExternalUserDetailsRequest();
            stageTwoExternalDetailsRequest.setStage(STAGE_TWO);
            stageTwoExternalDetailsRequest.setIncompleteUserBasic(incompleteUserBasic);
            stageTwoExternalDetailsRequest.setDomainName(domainName);

            Response<ExternalUserDetailsResponse> externalResTwo = userAPIExternalSystemClient.externalRegister(stageTwoExternalDetailsRequest);
            if (externalResTwo.isSuccessful() && externalResTwo.getData().getStatus() == IncompleteUserStatus.SUCCESS.id()) {
              icPlayerBasic = externalResTwo.getData().getPlayerBasic();
              icPlayerBasic.setId(null);
              setIncompleteUserProfile(icPlayerBasic, incompleteUser);
              Address residentialAddress;
              if (icPlayerBasic.getResidentialAddress() != null) {
                residentialAddress = modelMapper.map(icPlayerBasic.getResidentialAddress(), Address.class);
                Address address = synchronizeService.overrideGeoData(residentialAddress);
                if (ObjectUtils.isEmpty(address.getCountry()) && !ObjectUtils.isEmpty(address.getCountryCode())) {
                  try {
                    GeoClient geoClient = factory.target(GeoClient.class, "service-geo", true);
                    address.setCountry(
                        geoClient.countries().getData().stream().filter(country -> country.getCode().equalsIgnoreCase(address.getCountryCode()))
                            .findFirst()
                            .orElseThrow(() -> new Status426InvalidParameterProvidedException(
                                RegistrationError.COUNTRY_NOT_PROVIDED.getResponseMessageLocal(messageSource, domainName)))
                            .getName());
                  } catch (LithiumServiceClientFactoryException e) {
                    log.error("Problem getting geo data. address= " + address + " incompleteUser= " + incompleteUser + " - errors : "
                        + ExceptionMessageUtil.allMessages(e), e);
                  }
                }
                residentialAddress = addressRepository.save(address);
                incompleteUser.setResidentialAddress(residentialAddress);
              }
              incompleteUser.setStage(String.valueOf(STAGE_TWO));
              incompleteUser.setStatus(IncompleteUserStatus.SUCCESS.id());
              build = Response.<PlayerBasic>builder().status(Status.OK_SUCCESS).data(icPlayerBasic).build();
            } else if (externalResTwo.isSuccessful() && (externalResTwo.getData().getStatus().equals(IncompleteUserStatus.FAIL.id()) && ObjectUtils.isEmpty(externalResTwo.getData().getPlayerBasic()))) {
              incompleteUser.setStage(String.valueOf(STAGE_TWO));
              incompleteUser.setStatus(externalResTwo.getData().getStatus());
              throw new Status463IncompleteUserRegistrationException(RegistrationError.INCOMPLETE_USER_REGISTRATION.getResponseMessageLocal(
                  messageSource, domainName, new Object[] { parameter }));
            } else if (externalResTwo.getStatus() == null || externalResTwo.getStatus().equals(Status.SERVER_TIMEOUT)) {
              throw new Status555ServerTimeoutException(messageSource.getMessage("ERROR_DICTIONARY.IDIN_REGISTRATION.TIMEOUT",
                  new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, null, LocaleContextHolder.getLocale()));
            } else {
              if(externalResTwo.getData() != null) {
                icPlayerBasic = externalResTwo.getData().getPlayerBasic();
                setIncompleteUserProfile(icPlayerBasic, incompleteUser);
              }
              incompleteUser.setStage(STAGE_TWO.toString());
              incompleteUser.setStatus(IncompleteUserStatus.FAIL.id());
              build = Response.<PlayerBasic>builder().status(Status.OK_SUCCESS).data(icPlayerBasic).build();
            }
            incompleteUserRepository.save(incompleteUser);
          } catch (Exception ex) {
            throw ex;
          }
          break;
      }
    } else {
      throw new Status512ProviderNotConfiguredException(domainName);
    }
    return build;
  }

  private void setIncompleteUserProfile(PlayerBasic pb, IncompleteUser incompleteUser) {
    incompleteUser.setFirstName(pb.getFirstName());
    incompleteUser.setLastNamePrefix(pb.getLastNamePrefix());
    incompleteUser.setLastName(pb.getLastName());
    String gender = pb.getGender() != null ? (pb.getGender().toLowerCase().startsWith("m") ? "Male" : "Female") : pb.getGender();
    incompleteUser.setGender(gender);
    incompleteUser.setDobDay(pb.getDobDay());
    incompleteUser.setDobMonth(pb.getDobMonth());
    incompleteUser.setDobYear(pb.getDobYear());
    incompleteUser.setEmail(pb.getEmail());
    incompleteUser.setCellphoneNumber(pb.getCellphoneNumber());
  }

  private void validateHash(String apiAuthorizationId, String applicantHash, String hash)
      throws Status470HashInvalidException, Status401UnAuthorisedException, Status500InternalServerErrorException {
        String payload = apiAuthorizationId + "|" + applicantHash + "|";
        externalApiAuthenticationService.validate(apiAuthorizationId, payload, hash);
  }

  public IncompleteUser findIncompleteUserFromLabelValue(String idinApplicationHash, String labelName) {
    lithium.service.user.data.entities.Label label = labelRepository.findByName(labelName);
    LabelValue byLabelAndValue = labelValueRepository.findByLabelAndValue(label, idinApplicationHash);
    IncompleteUserLabelValue incompleteUserLabelValueByLabelValue = incompleteUserLabelValueRepository.findIncompleteUserLabelValueByLabelValue(byLabelAndValue);
    if (incompleteUserLabelValueByLabelValue != null) {
      return incompleteUserLabelValueByLabelValue.getIncompleteUser();
    } else return null;
  }

  public PlayerBasic findOrCreateIncompleteUserWithLabelValue(final PlayerBasic pb, String applicantHash, Long status, String applicantHashKey) {
    lithium.service.user.data.entities.Label saveLabel = labelService.findOrCreate(applicantHashKey);
    LabelValue labelValue = labelValueService.findOrCreate(saveLabel, applicantHash);
    IncompleteUserLabelValue incompleteUserLabel = incompleteUserLabelValueRepository.findIncompleteUserLabelValueByLabelValue(labelValue);
    IncompleteUser incompleteUser;

    if((!ObjectUtils.isEmpty(incompleteUserLabel) && !ObjectUtils.isEmpty(incompleteUserLabel.getIncompleteUser()))
    && !(incompleteUserLabel.getIncompleteUser().getId() == null || incompleteUserLabel.getIncompleteUser().getId() == 0L)) {
      Long incompleteUserId = incompleteUserLabel.getIncompleteUser().getId();
      incompleteUser = incompleteUserRepository.findOne(incompleteUserId);
    } else {
      incompleteUser = incompleteUser(pb);
      incompleteUser.setStatus(status);
      IncompleteUserLabelValue incompleteUserLabelValue = IncompleteUserLabelValue.builder()
          .incompleteUser(incompleteUser)
          .labelValue(labelValue)
          .build();
      incompleteUserLabel = incompleteUserLabelValueRepository.save(incompleteUserLabelValue);
    }
    List<IncompleteUserLabelValue> userLabelList = new ArrayList<>();
    userLabelList.add(incompleteUserLabel);
    incompleteUser.setIncompleteUserLabelValueList(userLabelList);
    modelMapper.getConfiguration().setAmbiguityIgnored(true);
    return modelMapper.map(incompleteUser, PlayerBasic.class);
  }

  public Page<IncompleteUser> findIncompleteUsers(Specification<IncompleteUser> spec, PageRequest pageRequest) {
    return incompleteUserRepository.findAll(spec, pageRequest);
  }

  public void removeIncompleteUser(final PlayerBasic playerBasic, final String labelName) {
    delete(playerBasic, labelName);
  }

  private IncompleteUser incompleteUser(PlayerBasic pb) {
    IncompleteUser icu;
    Domain domain = domainService.findOrCreate(pb.getDomainName());
    if (pb.getId() != null && pb.getId() > 0) {
      icu = incompleteUserRepository.findOne(pb.getId());

      if (icu != null) {
        log.debug("Change details for incomplete user from: " + icu + " to: " + pb);
      } else {
        log.debug(
            "New incomplete user being registered as there was no record left of old incomplete registration. :"
                + pb);
        pb.setId(0L);
      }
    }
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    modelMapper.getConfiguration().setAmbiguityIgnored(true);
    icu = modelMapper.map(pb, IncompleteUser.class);
    icu.setDomain(domain);
    icu = incompleteUserRepository.save(icu);
    return icu;
  }

  public PlayerBasic findIncompleteUser(final Long incompleteUserId) {
    // When null is returned, it means the signup was completed and the entry
    // removed from incomplete user table
    if (incompleteUserId == null || incompleteUserId <= 0) {
      return null;
    }

    IncompleteUser icu = incompleteUserRepository.findOne(incompleteUserId);
    if (icu == null) {
      return null;
    }

    PlayerBasic plb = new PlayerBasic();
    plb.setEmail(icu.getEmail());
    plb.setFirstName(icu.getFirstName());
    plb.setLastName(icu.getLastName());
    plb.setCellphoneNumber(icu.getCellphoneNumber());
    plb.setCountryCode(icu.getCountryCode());
    plb.setGender(icu.getGender());
    plb.setId(icu.getId());

    return plb;
  }

  public Response<ValidatePreRegistrationResponse> incompleteUserPostRegistrationSteps(PlayerBasic pb, User user, Provider provider) {
    if(!StringUtil.isEmpty(provider.getUrl())) {
      try {
        UserAPIExternalSystemClient userAPIExternalSystemClient = factory.target(UserAPIExternalSystemClient.class, provider.getUrl(), true);
        String applicantHash = getApplicantHash(pb);
        PostRegistrationSteps postRegistrationSteps =
            PostRegistrationSteps.builder()
                .userId(user.getId())
                .applicantGuid(user.getDomain().getName() + "/" + applicantHash)
                .email(user.getEmail())
                .cellphoneNumber(user.getCellphoneNumber())
                .build();
        return userAPIExternalSystemClient.postRegistrationSteps(postRegistrationSteps);
      } catch (LithiumServiceClientFactoryException ex) {
        log.error("Incomplete user post-registration steps failed | userGuid : " + user.guid() , ex);
      }
    }
    return null;
  }

  public Provider getProviderWhenEnabled(String domainName, ProviderType providerType) {
    Response<Iterable<Provider>> iterableResponse;
    try {
      ProviderClient providerClient = factory.target(ProviderClient.class, true);
      iterableResponse = providerClient.listByDomainAndType(domainName, providerType.name());
      List<Provider> providerUrl = StreamSupport.stream(iterableResponse.getData().spliterator(), true)
          .filter(s -> !ObjectUtils.isEmpty(s.getEnabled()) && s.getEnabled().booleanValue())
          .collect(Collectors.toList());
      return providerUrl.size() > 0 ? providerUrl.get(0) : null;
    } catch (LithiumServiceClientFactoryException ex) {
      log.error("Failed to obtain provider | domainName " + domainName + " | providerType " + providerType.toString(),ex);
    }
    return null;
  }

  /**
   * @Title : Incomplete User Workflow during registration process
   * @Description : When a registration request is received we check for an applicantHashKey and applicantHash value which should have been generated when a player passed
   * stage one of the external user register provider process. The applicantHash will be associated with an incomplete user record that will be used to retrieve details of
   * the player from the provider when needed for final registration
   */
  public IncompleteUser getIncompleteUser(PlayerBasic playerBasic, String applicantHash, String labelName) throws Status426InvalidParameterProvidedException, Status463IncompleteUserRegistrationException {
    IncompleteUser incompleteUserFromLabelValue = findIncompleteUserFromLabelValue(applicantHash, labelName);
    String parameter = "ApplicantHash = " + applicantHash;
    if (incompleteUserFromLabelValue == null) {
      throw new Status426InvalidParameterProvidedException(RegistrationError.INVALID_PARAMETER.getResponseMessageLocal(messageSource,
          playerBasic.getDomainName(), new Object[] { parameter }));
    }

    if (Integer.parseInt(incompleteUserFromLabelValue.getStage()) != 2 || (Integer.parseInt(incompleteUserFromLabelValue.getStage()) == 2 && !incompleteUserFromLabelValue.getStatus().equals(IncompleteUserStatus.SUCCESS.id()))) {
      throw new Status463IncompleteUserRegistrationException(RegistrationError.INCOMPLETE_USER_REGISTRATION.getResponseMessageLocal(messageSource,
          playerBasic.getDomainName(), new Object[]{ parameter }));
    }
    return incompleteUserFromLabelValue;
  }

  public void sendAddressVerifiedPubSub(User user) {
    try {
      pubSubUserService.buildAndSendPubSubAccountCreate(user, PubSubEventType.ACCOUNT_UPDATE);
    } catch (Exception ex) {
      log.warn("sendAddressVerifiedPubSub : " + ex.getMessage(), ex);
    }
  }

  public IncompleteUser incompleteUserRegistrationChecks(PlayerBasic playerBasic) throws Status426InvalidParameterProvidedException, Status463IncompleteUserRegistrationException {
    String applicantHash = getApplicantHash(playerBasic);
    if (!ObjectUtils.isEmpty(applicantHash)) {
      return getIncompleteUser(playerBasic, applicantHash, getApplicantHashKey(playerBasic.getDomainName()));
    }
    return null;
  }

  public String getApplicantHash(PlayerBasic playerBasic) {
    return getApplicantHash(playerBasic.getAdditionalData(), getApplicantHashKey(playerBasic));
  }

  public String getApplicantHash(IncompleteUserBasic incompleteUserBasic, String domainName) {
    return getApplicantHash(incompleteUserBasic.getAdditionalData(), getApplicantHashKey(domainName));
  }

  private static String getApplicantHash(Map<String, String> additionalData, String applicantHashKey) {
    return !ObjectUtils.isEmpty(additionalData) ? additionalData.get(applicantHashKey) : null;
  }

  @Cacheable(cacheNames="lithium.service.user.provider.incomplete-applicant-hash", key="#root.args[0]")
  public String getApplicantHashKey(String domainName) {
    final String DEFAULT_APPLICANT_HASH = "applicantHash";
    Provider provider = getProviderWhenEnabled(domainName, ProviderType.REGISTER);
    if (provider == null) {
      return DEFAULT_APPLICANT_HASH;
    }
    //Get applicantHashKey from configured ProviderType.REGISTER
    return provider.getProperties().stream().filter(pp -> "applicantHashKey".equals(pp.getName()))
        .map(ProviderProperty::getValue).findFirst().orElse(DEFAULT_APPLICANT_HASH);
  }

  public String getApplicantHashKey(PlayerBasic playerBasic) {
    return getApplicantHashKey(playerBasic.getDomainName());
  }

  public void incompleteUserPreRegChecks(PlayerBasic pb) {
    IncompleteUser incompleteUser = incompleteUserRegistrationChecks(pb);
    if(!ObjectUtils.isEmpty(incompleteUser)) {
      String applicantHash = getApplicantHash(pb);
      checkRegistrationAllowed(pb.getDomainName(), applicantHash);
    }
  }

  private void checkRegistrationAllowed(String domainName, String applicantHash) {
    Provider provider = getProviderWhenEnabled(domainName, ProviderType.REGISTER);
    if(!ObjectUtils.isEmpty(provider)) {
      String guid = domainName.concat("/").concat(applicantHash);
      ValidatePreRegistration build = ValidatePreRegistration.builder()
          .applicantGuid(guid)
          .build();
      UserAPIExternalSystemClient userAPIExternalSystemClient = null;
      try {
        userAPIExternalSystemClient = factory.target(UserAPIExternalSystemClient.class, true);
      } catch (LithiumServiceClientFactoryException e) {
        log.error("An error occured when reaching UserAPIExternalSystemClient : " + e);
      }

      if(!ObjectUtils.isEmpty(userAPIExternalSystemClient)) {
        Response<ValidatePreRegistrationResponse> validatePreRegResponse = userAPIExternalSystemClient.validatePreRegistration(build);
        if(!ObjectUtils.isEmpty(validatePreRegResponse) && validatePreRegResponse.isSuccessful() && !validatePreRegResponse.getData().getRegistrationAllowed()) {
          String parameter = getApplicantHashKey(domainName) + " " + applicantHash;
          throw new Status463IncompleteUserRegistrationException(RegistrationError.INCOMPLETE_USER_REGISTRATION.getResponseMessageLocal(messageSource, domainName , new Object[]{parameter}));
        }
      }
    }
  }

  private static boolean containsApplicantHash(Map<String, String> additionalData, String applicantHashKey) {
    return additionalData.keySet().stream().anyMatch(s -> s.toLowerCase().contains(applicantHashKey.toLowerCase()));
  }
}
