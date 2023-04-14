package lithium.service.kyc.api.controller;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.kyc.entities.VerificationResult;
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
import lithium.service.kyc.provider.objects.KycBank;
import lithium.service.kyc.provider.objects.VerificationMethodType;
import lithium.service.kyc.provider.objects.VerifyParam;
import lithium.service.kyc.provider.objects.VerifyRequest;
import lithium.service.kyc.schema.BOVerificationRequest;
import lithium.service.kyc.schema.VerificationStatusResponse;
import lithium.service.kyc.service.UpdateVerificationStatusService;
import lithium.service.kyc.service.VerificationResultsService;
import lithium.service.stats.client.exceptions.Status513StatsServiceUnavailableException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static lithium.service.kyc.provider.config.VerifyIdParameters.BANK_CODE_PARAM;
import static lithium.service.kyc.provider.config.VerifyIdParameters.GUID_PARAM;
import static lithium.service.kyc.provider.config.VerifyIdParameters.ID_NUMBER_PARAM;
import static lithium.service.kyc.provider.config.VerifyIdParameters.LAST_NAME_PARAM;

@RestController
@RequestMapping("/backoffice/kyc")
@Slf4j
public class BackofficeController {
    @Autowired
    VerificationResultsService service;
	@Autowired
	private UpdateVerificationStatusService updateVerificationStatusService;
	@Autowired
	private UserApiInternalClientService userService;

    @PostMapping("/table")
    public DataTableResponse<VerificationResult> table(
            @RequestParam(name = "userGuid") String userGuid,
            @RequestParam(name = "start") int start,
            @RequestParam(name = "length") int length,
            @RequestParam(name = "order[0][dir]", required = false) String orderDirection) {
        log.debug("KYC Verification Data table requested  userGuid=" + userGuid + ", start=" + start + ", length=" + length);
        PageRequest pageRequest = PageRequest.of(start / length, length, Sort.Direction.fromString(orderDirection), new String[]{"id"});
        DataTableRequest request = new DataTableRequest();
        Page<VerificationResult> results =
                service.findForUser(userGuid, pageRequest);
        request.setPageRequest(pageRequest);
        log.debug("For KYC Verification table request with guid: " + userGuid + ", " + results.getTotalElements() + " Records found:" + results.getContent());
        return new DataTableResponse<>(request, results);
    }

	@PostMapping(value = "/verify")
	public Response<VerificationStatusResponse> verify(@RequestBody BOVerificationRequest verifyRequest)
			throws LithiumServiceClientFactoryException, UserNotFoundException, UserClientServiceFactoryException,
			Status520KycProviderEndpointException, Status504KycProviderEndpointUnavailableException, Status515SignatureCalculationException,
			Status407InvalidVerificationIdException, Status406InvalidVerificationNumberException, Status424KycVerificationUnsuccessfulException,
			Status512ProviderNotConfiguredException, Status425IllegalUserStateException, Status400BadRequestException, Status550ServiceDomainClientException,
			Status513StatsServiceUnavailableException, Status427UserKycVerificationLifetimeAttemptsExceeded, Status500InternalServerErrorException,
			Status428KycMismatchLastNameException, Status429KycMismatchDobException {

		if (verifyRequest == null || verifyRequest.getIdentifier() == null) {
			throw new Status406InvalidVerificationNumberException("Invalid Phone Number");
		}

    	User user = userService.getUserByGuid(verifyRequest.getUserGuid());
    	if (user == null) {
		    log.error("Cant find user by guid=" + verifyRequest.getUserGuid());
		    throw new UserNotFoundException("Cant find user by guid=" + verifyRequest.getUserGuid());
	    }

		VerificationStatusResponse response = updateVerificationStatusService.verify(buildVerifyRequest(verifyRequest, user), verifyRequest.getUserGuid());

		return Response.<VerificationStatusResponse>builder().data(response).status(Response.Status.OK).build();
	}

	private VerifyRequest buildVerifyRequest(
		BOVerificationRequest boVerifyRequest,
		User user
	) throws Status400BadRequestException {

		VerificationMethodType verificationMethodType = VerificationMethodType.findByValue(boVerifyRequest.getMethodType());
		if (verificationMethodType == null) {
			log.error("Cant find VerificationMethodType by value=" + boVerifyRequest.getMethodType());
			throw new Status400BadRequestException("Invalid parameter verificationMethodName=" + boVerifyRequest.getMethodType());
		}

		VerifyRequest verifyRequest = new VerifyRequest();
		verifyRequest.setVerificationMethodName(verificationMethodType);

		VerifyParam identificationNumber = new VerifyParam();
		identificationNumber.setKey(ID_NUMBER_PARAM);
		identificationNumber.setValue(boVerifyRequest.getIdentifier());

		VerifyParam bankCode = new VerifyParam();
		bankCode.setKey(BANK_CODE_PARAM);
		bankCode.setValue(boVerifyRequest.getBankCode());

		VerifyParam lastName = new VerifyParam();
		lastName.setKey(LAST_NAME_PARAM);
		lastName.setValue(user.getLastName());

		VerifyParam guid = new VerifyParam();
		guid.setKey(GUID_PARAM);
		guid.setValue(user.guid());

		List<VerifyParam> fields = new ArrayList<>();
		fields.add(identificationNumber);
		fields.add(bankCode);
		fields.add(lastName);
		fields.add(guid);

		verifyRequest.setFields(fields);
		return verifyRequest;
	}

	@GetMapping("/banks")
	public ResponseEntity<List<KycBank>> banks(
			@RequestParam(name = "userGuid") String userGuid,
			@RequestParam(name = "provider") String provider
	) throws Exception, UserClientServiceFactoryException {

		User user = userService.getUserByGuid(userGuid);
		if (user == null) {
			log.error("Cant find user by guid=" + userGuid);
			throw new UserNotFoundException("Cant find user by guid=" + userGuid);
		}
		List<KycBank> response = updateVerificationStatusService.banks(provider, user.getDomain().getName());
		return ResponseEntity.ok(response);
	}
}