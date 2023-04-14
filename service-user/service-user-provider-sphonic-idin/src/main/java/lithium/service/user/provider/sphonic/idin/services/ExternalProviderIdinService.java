package lithium.service.user.provider.sphonic.idin.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.access.provider.sphonic.data.entities.Domain;
import lithium.service.access.provider.sphonic.data.repositories.DomainRepository;
import lithium.service.access.provider.sphonic.data.repositories.SphonicAuthenticationRepository;
import lithium.service.access.provider.sphonic.services.SphonicAuthenticationService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.objects.AddressBasic;
import lithium.service.user.client.objects.ExternalUserDetailsRequest;
import lithium.service.user.client.objects.ExternalUserDetailsResponse;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.provider.sphonic.idin.objects.*;
import lithium.service.user.provider.sphonic.idin.objects.IncompleteUserStatus;
import lithium.service.user.provider.sphonic.idin.storage.entities.IDINRequest;
import lithium.service.user.provider.sphonic.idin.storage.entities.IDINResponse;
import lithium.service.user.provider.sphonic.idin.storage.entities.User;
import lithium.service.user.provider.sphonic.idin.storage.repositories.IDINRequestReposistory;
import lithium.service.user.provider.sphonic.idin.storage.repositories.IDINResponseRepository;
import lithium.service.user.provider.sphonic.idin.storage.repositories.UserRepository;
import lithium.util.ExceptionMessageUtil;
import lithium.util.HmacSha256HashCalculator;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
@Slf4j
/**
 * {@code ExternalProviderIdinService}
 * This service is used for retrieving iDin details during registration
 */
public class ExternalProviderIdinService {
    private IDINRequestReposistory idinRequestReposistory;
    private IDINConfigurationService idinConfigurationService;
    private SphonicAuthenticationRepository sphonicAuthenticationRepository;
    private SphonicAuthenticationService sphonicAuthenticationService;
    private DomainRepository domainRepository;
    private UserRepository userRepository;
    private IDINCheckPlayerDetailsService checkPlayerDetailsService;
    private IDINRestService idinRestService;
    private IDINResponseRepository idinResponseRepository;

    private final String PLAYER_IP_ADDRESS = "playerIpAddress";
    private final String IDIN_VERIFICATION_URL = "iDinVerificationUrl";
    private final String IDIN_RETURN_URL = "iDinReturnUrl";
    private final String NONE = "None";

    @Autowired
    public ExternalProviderIdinService(IDINRequestReposistory idinRequestReposistory, IDINConfigurationService idinConfigurationService,
                                       SphonicAuthenticationRepository sphonicAuthenticationRepository, SphonicAuthenticationService sphonicAuthenticationService,
                                       DomainRepository domainRepository, UserRepository userRepository, IDINCheckPlayerDetailsService checkPlayerDetailsService,
                                       IDINRestService idinRestService, IDINResponseRepository idinResponseRepository) {
        this.idinRequestReposistory = idinRequestReposistory;
        this.idinConfigurationService = idinConfigurationService;
        this.sphonicAuthenticationRepository = sphonicAuthenticationRepository;
        this.sphonicAuthenticationService = sphonicAuthenticationService;
        this.domainRepository = domainRepository;
        this.userRepository = userRepository;
        this.checkPlayerDetailsService = checkPlayerDetailsService;
        this.idinRestService = idinRestService;
        this.idinResponseRepository = idinResponseRepository;
    }

