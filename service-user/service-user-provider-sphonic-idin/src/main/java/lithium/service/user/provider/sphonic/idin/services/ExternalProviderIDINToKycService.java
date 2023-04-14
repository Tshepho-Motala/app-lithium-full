package lithium.service.user.provider.sphonic.idin.services;

import lithium.client.changelog.SubCategory;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.kyc.client.KycResultsClient;
import lithium.service.kyc.client.objects.VerificationKycAttempt;
import lithium.service.kyc.entities.VerificationResult;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.kyc.provider.objects.VendorData;
import lithium.service.kyc.provider.objects.VerificationMethodType;
import lithium.service.user.client.objects.*;
import lithium.service.user.provider.sphonic.idin.ServiceUserProviderSphonicIdinModuleInfo;
import lithium.service.user.provider.sphonic.idin.objects.*;
import lithium.service.user.provider.sphonic.idin.storage.entities.IDINRequest;
import lithium.service.user.provider.sphonic.idin.storage.repositories.IDINRequestReposistory;
import lithium.service.user.provider.sphonic.idin.storage.repositories.IDINResponseRepository;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
/**
 * {@code ExternalProviderIDINToKycService}
 * This service is used for integration with the KYC service during registration
 */
public class ExternalProviderIDINToKycService {
    private LithiumServiceClientFactory clientFactory;
    private IDINRequestReposistory idinRequestReposistory;
    private IDINCheckPlayerDetailsService checkPlayerDetailsService;
    private IDINResponseRepository idinResponseRepository;
    private MessageSource messageSource;
    private ServiceUserProviderSphonicIdinModuleInfo moduleInfo;

    public ExternalProviderIDINToKycService(LithiumServiceClientFactory clientFactory, IDINRequestReposistory idinRequestReposistory,
                                            IDINCheckPlayerDetailsService checkPlayerDetailsService, IDINResponseRepository idinResponseRepository,
                                            MessageSource messageSource, ServiceUserProviderSphonicIdinModuleInfo moduleInfo) {
        this.clientFactory = clientFactory;
        this.idinRequestReposistory = idinRequestReposistory;
        this.checkPlayerDetailsService = checkPlayerDetailsService;
        this.idinResponseRepository = idinResponseRepository;
        this.messageSource = messageSource;
        this.moduleInfo = moduleInfo;
    }

    public Response<VerificationResult> kycExternalProvider(PostRegistrationSteps postRegistrationSteps, User user) {
        try {
            if(!ObjectUtils.isEmpty(user)) {
                KycResultsClient kycResultsClient = clientFactory.target(KycResultsClient.class, "service-kyc", true);
                VerificationKycAttempt verificationKycAttempt = createVerificationKycAttemptObject(postRegistrationSteps, user);
                Response<VerificationResult> kycRes = kycResultsClient.addVerificationResult(verificationKycAttempt);
                log.info("KYC request for user {} was {} ", kycRes.getData().getUser().getGuid(), kycRes.isSuccessful());
            }
        } catch (LithiumServiceClientFactoryException ex) {
            log.error("Error occurred when trying to reach external clients :" + ex);
        }
        return null;
    }

