package lithium.service.kyc.provider.smileindentity.api.controller;

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
import lithium.service.kyc.provider.smileindentity.api.schema.ReportResponse;
import lithium.service.kyc.provider.smileindentity.api.schema.ResolveDobResponse;
import lithium.service.kyc.provider.smileindentity.service.SmileIdentityVerifyService;
import lithium.service.kyc.provider.smileindentity.service.util.SmileIdentityIdType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/system")
@Slf4j
public class SystemController implements KycVerifyClient {
    @Autowired
    private SmileIdentityVerifyService smileIdentityVerifyService;

    private static final int PHONE_NUMBER_LENGTH = 10;
	private static final int PHONE_NUMBER_WITH_PREFIX_LENGTH = 11;
	private static final int PHONE_COUNTRY_CODE_LENGTH = 3;
	private static final int PHONE_NUMBER_WITH_COUNTRY_CODE_LENGTH = 13;
	private static final int PHONE_NUMBER_PREFIX_LENGTH = 1;
	private static final String PHONE_NUMBER_PREFIX = "0";

    @TimeThisMethod
    @PostMapping(value = "/verify")
    public ResponseEntity<KycSuccessVerificationResponse> verify(@RequestBody VerifyRequest verifyRequest)
            throws Status512ProviderNotConfiguredException, Status520KycProviderEndpointException, Status515SignatureCalculationException,
            Status407InvalidVerificationIdException, Status406InvalidVerificationNumberException, Status504KycProviderEndpointUnavailableException,
            Status424KycVerificationUnsuccessfulException, Status500InternalServerErrorException, Status428KycMismatchLastNameException, Status429KycMismatchDobException {
        log.info("{}", verifyRequest);

        Map<String, String> fields = verifyRequest.getFieldsAsMap();
        String guid = fields.get(VerifyIdParameters.GUID_PARAM);
        VerificationMethodType methodType = verifyRequest.getVerificationMethodName();
        String idType = SmileIdentityIdType.resolveIdType(methodType);
        prepareNinPhoneNumber(guid, methodType, fields);
        ReportResponse verifyReport = smileIdentityVerifyService.doVerifyId(idType, fields, guid);

        if ("1012".equals(verifyReport.getResultCode())) {
            log.info("User " + guid + " verified at Smileidentity, using  " + verifyReport.getIdType());
            String verifiedLastName = smileIdentityVerifyService.resolveLastName(idType, verifyReport.getFullData());
	        ResolveDobResponse dobResponse = smileIdentityVerifyService.resolveDob(idType, verifyReport.getFullData());
            KycSuccessVerificationResponse response = smileIdentityVerifyService.buildKycSuccessVerificationResponse(idType, verifyReport, verifiedLastName, dobResponse);
            return ResponseEntity.ok(response);
        } else {
            log.info("User " + guid + " not verified at Smileidentity. " + verifyReport.getResultText() + "(" + verifyReport.getResultCode() + ")");
            if ("1014".equals(verifyReport.getResultCode())) {
                throw new Status406InvalidVerificationNumberException("We're unable to verify your account. Please enter your ID number in the correct format or try again with another method.");
            }
            if ("1015".equals(verifyReport.getResultCode())) {
                throw new Status504KycProviderEndpointUnavailableException("We're unable to verify your account. This method is temporary unavailable, please try again with another verification method.");
            }
            throw new Status424KycVerificationUnsuccessfulException("We're unable to verify your account. Please use another method or update your details and retry.");
        }
    }

	@Override
	@GetMapping(value = "/banks")
	public List<KycBank> banks(@RequestParam("domainName") String domainName) {
    	try {
		    return smileIdentityVerifyService.getBankList(domainName);
	    } catch (Exception ex) {
    		log.error("Error during getting bank list fron SmileId", ex);
    		return new ArrayList<>();
	    }
	}

	private void prepareNinPhoneNumber(String userGuid, VerificationMethodType verificationMethodName, Map<String, String> fields) throws Status406InvalidVerificationNumberException {
    	if (VerificationMethodType.METHOD_NIN_PHONE_NUMBER.equals(verificationMethodName)) {
    		// kyc phone number format :  "0"+10 digits
		    String identificationNumber = fields.get("identificationNumber");
		    if (identificationNumber.startsWith("+")) {
			    identificationNumber = identificationNumber.replace("+", "");
		    }
		    switch (identificationNumber.length()) {
			    case PHONE_NUMBER_LENGTH : {
			    	// phone number without code
				    fields.put("identificationNumber", PHONE_NUMBER_PREFIX + identificationNumber);
				    break;
			    }
			    case PHONE_NUMBER_WITH_PREFIX_LENGTH: {
			    	// code is correct
			    	if (identificationNumber.startsWith(PHONE_NUMBER_PREFIX)) return;
			    	// wrong code. fix it
				    fields.put("identificationNumber", PHONE_NUMBER_PREFIX + identificationNumber.substring(PHONE_NUMBER_PREFIX_LENGTH, PHONE_NUMBER_WITH_PREFIX_LENGTH));
			    	break;
			    }
			    case PHONE_NUMBER_WITH_COUNTRY_CODE_LENGTH: {
			    	// number contains country code. fix it
				    fields.put("identificationNumber", PHONE_NUMBER_PREFIX + identificationNumber.substring(PHONE_COUNTRY_CODE_LENGTH, PHONE_NUMBER_WITH_COUNTRY_CODE_LENGTH));
				    break;
			    }
			    default : {
			    	String message = "Cant parse phone number =["+identificationNumber+"] for player guid=["+userGuid+"]";
			    	log.warn(message);
				    throw new Status406InvalidVerificationNumberException(message);
			    }
		    }
		    log.debug("Kyc verification for player=["+userGuid+"] after checking=["+fields.get("identificationNumber")+"]");
	    }
	}
}