    public ExternalUserDetailsResponse sendRequestToIdin(ExternalUserDetailsRequest externalUserDetailsRequest) throws Status512ProviderNotConfiguredException, Status550ServiceDomainClientException {
        IDINRequestObject idinRequestObject;
        ExternalUserDetailsResponse externalUserDetailsRes = ExternalUserDetailsResponse.builder().build();

        String configUsername = idinConfigurationService.getDomainConfiguration(externalUserDetailsRequest.getDomainName()).getUsername();
        String configPassword = idinConfigurationService.getDomainConfiguration(externalUserDetailsRequest.getDomainName()).getPassword();
        String iDinUrl = idinConfigurationService.getDomainConfiguration(externalUserDetailsRequest.getDomainName()).getIDinUrl();
        String merchantId = idinConfigurationService.getDomainConfiguration(externalUserDetailsRequest.getDomainName()).getMerchantId();
        String iDinStartWorkflowName = idinConfigurationService.getDomainConfiguration(externalUserDetailsRequest.getDomainName()).getIDinStartWorkflowName();
        String iDinRetrieveWorkflowName = idinConfigurationService.getDomainConfiguration(externalUserDetailsRequest.getDomainName()).getIDinRetrieveWorkflowName();
        String iDinApplicantHashKey = idinConfigurationService.getDomainConfiguration(externalUserDetailsRequest.getDomainName()).getApplicantHashKey();

        int connectionRequestTimeout = idinConfigurationService.getDomainConfiguration(externalUserDetailsRequest.getDomainName()).getConnectionRequestTimeout();
        int connectionTimeout = idinConfigurationService.getDomainConfiguration(externalUserDetailsRequest.getDomainName()).getConnectionTimeout();
        int socketTimeout = idinConfigurationService.getDomainConfiguration(externalUserDetailsRequest.getDomainName()).getSocketTimeout();

        switch(externalUserDetailsRequest.getStage()) {
            case 1 :
                String iDInApplicantHashReq = getIDInApplicantHash(iDinApplicantHashKey, externalUserDetailsRequest);
                String applicantHash;
                if(!ObjectUtils.isEmpty(iDInApplicantHashReq)) {
                    IDINRequest idinRequest = idinRequestReposistory.findIDINRequestByIdinApplicantHash(iDInApplicantHashReq);
                    Map<String, String> additionalData = new HashMap<>();
                    additionalData.put(iDinApplicantHashKey, idinRequest.getIdinApplicantHash());
                    additionalData.put(IDIN_VERIFICATION_URL,idinRequest.getVerificationUrl());
                    PlayerBasic playerBasic = PlayerBasic.builder()
                            .additionalData(additionalData)
                            .domainName(externalUserDetailsRequest.getDomainName())
                            .build();

                    externalUserDetailsRes = ExternalUserDetailsResponse.builder()
                            .domainName(externalUserDetailsRequest.getDomainName())
                            .stage(externalUserDetailsRequest.getStage())
                            .status(IncompleteUserStatus.SUCCESS.id())
                            .playerBasic(playerBasic)
                            .build();
                } else {
                    String clientIpAddress = externalUserDetailsRequest.getIncompleteUserBasic().getAdditionalData().get(PLAYER_IP_ADDRESS);
                    String returnUrl = externalUserDetailsRequest.getIncompleteUserBasic().getAdditionalData().get(IDIN_RETURN_URL);
                    applicantHash = generateApplicantHash(configPassword, clientIpAddress);
                    Domain domain = domainRepository.findOrCreateByName(externalUserDetailsRequest.getDomainName(), () -> new Domain());
                    String iDinUserGuid = domain.getName() + "/" + applicantHash;
                    User user = userRepository.findOrCreateByGuid(iDinUserGuid, () -> User.builder().domain(domain).build());

                    String requestId = UUID.randomUUID().toString().replace("-", ""); //only used for correlation from lithium to sphonic - 35 char limit on uniqueReference
                    Long applicantReferenceOffset = getApplicantReferenceOffset(domain.getName());
                    // Pre-builds the request to iDin
                    IDINRequest idinRequest = idinRequestReposistory.save(IDINRequest.builder()
                            .createdDate(Instant.now().toEpochMilli())
                            .idinApplicantHash(applicantHash) //Do we need to store it here still, since we are able to resolve the applicant by guid (domainName/applicantHash)
                            .returnUrl(returnUrl)
                            .lithiumRequestId(requestId)
                            .playerIpAddress(clientIpAddress)
                            .user(user)
                            .domain(domain)
                            .applicantRefOffset(applicantReferenceOffset)
                            .build());
                    try {
                        // Prepare iDinRetrieve
                        idinRequestObject = generateIdinRequest(idinRequest.getId(), returnUrl, applicantReferenceOffset);
                        // Get Sphonic Authentication token
                        String sphonicAccessToken = getSphonicAccessToken(externalUserDetailsRequest.getDomainName(), configUsername , configPassword ,
                                idinConfigurationService.getDomainConfiguration(externalUserDetailsRequest.getDomainName()).getAuthenticationUrl(), connectionTimeout, connectionRequestTimeout, socketTimeout);

                        if (sphonicAccessToken != null) {
                            // Do Workflow 1 (IDINRetrieve API Call)
                            MultiValueMap<String, String> headers = getRequestHeaderMultiValueMap(sphonicAccessToken);
                            ObjectMapper objectMapper = new ObjectMapper();
                            String stringObj = objectMapper.writeValueAsString(idinRequestObject);
                            HttpEntity<String> entity = new HttpEntity<>(stringObj, headers);
                            final String url = iDinUrl + "/" + merchantId + "/" + iDinStartWorkflowName;
                            RestTemplate restTemplate = idinRestService.createRestTemplate(connectionRequestTimeout, connectionTimeout, socketTimeout);
                            ResponseEntity<String> idinResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                            switch (idinResponse.getStatusCode().value()) {
                                case 200:
                                    JSONObject jsonObject = new JSONObject(idinResponse.getBody());
                                    JSONObject data = jsonObject.getJSONObject("SphonicResponse").getJSONObject("data");
                                    if (jsonObject != null && idinResponse.getStatusCodeValue() == 200 && !data.get("identURL").toString().equalsIgnoreCase(NONE)) {
                                        // Update iDinRequest with Sphonic Response
                                        idinRequest.setLithiumRequestId(data.get("livescoreRequestId").toString());
                                        idinRequest.setBluemTransactionId(data.get("bluemTransactionId").toString());
                                        idinRequest.setSphonicTransactionId(data.get("sphonicTransactionId").toString());
                                        idinRequest.setVerificationUrl(data.get("identURL").toString());
                                        idinRequest.setLastModifiedDate(Instant.now().toEpochMilli());
                                        IDINRequest savedIdinRequest = idinRequestReposistory.save(idinRequest);
                                        saveIdinResponse(externalUserDetailsRequest, jsonObject, savedIdinRequest);

                                        Map<String, String> additionalData = new HashMap<>();
                                        additionalData.put(iDinApplicantHashKey, idinRequest.getIdinApplicantHash());
                                        additionalData.put(IDIN_VERIFICATION_URL,data.get("identURL").toString());
                                        PlayerBasic playerBasic = PlayerBasic.builder()
                                                .additionalData(additionalData)
                                                .domainName(externalUserDetailsRequest.getDomainName())
                                                .build();

                                        externalUserDetailsRes = ExternalUserDetailsResponse.builder()
                                                .domainName(externalUserDetailsRequest.getDomainName())
                                                .stage(externalUserDetailsRequest.getStage())
                                                .status(IncompleteUserStatus.SUCCESS.id())
                                                .playerBasic(playerBasic)
                                                .build();
                                    } else {
                                        externalUserDetailsRes = iDinStageOneFailed(externalUserDetailsRequest, idinRequest, IncompleteUserStatus.FAIL.id(), iDinApplicantHashKey);
                                    }
                                    break;
                                case 404:
                                    externalUserDetailsRes = iDinStageOneFailed(externalUserDetailsRequest, idinRequest, IncompleteUserStatus.TIMEOUT.id(), iDinApplicantHashKey);
                                    break;
                                default:
                                    externalUserDetailsRes = iDinStageOneFailed(externalUserDetailsRequest, idinRequest, IncompleteUserStatus.FAIL.id(), iDinApplicantHashKey);
                            }
                        }
                    } catch(ResourceAccessException resourceAccessException) {
                        log.error("IDIN request 1 timed out : " + resourceAccessException.getMessage());
                        externalUserDetailsRes = iDinStageOneFailed(externalUserDetailsRequest, idinRequest, IncompleteUserStatus.TIMEOUT.id(), iDinApplicantHashKey);
                    } catch(Exception ex) {
                        log.error("IDIN stage 1 process failed for " + idinRequest + " - errors : " + ExceptionMessageUtil.allMessages(ex), ex);
                        externalUserDetailsRes = iDinStageOneFailed(externalUserDetailsRequest, idinRequest, IncompleteUserStatus.FAIL.id(), iDinApplicantHashKey);
                    }

                }
                break;
            case 2 :
                applicantHash = externalUserDetailsRequest.getIncompleteUserBasic().getAdditionalData().get(iDinApplicantHashKey);
                IDINRequest byLithiumRequestId = idinRequestReposistory.findIDINRequestByIdinApplicantHash(applicantHash);
                boolean addressVerified = false;
                JSONObject jsonObject;
                try {
                    String accessToken = getSphonicAccessToken(externalUserDetailsRequest.getDomainName(), configUsername , configPassword ,
                            idinConfigurationService.getDomainConfiguration(externalUserDetailsRequest.getDomainName()).getAuthenticationUrl(), connectionTimeout, connectionRequestTimeout, socketTimeout);

                    if (accessToken != null) {
                        RequestDetails requestDetails = RequestDetails.builder()
                                .requestId(byLithiumRequestId.getLithiumRequestId())
                                .requestDateTime(LocalDateTime.now().toString())
                                .build();
                        RequestDataWorkFlowTwo requestDataWorkFlowTwo = RequestDataWorkFlowTwo.builder()
                                .bluemTransactionId(byLithiumRequestId.bluemTransactionId)
                                .build();
                        IDINWorkflowTwoRequest idinWorkflowTwoRequest = IDINWorkflowTwoRequest.builder()
                                .requestDetails(requestDetails)
                                .requestData(requestDataWorkFlowTwo)
                                .build();
                        MultiValueMap<String, String> requestHeaders = getRequestHeaderMultiValueMap(accessToken);
                        ObjectMapper objectMapper = new ObjectMapper();
                        HttpEntity<?> requestEntity = new HttpEntity<>(objectMapper.writeValueAsString(idinWorkflowTwoRequest), requestHeaders);
                        RestTemplate restTemplate = idinRestService.createRestTemplate(connectionRequestTimeout, connectionTimeout, socketTimeout);
                        final String url = iDinUrl + "/" + merchantId + "/" + iDinRetrieveWorkflowName;
                        ResponseEntity<String> idinWorkFlowTwo = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
                        switch (idinWorkFlowTwo.getStatusCodeValue()) {
                            case 200:
                                jsonObject = new JSONObject(idinWorkFlowTwo.getBody());
                                String nameJsonData = jsonObject.getJSONObject("SphonicResponse").getJSONObject("data").get("nameData").toString();
                                if(jsonObject != null && idinWorkFlowTwo.getStatusCodeValue() == 200 && checkPlayerDetailsService.isPresent(nameJsonData)) {
                                    saveIdinResponse(externalUserDetailsRequest, jsonObject, byLithiumRequestId);
                                    JSONObject data = jsonObject.getJSONObject("SphonicResponse").getJSONObject("data");
                                    Map<String, String> additionalData = new HashMap<>();
                                    additionalData.put(iDinApplicantHashKey, applicantHash);
                                    final NameData nameData = checkPlayerDetailsService.checkNameData(data);
                                    final AddressData addressData = checkPlayerDetailsService.checkAddressData(data);
                                    final AgeData ageData = checkPlayerDetailsService.checkAgeData(data);
                                    final GenderData genderData =checkPlayerDetailsService.checkGenderData(data);
                                    final ContactData contactData = checkPlayerDetailsService.checkContactData(data);
                                    AddressBasic addressBasic = null;
                                    if (addressData != null) {
                                        addressBasic = AddressBasic.builder()
                                                .addressLine1(addressData.getHouseNumber() + " " + addressData.getStreet())
                                                .city(addressData.getCity())
                                                .postalCode(addressData.getPostalCode())
                                                .countryCode(addressData.getCountryCode())
                                                .build();
                                        addressVerified = true;
                                    }
                                    PlayerBasic playerBasic = PlayerBasic.builder()
                                            .lastName(nameData.getLegalLastName())
                                            .lastNamePrefix(nameData.getLegalLastNamePrefix())
                                            .residentialAddress(addressBasic)
                                            .dobDay((ageData != null && ageData.getDateOfBirth() != null) ? ageData.getDateOfBirth().toLocalDate().getDayOfMonth() : null)
                                            .dobMonth((ageData != null && ageData.getDateOfBirth() != null) ? ageData.getDateOfBirth().toLocalDate().getMonthValue() : null)
                                            .dobYear((ageData != null && ageData.getDateOfBirth() != null) ? ageData.getDateOfBirth().toLocalDate().getYear() : null)
                                            .gender((genderData != null && genderData.getGender() != null) ? genderData.getGender() : null)
                                            .cellphoneNumber((contactData != null && contactData.getPhoneNumber() != null) ? contactData.getPhoneNumber() : null)
                                            .email((contactData != null && contactData.getEmail() != null) ? contactData.getEmail() : null)
                                            .additionalData(additionalData)
                                            .domainName(externalUserDetailsRequest.getDomainName())
                                            .build();

                                    externalUserDetailsRes = ExternalUserDetailsResponse.builder()
                                            .domainName(externalUserDetailsRequest.getDomainName())
                                            .stage(externalUserDetailsRequest.getStage())
                                            .status(IncompleteUserStatus.SUCCESS.id())
                                            .playerBasic(playerBasic)
                                            .build();
                                } else if (jsonObject != null && idinWorkFlowTwo.getStatusCodeValue() == 200 && !checkPlayerDetailsService.isPresent(nameJsonData)) {
                                    externalUserDetailsRes = ExternalUserDetailsResponse.builder()
                                            .domainName(externalUserDetailsRequest.getDomainName())
                                            .stage(externalUserDetailsRequest.getStage())
                                            .status(IncompleteUserStatus.FAIL.id())
                                            .build();
                                }
                                break;
                            case 404:
                                log.error("IDIN request 2 timed out : " );
                                externalUserDetailsRes = ExternalUserDetailsResponse.builder()
                                        .domainName(externalUserDetailsRequest.getDomainName())
                                        .stage(externalUserDetailsRequest.getStage())
                                        .status(IncompleteUserStatus.TIMEOUT.id())
                                        .build();
                                break;
                            default:
                                Map<String, String> additionalData = new HashMap<>();
                                additionalData.put(iDinApplicantHashKey, applicantHash);
                                externalUserDetailsRes = ExternalUserDetailsResponse.builder()
                                        .domainName(externalUserDetailsRequest.getDomainName())
                                        .stage(externalUserDetailsRequest.getStage())
                                        .status(IncompleteUserStatus.FAIL.id())
                                        .build();
                        }
                    }
                } catch(ResourceAccessException resourceAccessException) {
                    log.error("IDIN request 2 timed out : " + resourceAccessException.getMessage());
                    externalUserDetailsRes = ExternalUserDetailsResponse.builder()
                            .domainName(externalUserDetailsRequest.getDomainName())
                            .stage(externalUserDetailsRequest.getStage())
                            .status(IncompleteUserStatus.TIMEOUT.id())
                            .build();
                } catch(Exception ex) {
                    log.error("IDIN stage 2 process failed for " + byLithiumRequestId + " - errors : " + ExceptionMessageUtil.allMessages(ex), ex);
                    Map<String, String> additionalData = new HashMap<>();
                    additionalData.put(iDinApplicantHashKey, applicantHash);
                    externalUserDetailsRes = ExternalUserDetailsResponse.builder()
                            .domainName(externalUserDetailsRequest.getDomainName())
                            .stage(externalUserDetailsRequest.getStage())
                            .status(IncompleteUserStatus.FAIL.id())
                            .build();
                } finally {
                    if(!ObjectUtils.isEmpty(externalUserDetailsRequest.getIncompleteUserBasic()) && !ObjectUtils.isEmpty(externalUserDetailsRequest.getIncompleteUserBasic().getAdditionalData())) {
                        User idinUser = userRepository.findByGuid(externalUserDetailsRequest.getDomainName() + "/" + getApplicantHash(externalUserDetailsRequest.getIncompleteUserBasic().getAdditionalData(), iDinApplicantHashKey));
                        if(!ObjectUtils.isEmpty(idinUser)) {
                            idinUser.setAddressVerified(addressVerified);
                            idinUser.setEmail(!ObjectUtils.isEmpty(externalUserDetailsRes) && !ObjectUtils.isEmpty(externalUserDetailsRes.getPlayerBasic()) ? externalUserDetailsRes.getPlayerBasic().getEmail() : null);
                            idinUser.setCellphoneNumber(!ObjectUtils.isEmpty(externalUserDetailsRes) && !ObjectUtils.isEmpty(externalUserDetailsRes.getPlayerBasic()) ? externalUserDetailsRes.getPlayerBasic().getCellphoneNumber() : null);
                            userRepository.save(idinUser);
                        }
                    }
                }
                break;
        }
        return externalUserDetailsRes;
    }

