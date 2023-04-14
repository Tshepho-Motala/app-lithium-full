package lithium.service.kyc.service;

import com.google.common.collect.ImmutableMap;
import lithium.service.kyc.entities.VerificationResult;
import lithium.service.kyc.exceptions.Status405KycWrongProviderName;
import lithium.service.kyc.exceptions.Status406KycWrongMethodName;
import lithium.service.kyc.repositories.VerificationResultRepository;
import lithium.service.kyc.schema.KycAttemptsCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class KycAttemptsService {

    @Autowired
    VerificationResultRepository verificationResultRepository;

    private static ImmutableMap<String, List<String>> names = ImmutableMap.<String, List<String>>builder()
            .put("GBG(ID3Global)", new ArrayList<String>(Arrays.asList("AuthenticateSP")))
            .put("Transunion", new ArrayList<String>(Arrays.asList("CallValidate5")))
            .put("kyc-smileidentity", new ArrayList<String>(Arrays.asList("METHOD_BANK_ACCOUNT", "METHOD_BVN", "METHOD_NIN", "METHOD_NATIONAL_ID", "METHOD_PASSPORT", "METHOD_DRIVERS_LICENSE", "METHOD_VOTER_ID")))
            .put("Paystack", new ArrayList<String>(Arrays.asList("BVN Check")))
            .put("service-document-provider-hellosoda", new ArrayList<String>(Arrays.asList("facebook")))
            .put("SphonicKYC", new ArrayList<String>(Arrays.asList("SphonicKYC")))
            .build();


    public KycAttemptsCheckResponse checkUserAttempts(String guid, String providerName, String methodName) throws Status405KycWrongProviderName, Status406KycWrongMethodName {
        if (!names.containsKey(providerName)) {
            log.warn("A check Kyc attempts request for user: " + guid + "  has been received with incorrect providerName:" + providerName);
            throw new Status405KycWrongProviderName();
        } else if (!names.get(providerName).contains(methodName)) {
            log.warn("A check Kyc attempts request for user: " + guid + "  has been received with incorrect methodName:" + methodName);
            throw new Status406KycWrongMethodName();
        }

        KycAttemptsCheckResponse response = new KycAttemptsCheckResponse();
        Long count = verificationResultRepository.countAllByUserGuidAndProviderGuidAndMethodTypeName(guid, providerName, methodName);
        response.setMethodUsed(count > 0);

        return response;
    }
}
