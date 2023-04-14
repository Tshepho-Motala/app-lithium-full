package lithium.service.kyc.provider.paystack.controllers;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.TimeThisMethod;
import lithium.service.kyc.provider.clients.KycVerifyClient;
import lithium.service.kyc.provider.config.VerifyIdParameters;
import lithium.service.kyc.provider.exceptions.Status406InvalidVerificationNumberException;
import lithium.service.kyc.provider.exceptions.Status407InvalidVerificationIdException;
import lithium.service.kyc.provider.exceptions.Status424KycVerificationUnsuccessfulException;
import lithium.service.kyc.provider.exceptions.Status428KycMismatchLastNameException;
import lithium.service.kyc.provider.exceptions.Status429KycMismatchDobException;
import lithium.service.kyc.provider.exceptions.Status504KycProviderEndpointUnavailableException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.exceptions.Status515SignatureCalculationException;
import lithium.service.kyc.provider.exceptions.Status520KycProviderEndpointException;
import lithium.service.kyc.provider.objects.KycBank;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.kyc.provider.objects.VerificationMethodType;
import lithium.service.kyc.provider.objects.VerifyRequest;
import lithium.service.kyc.provider.paystack.data.objects.BvnResolveResponse;
import lithium.service.kyc.provider.paystack.services.PaystackService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Slf4j
@RestController
@RequestMapping("/system")
public class SystemVerificationController implements KycVerifyClient {

    @Autowired
    private PaystackService paystackService;

    @TimeThisMethod
    @PostMapping(value = "/verify")
    public ResponseEntity<KycSuccessVerificationResponse> verify(@RequestBody VerifyRequest verifyRequest)
            throws Status512ProviderNotConfiguredException, Status520KycProviderEndpointException, Status515SignatureCalculationException,
            Status407InvalidVerificationIdException, Status406InvalidVerificationNumberException, Status504KycProviderEndpointUnavailableException,
            Status424KycVerificationUnsuccessfulException, Status500InternalServerErrorException, Status428KycMismatchLastNameException, Status429KycMismatchDobException {

        log.debug("{}", verifyRequest);
        if (!VerificationMethodType.METHOD_BVN.equals(verifyRequest.getVerificationMethodName())) {
            log.warn("Unsupported verification type: " + verifyRequest.getVerificationMethodName());
            throw new Status407InvalidVerificationIdException("Paystack: Unsupported verification type: " + verifyRequest.getVerificationMethodName());
        }

        Map<String, String> fields = verifyRequest.getFieldsAsMap();
        if (isNull(fields.get(VerifyIdParameters.ID_NUMBER_PARAM))) {
            log.warn("Invalid Bvn Number");
            throw new Status406InvalidVerificationNumberException("We're unable to verify your account. Please enter your ID number in the correct format or try again with another method.");
        }

        BvnResolveResponse bvnResult = paystackService.bvnResolveBvn(fields.get(VerifyIdParameters.ID_NUMBER_PARAM), fields.get(VerifyIdParameters.GUID_PARAM));
        log.info("{}", bvnResult);

        if (StringUtils.isNotBlank(bvnResult.getStatus()) && "true".equalsIgnoreCase(bvnResult.getStatus())) {
            String lastName = bvnResult.getData().getLastName();
            String dob = bvnResult.getData().getFormattedDob();

            KycSuccessVerificationResponse response = KycSuccessVerificationResponse.builder()
                    .lastName(lastName)
                    .dob(dob)
                    .dobYearOnly(false)
                    .manual(false)
                    .success(true)
                    .createdOn(DateTime.now().toDate())
                    .resultMessageText(bvnResult.getMessage())
                    .methodTypeUid(bvnResult.getData().getBvn())
                    .providerRequestId("Request id not provided")
                    .build();
            return ResponseEntity.ok(response);
        }
        log.info("Paystack service can't verify user: " + bvnResult);
        throw new Status424KycVerificationUnsuccessfulException("We're unable to verify your account. Please use another method or update your details and retry.");
    }

	@Override
	@GetMapping(value = "/banks")
	public List<KycBank> banks(@RequestParam("domainName") String domainName) throws Exception {
		throw new Status500InternalServerErrorException("Unsupported provider operation");
	}
}
