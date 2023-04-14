package lithium.service.kyc.provider.smileindentity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.rest.EnableRestTemplate;
import lithium.service.kyc.entities.KYCDocumentType;
import lithium.service.kyc.provider.config.VerifyIdParameters;
import lithium.service.kyc.provider.exceptions.Status428KycMismatchLastNameException;
import lithium.service.kyc.provider.exceptions.Status429KycMismatchDobException;
import lithium.service.kyc.provider.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.kyc.provider.exceptions.Status515SignatureCalculationException;
import lithium.service.kyc.provider.exceptions.Status520KycProviderEndpointException;
import lithium.service.kyc.provider.objects.KycBank;
import lithium.service.kyc.provider.objects.KycBankResponse;
import lithium.service.kyc.provider.objects.KycSuccessVerificationResponse;
import lithium.service.kyc.provider.smileindentity.ServiceKycSmileIdentityModuleInfo;
import lithium.service.kyc.provider.smileindentity.api.schema.IdVerifyRequest;
import lithium.service.kyc.provider.smileindentity.api.schema.PartnerParams;
import lithium.service.kyc.provider.smileindentity.api.schema.ReportResponse;
import lithium.service.kyc.provider.smileindentity.api.schema.ResolveDobResponse;
import lithium.service.kyc.provider.smileindentity.config.ProviderConfig;
import lithium.service.kyc.provider.smileindentity.config.ProviderConfigService;
import lithium.service.kyc.provider.smileindentity.service.util.Signature;
import lithium.service.kyc.provider.smileindentity.service.util.SmileIdentityIdType;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static java.util.Objects.nonNull;

@Service
@Slf4j
@EnableRestTemplate
public class SmileIdentityVerifyService {
	private final static Integer VERIFY_ID_JOB_TYPE = 5;
	private static final String NATIONALITY = "Nationality";
	private static final String PLACE_OF_BIRTH = "PlaceOfBirth";
	private static final String BVN_NUMBER = "BVNNumber";
	private final static List<String> DATE_FORMAT = Arrays.asList("dd-MMM-yy", "dd/MMM/yyyy", "yyyy-MM-dd");
	private final ObjectMapper mapper = new ObjectMapper();
	private final ProviderConfigService configService;
	private final ServiceKycSmileIdentityModuleInfo moduleInfo;
	private final RestService restService;

	@Autowired
	public SmileIdentityVerifyService(RestService restService,
	                                  ProviderConfigService configService, ServiceKycSmileIdentityModuleInfo moduleInfo) {
		this.restService = restService;
		this.configService = configService;
		this.moduleInfo = moduleInfo;
	}

