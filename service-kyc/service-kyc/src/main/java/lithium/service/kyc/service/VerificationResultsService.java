package lithium.service.kyc.service;


import lithium.service.kyc.entities.Domain;
import lithium.service.kyc.entities.KYCDocument;
import lithium.service.kyc.entities.KYCDocumentType;
import lithium.service.kyc.entities.KYCReason;
import lithium.service.kyc.entities.ResultMessage;
import lithium.service.kyc.entities.User;
import lithium.service.kyc.entities.VendorData;
import lithium.service.kyc.entities.VerificationResult;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.kyc.provider.objects.VerificationMethodType;
import lithium.service.kyc.repositories.DomainRepository;
import lithium.service.kyc.repositories.KYCDocumentRepository;
import lithium.service.kyc.repositories.MethodTypeRepository;
import lithium.service.kyc.repositories.ProviderRepository;
import lithium.service.kyc.repositories.ResultMessageRepository;
import lithium.service.kyc.repositories.UserRepository;
import lithium.service.kyc.repositories.VendorDataRepository;
import lithium.service.kyc.repositories.VerificationResultRepository;
import lithium.service.kyc.repositories.specifications.VerificationResultSpecifications;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@AllArgsConstructor
public class VerificationResultsService {

    private final VerificationResultRepository verificationResultRepository;
    private final DomainRepository domainRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final ResultMessageRepository resultMessageRepository;
    private final KYCDocumentRepository kycDocumentRepository;
    private final MethodTypeRepository methodTypeRepository;
    private final VendorDataRepository vendorDataRepository;

    public Page<VerificationResult> findForUser(String guid, PageRequest pageRequest) {
        User user = null;
        if ((guid != null) && (!guid.isEmpty())) {
            user = userRepository.findOrCreateByGuid(guid, () -> new User());
        }
        DateTime startDate = DateTime.now().minusYears(10);
        DateTime endDate = DateTime.now().plusYears(10);
        Page<VerificationResult> result = verificationResultRepository.findAll(VerificationResultSpecifications.table(user, startDate, endDate), pageRequest);
        return result;
    }

    @Transactional
    public VerificationResult updateWithKycVerificationData(KycSuccessVerificationResponse kvResponse, VerificationResult verificationResult, KYCReason reason) {
        verificationResult.setSuccess(reason.success());
        if (kvResponse != null) {
            verificationResult.setResultMessage(resultMessageRepository.save(ResultMessage.builder().description(kvResponse.getResultMessageText()).build()));
            verificationResult.setLegalLastName(kvResponse.getLastName());
            verificationResult.setDob(kvResponse.getDob());
            verificationResult.setProviderRequestId(kvResponse.getProviderRequestId());
            verificationResult.setFullName(kvResponse.getFullName());
            if (kvResponse.getKycDocumentType() != null) {
                updateWithKycDocument(kvResponse, verificationResult);
            }
            if (kvResponse.getNationality() != null) {
                verificationResult.setNationality(kvResponse.getNationality());
            }
            if (kvResponse.getAddress() != null) {
                verificationResult.setAddress(kvResponse.getAddress());
            }
            if (kvResponse.getCountryOfBirth() != null) {
                verificationResult.setCountryOfBirth(kvResponse.getCountryOfBirth());
            }
            if (kvResponse.getMethodTypeUid() != null) {
                verificationResult.setMethodTypeUid(kvResponse.getMethodTypeUid());
            }
            if (kvResponse.getPhoneNumber() != null) {
                verificationResult.setPhoneNumber(kvResponse.getPhoneNumber());
            }
            if (kvResponse.getBvnUid() != null) {
                verificationResult.setBvnUid(kvResponse.getBvnUid());
            }
        }
        verificationResult.setReason(reason);
        log.debug("Successful Verification Result updated " + verificationResult);
        return saveVendorSpecificData(kvResponse, verificationResultRepository.save(verificationResult));

    }

    public VerificationResult buildAndSaveInitialVerificationResult(lithium.service.user.client.objects.User user, VerificationMethodType verificationMethodName, String providerName,
                                                                    KYCReason reason) {
        VerificationResult verificationResult = VerificationResult.builder()
                .domain(domainRepository.findOrCreateByName(user.getDomain().getName(), () -> Domain.builder().build()))
                .provider(providerRepository.findOrCreateProvider(providerName))
                .user(userRepository.findOrCreateByGuid(user.getGuid(), () -> new User()))
                .methodType(methodTypeRepository.findByName(verificationMethodName.getValue()))
                .success(reason.success())
                .createdOn(DateTime.now().toDate())
                .reason(reason)
                .build();
        return verificationResultRepository.save(verificationResult);
    }

    public void saveFailAttempt(lithium.service.user.client.objects.User user, VerificationMethodType verificationMethodName, String comment) {
        verificationResultRepository.save(
                VerificationResult.builder()
                        .domain(domainRepository.findOrCreateByName(user.getDomain().getName(), () -> Domain.builder().build()))
                        .user(userRepository.findOrCreateByGuid(user.getGuid(), () -> new User()))
                        .methodType(methodTypeRepository.findByName(verificationMethodName.getValue()))
                        .success(false)
                        .createdOn(DateTime.now().toDate())
                        .resultMessage(resultMessageRepository.save(ResultMessage.builder().description(comment).build()))
                        .build());
    }

    public void updateWithKycDocument(KycSuccessVerificationResponse kvResponse, VerificationResult verificationResult) {
        byte[] doc = null;
        String cleanedBody = kvResponse.getDocumentBody().replace("\n", "").replace("\r", "");
        try {
            doc = Base64.getDecoder().decode(cleanedBody);
        } catch (IllegalArgumentException e) {
            log.error("Can't convert document(" + verificationResult.getUser().getGuid() + ", " + kvResponse.getProviderRequestId()
                    + ") to base64: " + kvResponse.getDocumentBody() + ". Error: " + e.getMessage());
        }
        if (doc != null) {
            KYCDocument kycDocument = KYCDocument.builder()
                    .type(KYCDocumentType.fromId(kvResponse.getKycDocumentType()))
                    .body(doc)
                    .build();
            verificationResult.setDocument(kycDocumentRepository.save(kycDocument));
        }
    }

    @Transactional
    public VerificationResult saveVendorSpecificData(KycSuccessVerificationResponse kycResponse, VerificationResult result) {
        if (kycResponse == null || kycResponse.getVendorsData() == null) {
            return result;
        }
        if (result.getVendorsData() != null) {
            vendorDataRepository.deleteAll(result.getVendorsData());
        }
        List<VendorData> vendorDataEntries = new ArrayList<>();
        for ( lithium.service.kyc.provider.objects.VendorData vendorData : kycResponse.getVendorsData()) {
            if (vendorData.getData() == null) {
                continue;
            }
            vendorData.getData().entrySet().forEach(d -> vendorDataEntries.add(VendorData.builder()
                                                                                .vendor(vendorData.getName())
                                                                                .name(d.getKey())
                                                                                .value(d.getValue())
                                                                                .result(result)
                                                                                .build()));
        }
        vendorDataRepository.saveAll(vendorDataEntries);
        return verificationResultRepository.findOne(result.getId());
    }
}
