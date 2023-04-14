package lithium.service.kyc.provider.paystack.services;

import lithium.service.kyc.provider.exceptions.Status406InvalidVerificationNumberException;
import lithium.service.kyc.provider.exceptions.Status424KycVerificationUnsuccessfulException;
import lithium.service.kyc.provider.exceptions.Status425IllegalUserStateException;
import lithium.service.kyc.provider.exceptions.Status426PlayerUnderAgeException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.exceptions.Status520KycProviderEndpointException;
import lithium.service.kyc.provider.paystack.config.BrandsConfigurationBrand;
import lithium.service.kyc.provider.paystack.data.objects.BvnResolveResponse;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.util.EnumSet;

@Data
@Slf4j
@AllArgsConstructor
@Service
public class PaystackService {

	private final RestService restService;
	private final BvnVerificationAdvice bvnVerificationAdvice;
	private final ApiService apiService;
	private final VerificationResultService verificationResultService;

	public VerificationStatus verifyBvn(String bvnNumber, String userGuid) throws Status520KycProviderEndpointException,
			Status425IllegalUserStateException,
			UserClientServiceFactoryException,
			Status424KycVerificationUnsuccessfulException,
			Status426PlayerUnderAgeException,
			Status512ProviderNotConfiguredException,
			Status406InvalidVerificationNumberException {
		BvnResolveResponse bvnResolveResponse = null;
		String comment = null;
		try {
			VerificationStatus currentStatus = bvnVerificationAdvice.currentStatus(userGuid);
			EnumSet<VerificationStatus> verifiedStatus = EnumSet.of(VerificationStatus.EXTERNALLY_VERIFIED,
					VerificationStatus.MANUALLY_VERIFIED, VerificationStatus.SOF_VERIFIED);
			if (currentStatus != null && verifiedStatus.contains(currentStatus)) {
				return currentStatus;
			}
			bvnResolveResponse = bvnResolveBvn(bvnNumber, userGuid);
			log.info("{}", bvnResolveResponse);
			VerificationStatus adviceStatus = bvnVerificationAdvice.advice(userGuid, bvnResolveResponse);
			UserVerificationStatusUpdate statusUpdate = UserVerificationStatusUpdate.builder()
					.statusId(adviceStatus.getId())
					.userGuid(userGuid)
					.build();
			apiService.updateVerificationStatus(statusUpdate);
			return adviceStatus;
		} catch (ParseException ex) {
            log.error("Formatted Date of Birth returned by Paystack: "  + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(ex)));
			throw new Status520KycProviderEndpointException("Paystack provider: Invalid Date of Birth. ");
		} catch (Exception e) {
			comment = "Error verifying bvh: "+ e.getMessage();
			throw e;
		} finally {
			verificationResultService.sendVerificationAttempt(bvnResolveResponse, userGuid, false, comment);
		}
	}

	public BvnResolveResponse bvnResolveBvn(String bvnNumber, String userGuid) throws Status520KycProviderEndpointException, Status512ProviderNotConfiguredException, Status406InvalidVerificationNumberException {
		log.debug("BvnNumber {}", bvnNumber);
		BrandsConfigurationBrand brand = apiService.getBrandsConfigurationBrand(userGuid);
		if (StringUtils.isBlank(bvnNumber) || !StringUtils.isNumeric(bvnNumber)) {
			log.error("Invalid Bvn Number {}", bvnNumber);
			throw new Status406InvalidVerificationNumberException("Invalid Bvn Number");
		}

		if (StringUtils.length(bvnNumber) != brand.getBvnLength() ){
			log.error("Invalid Bvn Number {}", bvnNumber);
			throw new Status406InvalidVerificationNumberException("Invalid Bvn Number");
		}
		HttpHeaders headers = setHeaders(brand.getApiKey());
		HttpEntity<?> requestEntity = new HttpEntity<>(headers);
		RestTemplate restTemplate = restService.restTemplate(brand.getConnectTimeout(), brand.getConnectionRequestTimeout(), brand.getSocketTimeout());
		try {
			String url = brand.getPlatformUrl() + "/" + bvnNumber;
			ResponseEntity<BvnResolveResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, BvnResolveResponse.class);
			log.info("{}", responseEntity);
			if (responseEntity != null && responseEntity.getBody() != null) {
				log.debug("{}", responseEntity.getBody());
				return responseEntity.getBody();
			} else {
				log.error("Paystack failed to process request {}", brand.getPlatformUrl());
				throw new Status520KycProviderEndpointException("We're unable to verify your account at the moment. Please use another method or try again later.");
			}
		} catch (RuntimeException ex) {
            log.error("Error verifying bvn " + bvnNumber + " due " + ex.getMessage(), ex);
			throw new Status520KycProviderEndpointException("We're unable to verify your account at the moment. Please use another method or try again later.");
		}
	}

	private HttpHeaders setHeaders(String apiKey) {
		log.debug(apiKey);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer "+ apiKey);
		headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		log.debug(String.valueOf(headers));
		return headers;
	}
}
