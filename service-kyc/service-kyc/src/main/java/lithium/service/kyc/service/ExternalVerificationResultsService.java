package lithium.service.kyc.service;


import lithium.service.kyc.client.exceptions.Status459VerificationResultNotFountException;
import lithium.service.kyc.client.objects.VerificationKycAttempt;
import lithium.service.kyc.entities.MethodType;
import lithium.service.kyc.entities.ResultMessage;
import lithium.service.kyc.entities.User;
import lithium.service.kyc.entities.VendorData;
import lithium.service.kyc.entities.VerificationResult;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.kyc.repositories.DomainRepository;
import lithium.service.kyc.repositories.MethodTypeRepository;
import lithium.service.kyc.repositories.ProviderRepository;
import lithium.service.kyc.repositories.ResultMessageRepository;
import lithium.service.kyc.repositories.UserRepository;
import lithium.service.kyc.repositories.VendorDataRepository;
import lithium.service.kyc.repositories.VerificationResultRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@AllArgsConstructor
public class ExternalVerificationResultsService {

    private final VerificationResultRepository verificationResultRepository;
    private final DomainRepository domainRepository;
    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final ResultMessageRepository resultMessageRepository;
    private final MethodTypeRepository methodTypeRepository;
    private final VerificationResultsService verificationResultsService;

    @Transactional
    public VerificationResult updateExternalVerificationAttempt(VerificationKycAttempt attempt) throws Status459VerificationResultNotFountException {
        VerificationResult verificationResult = verificationResultRepository.findOne(attempt.getVerificationResultId());
        if(verificationResult == null){
            throw new Status459VerificationResultNotFountException();
        }
        KycSuccessVerificationResponse kvResponse = attempt.getKycSuccessVerificationResponse();
        ResultMessage resultMessage = resultMessageRepository.findOne(verificationResult.getResultMessage().getId());
        resultMessage.setDescription(kvResponse.getResultMessageText());
        verificationResult.setResultMessage(resultMessageRepository.save(resultMessage));
        verificationResult.setSuccess(kvResponse.isSuccess());
        if (kvResponse.getNationality() != null)
            verificationResult.setNationality(kvResponse.getNationality());
        if (kvResponse.getAddress() != null)
            verificationResult.setAddress(kvResponse.getAddress());
        if (kvResponse.getCountryOfBirth() != null)
            verificationResult.setCountryOfBirth(kvResponse.getCountryOfBirth());
        if (kvResponse.getMethodTypeUid() != null)
            verificationResult.setMethodTypeUid(kvResponse.getMethodTypeUid());
        if (kvResponse.getPhoneNumber() != null)
            verificationResult.setPhoneNumber(kvResponse.getPhoneNumber());
        if (kvResponse.getLastName() != null)
            verificationResult.setLegalLastName(kvResponse.getLastName());
        if (kvResponse.getFullName() != null)
            verificationResult.setFullName(kvResponse.getFullName());
        if (kvResponse.getDob() != null)
            verificationResult.setDob(kvResponse.getDob());
        if (nonNull(kvResponse.getAddressDecision())) {
            verificationResult.setAddressDecision(kvResponse.getAddressDecision());
        }
        if (nonNull(kvResponse.getDocumentDecision())) {
            verificationResult.setDocumentDecision(kvResponse.getDocumentDecision());
        }
        if (kvResponse.getKycDocumentType() != null) {
            verificationResultsService.updateWithKycDocument(kvResponse, verificationResult);
        }

        return verificationResultsService.saveVendorSpecificData(kvResponse, verificationResultRepository.save(verificationResult));
    }

    @Transactional
    public VerificationResult storeExternalVerificationAttempt(VerificationKycAttempt attempt) {

        KycSuccessVerificationResponse kvResponse = attempt.getKycSuccessVerificationResponse();
        VerificationResult verificationResult = VerificationResult.builder()
                .user(userRepository.findOrCreateByGuid(attempt.getUserGuid(), () -> new User()))
                .domain(domainRepository.findOrCreateByName(attempt.getDomainName(), () -> lithium.service.kyc.entities.Domain.builder().build()))
                .provider(providerRepository.findOrCreateProvider(attempt.getProviderName()))
                .methodType(methodTypeRepository.findOrCreateByName(attempt.getMethodName(), () -> new MethodType()))
                .createdOn(attempt.getKycSuccessVerificationResponse().getCreatedOn())
                .success(attempt.getKycSuccessVerificationResponse().isSuccess())
                .manual(attempt.getKycSuccessVerificationResponse().isManual())
                .resultMessage(resultMessageRepository.save(ResultMessage.builder().description(kvResponse.getResultMessageText()).build()))
                .providerRequestId(kvResponse.getProviderRequestId())
                .nationality(kvResponse.getNationality())
                .address(kvResponse.getAddress())
                .countryOfBirth(kvResponse.getCountryOfBirth())
                .methodTypeUid(kvResponse.getMethodTypeUid())
                .phoneNumber(kvResponse.getPhoneNumber())
                .legalLastName(kvResponse.getLastName())
                .fullName(kvResponse.getFullName())
                .dob(kvResponse.getDob())
                .documentDecision(attempt.getKycSuccessVerificationResponse().getDocumentDecision())
                .addressDecision(attempt.getKycSuccessVerificationResponse().getAddressDecision())
                .build();

        if (kvResponse.getKycDocumentType() != null) {
            verificationResultsService.updateWithKycDocument(kvResponse, verificationResult);
        }

        VerificationResult result = verificationResultRepository.save(verificationResult);
        verificationResultsService.saveVendorSpecificData(kvResponse, result);
        log.debug("External Verification Attempt added to DB" + result);
        return result;
    }


}