    public VerificationKycAttempt createVerificationKycAttemptObject(PostRegistrationSteps postRegistrationSteps, lithium.service.user.client.objects.User user) {
        final String iDinApplicantHash = postRegistrationSteps.getApplicantGuid().split("/")[1];
        IDINRequest idinRequest = idinRequestReposistory.findIDINRequestByIdinApplicantHash(iDinApplicantHash);
        lithium.service.user.provider.sphonic.idin.storage.entities.IDINResponse iDinResponse = idinResponseRepository.findFirstByIdinRequestIdOrderByIdDesc(idinRequest.id);
        JSONObject jsonObject = new JSONObject(iDinResponse.getRawResponseData());
        JSONObject data = jsonObject.getJSONObject("SphonicResponse").getJSONObject("data");
        final String domainName = postRegistrationSteps.getApplicantGuid().split("/")[0];
        final NameData nameData = checkPlayerDetailsService.checkNameData(data);
        final AddressData addressData = checkPlayerDetailsService.checkAddressData(data);
        final AgeData ageData = checkPlayerDetailsService.checkAgeData(data);
        final GenderData genderData =checkPlayerDetailsService.checkGenderData(data);
        final ContactData contactData = checkPlayerDetailsService.checkContactData(data);
        final TraceData traceData = checkPlayerDetailsService.checkTraceData(data);
        final String outComeData = data.getJSONObject("outcome").toString();

        Map<String, String> vendorDataMap = new HashMap<>();
        List<VendorData> vendorDataList = new ArrayList<>();
        vendorDataMap.put("iDinApplicantHash", iDinApplicantHash);
        vendorDataMap.put("legalLastName", nameData.getLegalLastName());
        vendorDataMap.put("preferredLastName", nameData.getPreferredLastName());
        vendorDataMap.put("legalLastNamePrefix", nameData.getLegalLastNamePrefix());
        vendorDataMap.put("preferredLastName", nameData.getPreferredLastName());
        vendorDataMap.put("dateOfBirth", ageData.getDateOfBirth().toString());
        vendorDataMap.put("gender", genderData.getGender());
        vendorDataMap.put("responseDateTime", traceData.getResponseDateTime());
        vendorDataMap.put("sphonicTransactionId", traceData.getSphonicTransactionId());
        vendorDataMap.put("bluemTransactionId", traceData.getBluemTransactionId());
        vendorDataMap.put("livescoreApplicantId", traceData.getLivescoreApplicantId());
        vendorDataMap.put("livescoreRequestId", traceData.getLivescoreRequestId());
        vendorDataMap.put("city", addressData.getCity());
        vendorDataMap.put("street", addressData.getStreet());
        vendorDataMap.put("countryCode", addressData.getCountryCode());
        vendorDataMap.put("postalCode", addressData.getPostalCode());
        vendorDataMap.put("phoneNumber", contactData.getPhoneNumber());
        vendorDataMap.put("email", contactData.getEmail());
        vendorDataMap.put("outcome", outComeData);

        VendorData vendorData = VendorData.builder()
                .name(SubCategory.IDIN_VERIFICATION.getName())
                .data(vendorDataMap)
                .build();
        vendorDataList.add(vendorData);

        final String lastNamePrefix = !StringUtil.isEmpty(user.getLastNamePrefix()) ? user.getLastNamePrefix() : "";
        KycSuccessVerificationResponse kycSuccessVerificationResponse = KycSuccessVerificationResponse.builder()
                .lastName(!ObjectUtils.isEmpty(user.getLastName()) ? lastNamePrefix + " " + user.getLastName() : null)
                .dob(user.getDobYear() + "/" + user.getDobMonth() + "/" + user.getDobDay())
                .address(!ObjectUtils.isEmpty(addressData) ? addressData.getHouseNumber() + " " + addressData.getStreet() + " " + addressData.getCity()
                        + " " + addressData.getPostalCode() + " " + addressData.getCountryCode() : null)
                .phoneNumber(!ObjectUtils.isEmpty(user.getCellphoneNumber()) ? user.getCellphoneNumber() : null)
                .dobYearOnly(!ObjectUtils.isEmpty(user.getDobYear()))
                .providerRequestId(idinRequest.getSphonicTransactionId())
                .success(false) // Always false for iDin because it's an address only verification service
                .createdOn(new DateTime().toDate())
                .methodTypeUid(VerificationMethodType.METHOD_IDIN_VERIFICATION.getValue())
                .vendorsData(vendorDataList)
                .resultMessageText(messageSource.getMessage("UI_NETWORK_ADMIN.CHANGELOGS.PLAYER.IDIN_ADDRESS_VERIFIED", new Object[]{new lithium.service.translate.client.objects.Domain(domainName), domainName}, LocaleContextHolder.getLocale()))
                .addressDecision(user.getAddressVerified().toString().toUpperCase())
                .build();
        VerificationKycAttempt verificationKycAttempt = VerificationKycAttempt.builder()
                .domainName(domainName)
                .providerName(moduleInfo.getModuleName())
                .methodName(VerificationMethodType.METHOD_IDIN_VERIFICATION.getValue())
                .userGuid(user.getGuid())
                .kycSuccessVerificationResponse(kycSuccessVerificationResponse)
                .build();
        return verificationKycAttempt;
    }
}