    private String getIDInApplicantHash(String applicantHashKey, ExternalUserDetailsRequest externalUserDetailsRequest) {
        Map<String, String> additionalDataMap = externalUserDetailsRequest.getIncompleteUserBasic().getAdditionalData();
        return additionalDataMap.get(applicantHashKey);
    }

    private MultiValueMap<String, String> getRequestHeaderMultiValueMap(String sphonicAccessToken) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Accept", "*/*");
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer " + sphonicAccessToken);
        return headers;
    }
    private void saveIdinResponse(ExternalUserDetailsRequest externalUserDetailsRequest, JSONObject jsonObject, IDINRequest savedIdinRequest) {
        try {
            IDINResponse iDinResponseJSON = IDINResponse.builder()
                    .idinRequestId(savedIdinRequest.id)
                    .stage(externalUserDetailsRequest.getStage())
                    .rawResponseData(jsonObject.toString())
                    .build();
            idinResponseRepository.save(iDinResponseJSON);
        } catch (Exception ex) {
            log.error("Could not save iDin response : " , ex);
        }
    }

    private ExternalUserDetailsResponse iDinStageOneFailed(ExternalUserDetailsRequest externalUserDetailsRequest, IDINRequest idinRequest, Long status, String iDinApplicantHashKey) {
        PlayerBasic playerBasic = PlayerBasic.builder().build();
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put(iDinApplicantHashKey, idinRequest.getIdinApplicantHash());
        playerBasic.setAdditionalData(additionalData);
        ExternalUserDetailsResponse externalUserDetailsRes =
                ExternalUserDetailsResponse.builder().domainName(externalUserDetailsRequest.getDomainName()).stage(
                        externalUserDetailsRequest.getStage()).status(status).playerBasic(playerBasic).build();
        return externalUserDetailsRes;
    }

    public IDINRequestObject generateIdinRequest(Long applicantId, String returnUrl, Long applicantReferenceOffset) {
        String requestId = UUID.randomUUID().toString().replace("-", ""); //only used for correlation from lithium to sphonic - 35 char limit on uniqueReference
        RequestDetails requestDetails = RequestDetails.builder()
                .requestId(requestId)
                .requestDateTime(LocalDateTime.now().toString()).build();
        RequestData requestData = RequestData.builder()
                .applicantReference(String.valueOf(applicantId + applicantReferenceOffset)) // The applicantId is simply the unique iDinRequest.id
                .returnUrl(returnUrl).build();

        return IDINRequestObject.builder()
                .requestDetails(requestDetails)
                .requestData(requestData).build();
    }

    private Long getApplicantReferenceOffset(String domainName) {
        Long applicantReferenceOffset = 0L;
        try {
            String offsetConfig = idinConfigurationService.getDomainConfiguration(domainName).getApplicantReferenceOffset();
            if(offsetConfig != null && !offsetConfig.isEmpty()){
                applicantReferenceOffset = Long.parseLong(offsetConfig);
            }
        } catch (Exception e) {
            log.error("Could not obtain applicant reference offset", e , e.getStackTrace());
        }
        return applicantReferenceOffset;
    }

    private String generateApplicantHash(String configPassword, String clientIPAddress) {
        Long timestamp = Instant.now().toEpochMilli();
        HmacSha256HashCalculator hashCalculator = new HmacSha256HashCalculator(configPassword);
        hashCalculator.addItem(clientIPAddress);
        hashCalculator.addItem(timestamp.toString());
        return hashCalculator.calculateHash();
    }

    private String getSphonicAccessToken(String domainName, String configUserName, String configHashPassword, String authenticationUrl, int connectionTimeout, int connectionRequestTimeout, int socketTimeout) throws Status512ProviderNotConfiguredException, Status500InternalServerErrorException, Status550ServiceDomainClientException {
        String accessToken = null;
        try {
            accessToken = sphonicAuthenticationService.getAccessToken(sphonicAuthenticationRepository, domainName, authenticationUrl , configUserName, configHashPassword,
                    connectionTimeout, connectionRequestTimeout, socketTimeout);
        } catch (Exception ex) {
            log.debug("Failed to obtain sphonic auth token", ex.getMessage());
        }
        return accessToken;
    }

    private String getApplicantHash(Map<String, String> data, String iDinApplicantHashKey) {
        String applicantHash = null;
        if(!ObjectUtils.isEmpty(data) && data.size() > 0) {
            TreeMap<String, String> dataMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            dataMap.putAll(data);
            if(dataMap.containsKey(iDinApplicantHashKey)) {
                applicantHash = dataMap.get(iDinApplicantHashKey);
            }
        }
        return applicantHash;
    }
}