	public ReportResponse doVerifyId(String type, Map<String, String> fields, String guid) throws Status515SignatureCalculationException, Status520KycProviderEndpointException, Status512ProviderNotConfiguredException, Status500InternalServerErrorException {

		String idNumber = fields.get(VerifyIdParameters.ID_NUMBER_PARAM);
		String bankCode = fields.get(VerifyIdParameters.BANK_CODE_PARAM);
		String domainName = fields.get(VerifyIdParameters.DOMAIN_NAME_PARAM);
		String firstName = fields.get(VerifyIdParameters.FIRST_NAME_PARAM);
		String lastName = fields.get(VerifyIdParameters.LAST_NAME_PARAM);
		String dob = fields.get(VerifyIdParameters.DOB_PARAM);

		ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), domainName);
		Long timestamp = System.currentTimeMillis();
		String secretKey = new Signature(config.getPartnerId(), config.getApiKey()).generateSecKey(timestamp);

		IdVerifyRequest verifyRequest = new IdVerifyRequest().builder()
				.partnerId(config.getPartnerId())
				.timestamp(timestamp)
				.secKey(secretKey)
				.country(config.getCountry())
				.idType(type)
				.idNumber(idNumber)
				.bankCode(bankCode)
				.firstName(firstName)
				.lastName(lastName)
				.dob(dob)
				.partnerParams(new PartnerParams().builder()
						.jobId(guid + "_" + type + "_" + timestamp)
						.userId(guid)
						.jobType(VERIFY_ID_JOB_TYPE)
						.build()
				)
				.build();

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Content-Type", "application/json");

		HttpEntity<IdVerifyRequest> entity = new HttpEntity<>(verifyRequest, headers);

		log.debug("Sending verify id request: " + entity);

		RestTemplate restTemplate = restService.restTemplate(config.getConnectTimeout(), config.getConnectionRequestTimeout(), config.getSocketTimeout());
		ResponseEntity<String> exchange =
				restTemplate.exchange(config.getVerifyApiUrl(), HttpMethod.POST, entity, String.class);

		log.debug("Got verify id response(" + guid + "): " + exchange.getBody());

		if (exchange.getStatusCode().is2xxSuccessful()) {
			try {
				return mapper.readValue(exchange.getBody(), ReportResponse.class);
			} catch (IOException e) {
				log.error("Can't convert " + exchange.getBody() + " to object, ", e);
				throw new Status520KycProviderEndpointException("We're unable to verify your account at the moment. Please use another method or try again later.");
			}
		} else {
			log.error("Can't get ok response from SmileIdentity: " + exchange.getStatusCode() + " (" + exchange.getBody() + ")");
			throw new Status520KycProviderEndpointException("We're unable to verify your account at the moment. Please use another method or try again later.");
		}
	}

	public String resolveLastName(String idType, Map<String, Object> fullData) throws Status428KycMismatchLastNameException {
		String lastName = null;
		if (SmileIdentityIdType.BVN.equals(idType) || SmileIdentityIdType.VOTER_ID.equals(idType) || SmileIdentityIdType.BANK_ACCOUNT.equals(idType)) {
			lastName = (String) fullData.get("LastName");
		} else if (SmileIdentityIdType.NIN.equals(idType)) {
			lastName = (String) fullData.get("surname");
		} else if (SmileIdentityIdType.PASSPORT.equals(idType) || SmileIdentityIdType.DRIVERS_LICENSE.equals(idType) || SmileIdentityIdType.NATIONAL_ID.equals(idType)) {
			lastName = (String) fullData.get("lastName");
		}

		if (nonNull(lastName)) {
			return lastName;
		}
		log.warn("Can't parse last name from VerificationId response (" + idType + ")");
		throw new Status428KycMismatchLastNameException();
	}

	public ResolveDobResponse resolveDob(String idType, Map<String, Object> fullData) throws Status429KycMismatchDobException {
		String dob = null;
		try {
			if (SmileIdentityIdType.BVN.equalsIgnoreCase(idType)) {
				dob = (String) fullData.get("DateOfBirth");
				if (nonNull(dob)) {
					return new ResolveDobResponse(DateTime.parse(dob, DateTimeFormat.forPattern("dd-MMMM-YYYY")));
				}
			} else if (SmileIdentityIdType.PASSPORT.equalsIgnoreCase(idType) || SmileIdentityIdType.DRIVERS_LICENSE.equalsIgnoreCase(idType) || SmileIdentityIdType.NATIONAL_ID.equalsIgnoreCase(idType)) {
				dob = (String) fullData.get("dateOfBirth");
				if (nonNull(dob)) {
					return new ResolveDobResponse(DateTime.parse(dob, DateTimeFormat.forPattern("YYYY-MM-dd")));
				}
			} else if (SmileIdentityIdType.NIN.equalsIgnoreCase(idType)) {
				dob = (String) fullData.get("birthdate");
				if (nonNull(dob)) {
					return new ResolveDobResponse(DateTime.parse(dob, DateTimeFormat.forPattern("dd-MM-YYYY")));
				}
			} else if (SmileIdentityIdType.VOTER_ID.equalsIgnoreCase(idType)) {
				dob = (String) fullData.get("DOB_Y");
				if (nonNull(dob)) {
					return new ResolveDobResponse(DateTime.parse(dob, DateTimeFormat.forPattern("YYYY")), true);
				}
			} else if (SmileIdentityIdType.BANK_ACCOUNT.equalsIgnoreCase(idType)) {
				dob = (String) fullData.get("DOB");
				if (nonNull(dob) && !dob.isEmpty()) {
					return new ResolveDobResponse(DateTime.parse(dob, DateTimeFormat.forPattern("dd/MM/YYYY")));
				}
			}
		} catch (IllegalArgumentException e) {
			return new ResolveDobResponse(tryAnotherDatePattern(dob));
		}
		log.warn("Can't parse dob from VerificationId response (" + idType + ")");
		throw new Status429KycMismatchDobException();
	}

	private DateTime tryAnotherDatePattern(String dob) throws Status429KycMismatchDobException {
		for (String format : DATE_FORMAT) {
			try {
				return DateTime.parse(dob, DateTimeFormat.forPattern(format));
			} catch (IllegalArgumentException e) {

			}
		}
		log.error("Can't parse dob >" + dob + "< from VerificationId response");
		throw new Status429KycMismatchDobException();
	}

	public KycSuccessVerificationResponse buildKycSuccessVerificationResponse(String idType, ReportResponse verifyReport, String verifiedLastName, ResolveDobResponse dobResponse) {
		KycSuccessVerificationResponse response = KycSuccessVerificationResponse.builder()
				.lastName(verifiedLastName)
				.fullName(verifyReport.getFullName())
				.dob(dobResponse.getDob().toString(DateTimeFormat.forPattern("yyyy-MM-dd")))
				.dobYearOnly(dobResponse.isDobYearOnly())
				.providerRequestId(verifyReport.getSmileJobID())
				.resultMessageText(verifyReport.getResultType() + "-" + verifyReport.getResultText())
				.address(verifyReport.getAddress())
				.success(true)
				.manual(false)
				.countryOfBirth(verifyReport.getCountry())
				.createdOn(DateTime.now().toDate())
				.methodTypeUid(verifyReport.getIdNumber())
				.phoneNumber(verifyReport.getPhoneNumber())
				.build();
		if (verifyReport.getFullData().containsKey(NATIONALITY)) {
			response.setNationality(String.valueOf(verifyReport.getFullData().get(NATIONALITY)));
		}
		if (verifyReport.getFullData().containsKey(PLACE_OF_BIRTH)) {
			response.setCountryOfBirth(String.valueOf(verifyReport.getFullData().get(PLACE_OF_BIRTH)));
		}
		if (verifyReport.getPhoto() != null && !verifyReport.getPhoto().isEmpty()
				&& !"Not Available".equals(verifyReport.getPhoto())) {
			response.setKycDocumentType(KYCDocumentType.PHOTO_BASE64.id());
			response.setDocumentBody(verifyReport.getPhoto());
		}
		if (verifyReport.getFullData().containsKey(BVN_NUMBER)) {
			response.setBvnUid(String.valueOf(verifyReport.getFullData().get(BVN_NUMBER)));
		}
		return response;
	}

	public List<KycBank> getBankList(String domainName) throws Status512ProviderNotConfiguredException, Status500InternalServerErrorException, Status520KycProviderEndpointException {
		ProviderConfig config = configService.getConfig(moduleInfo.getModuleName(), domainName);
		RestTemplate restTemplate = restService.restTemplate(config.getConnectTimeout(), config.getConnectionRequestTimeout(), config.getSocketTimeout());
		ResponseEntity<KycBankResponse> exchange =
				restTemplate.exchange(config.getBankListUrl(), HttpMethod.GET, new HttpEntity<>(new LinkedMultiValueMap<>()), KycBankResponse.class);
		log.debug("KycBankResponse: " + exchange);
		if (exchange.getStatusCode().is2xxSuccessful()) {
			return exchange.getBody().getBankCodes();
		}
		return new ArrayList<>();
	}
}
