package lithium.service.access.provider.gamstop.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.client.changelog.Category;
import lithium.client.changelog.SubCategory;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.client.changelog.ChangeLogClient;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.exceptions.Status551ServiceChangeLogClientException;
import lithium.client.changelog.objects.ChangeLog;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.access.client.exceptions.Status513InvalidDomainConfigurationException;
import lithium.service.access.client.exceptions.Status551ServiceAccessClientException;
import lithium.service.access.client.gamstop.exceptions.Status424InvalidRequestException;
import lithium.service.access.client.gamstop.exceptions.Status512ExclusionCheckException;
import lithium.service.access.client.gamstop.objects.BatchExclusionCheckRequest;
import lithium.service.access.client.gamstop.objects.BatchExclusionCheckResponse;
import lithium.service.access.client.gamstop.objects.ExclusionRequest;
import lithium.service.access.client.gamstop.objects.ExclusionRequestLine;
import lithium.service.access.client.gamstop.objects.ExclusionResult;
import lithium.service.access.client.gamstop.objects.ExclusionResultLine;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.provider.gamstop.ServiceAccessProviderGamstopModuleInfo;
import lithium.service.access.provider.gamstop.adapter.GamstopAdapter;
import lithium.service.access.provider.gamstop.config.BrandsConfigurationBrand;
import lithium.service.access.provider.gamstop.data.objects.SelfExclusionResponse;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.limit.client.ExclusionClient;
import lithium.service.limit.client.exceptions.Status489PlayerExclusionNotFoundException;
import lithium.service.limit.client.objects.ExclusionSource;
import lithium.service.limit.client.objects.PlayerExclusionV2;
import lithium.service.limit.client.schemas.exclusion.RemoveExclusionRequest;
import lithium.service.limit.client.schemas.exclusion.RemoveExclusionResponse;
import lithium.service.report.client.players.PlayersReportClient;
import lithium.service.report.client.players.exceptions.Status551ServiceReportClientException;
import lithium.service.user.client.UserApiInternalClient;
import lithium.service.user.client.enums.StatusReason;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.client.objects.User;
import lithium.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
public class ApiService {

	public final static String ADVISOR = "GAMSTOP";

	@Getter
	@Value("${spring.application.name}")
	private String moduleName;

	@Autowired
	@Setter
	private ApiClientService apiClientService;

	@Autowired
	private ChangeLogService changeLogService;
	@Autowired
	private CachingDomainClientService cachingDomainClientService;
	@Autowired
	MessageSource messageSource;
	@Autowired
	RestService restService;

	public SelfExclusionResponse checkExclusionRaw(
			String url,
			String apiKey,
			PlayerBasic playerBasic,
			RawAuthorizationData rawAuthorizationData,
			Integer connectTimeout,
			Integer connectionRequestTimeout,
			Integer socketTimeout
	) {
		RestTemplate restTemplate = restService.restTemplate(connectTimeout, connectionRequestTimeout, socketTimeout);
		GamstopAdapter adapter = GamstopAdapter.builder()
				.restTemplate(restTemplate)
				.build();
		SelfExclusionResponse res;
		try {

			String postalCode = null;
			postalCode = playerBasic.getResidentialAddress() != null ? playerBasic.getResidentialAddress().getPostalCode() : null;
			if (postalCode == null || postalCode.trim().isEmpty()) {
				postalCode = playerBasic.getPostalAddress() != null ? playerBasic.getPostalAddress().getPostalCode() : null;
			}

			res = adapter.checkExclusionRaw(
					url, apiKey, playerBasic.getFirstName(), playerBasic.getLastName(),
					playerBasic.getDobDay(), playerBasic.getDobMonth(), playerBasic.getDobYear(), playerBasic.getEmail(),
					postalCode,
					playerBasic.getCellphoneNumber());
			if (res != null && res.getExclusionType() != null) {
				ExclusionResult exclusionResult = ExclusionResult.builder()
						.exclusion(res.getExclusionType().toString())
						.msRequestId(res.getXUniqueId())
						.build();
			}
			log.info("reached end with result: " + res);
			try {
				populateRawData(rawAuthorizationData, playerBasic, res);
				log.debug("Reached end with raw result: " + new ObjectMapper().writeValueAsString(res));
			} catch (JsonProcessingException e) {
				log.error("Unable to map response object to string in SelfExclusionResponse");
			}
		} catch (Exception ex) {
			log.error("Error checking exclusion", ex);
			res = SelfExclusionResponse.builder().build();
		}
		return res;
	}

	public SelfExclusionResponse checkExclusion(
			User user,
			String url,
			String apiKey,
			RawAuthorizationData rawAuthorizationData,
			Integer connectTimeout,
			Integer connectionRequestTimeout,
			Integer socketTimeout) {
		RestTemplate restTemplate = restService.restTemplate(connectTimeout, connectionRequestTimeout, socketTimeout);
		GamstopAdapter adapter = GamstopAdapter.builder()
				.restTemplate(restTemplate)
				.build();
		SelfExclusionResponse res;
		try {
			res = adapter.checkExclusion(url, user, apiKey);
			if (res != null && res.getExclusionType() != null) {
				ExclusionResult exclusionResult = ExclusionResult.builder()
						.exclusion(res.getExclusionType().toString())
						.msRequestId(res.getXUniqueId())
						.build();
				updateExclusion(exclusionResult, user);
			}
			log.info("reached end with result: " + res);
			try {
				populateRawData(rawAuthorizationData, user, res);
				log.debug("Reached end with raw result: " + new ObjectMapper().writeValueAsString(res));
			} catch (JsonProcessingException e) {
				log.error("Unable to map response object to string in SelfExclusionResponse");
			}
		} catch (UserClientServiceFactoryException | Exception ex) {
			log.error("Error checking exclusion", ex);
			res = SelfExclusionResponse.builder().build();
		}
		return res;
	}

	public BrandsConfigurationBrand getBrandConfiguration(String providerUrl, String domainName) throws Status513InvalidDomainConfigurationException, Status550ServiceDomainClientException {
		ProviderClient cl = apiClientService.getProviderService();
		BrandsConfigurationBrand brandConfiguration = new BrandsConfigurationBrand();//external system id = providerId as stored in domain config
		if (cl != null) {
			Response<Provider> providerResponse = cl.findByUrlAndDomainName(providerUrl, domainName);
			if (providerResponse == null || providerResponse.getData() == null || !providerResponse.getData().getEnabled()) {
				log.error("Provider Not Enabled providerUrl={},domain={}", providerUrl, domainName);
				throw new Status513InvalidDomainConfigurationException("Provider Not Enabled.");
			}
			Response<Iterable<ProviderProperty>> pp = cl.propertiesByProviderUrlAndDomainName(providerUrl, domainName);
			for (ProviderProperty p: pp.getData()) {
				if (p.getName().equalsIgnoreCase(ServiceAccessProviderGamstopModuleInfo.ConfigProperties.PLATFORM_URL.getValue())) brandConfiguration.setPlatformUrl(p.getValue());
				if (p.getName().equalsIgnoreCase(ServiceAccessProviderGamstopModuleInfo.ConfigProperties.API_KEY.getValue())) brandConfiguration.setApiKey(p.getValue());
				if (p.getName().equalsIgnoreCase(ServiceAccessProviderGamstopModuleInfo.ConfigProperties.BATCH_PLATFORM_URL.getValue())) brandConfiguration.setBatchPlatformUrl(p.getValue());
				if (p.getName().equalsIgnoreCase(ServiceAccessProviderGamstopModuleInfo.ConfigProperties.CONNECTION_REQUEST_TIMEOUT.getValue())
						&& !StringUtil.isEmpty(p.getValue()) && StringUtil.isNumeric(p.getValue())) {
					brandConfiguration.setConnectionRequestTimeout(Integer.parseInt(p.getValue()));
				}
				if (p.getName().equalsIgnoreCase(ServiceAccessProviderGamstopModuleInfo.ConfigProperties.CONNECT_TIMEOUT.getValue())
						&& !StringUtil.isEmpty(p.getValue()) && StringUtil.isNumeric(p.getValue())) {
					brandConfiguration.setConnectTimeout(Integer.parseInt(p.getValue()));
				}
				if (p.getName().equalsIgnoreCase(ServiceAccessProviderGamstopModuleInfo.ConfigProperties.SOCKET_TIMEOUT.getValue())
						&& !StringUtil.isEmpty(p.getValue()) && StringUtil.isNumeric(p.getValue())) {
					brandConfiguration.setSocketTimeout(Integer.parseInt(p.getValue()));
				}
			}
		}

		return brandConfiguration;
	}

	public User getUser(String playerguid) throws UserClientServiceFactoryException {
		UserApiInternalClient cl = apiClientService.getUserService();
		Response<User> response = cl.getUser(playerguid);
		if (response.isSuccessful()) return response.getData();
		return null;
	}

	/**
	 * Utility function to populate raw transaction data into a pre-initilized data object.
	 * The requestData and responseData parameters can be null and will then be ignored for the serialization operation.
	 * @param rawAuthorizationData (output)
	 * @param requestData (input)
	 * @param responseData (input)
	 */
	private void populateRawData(RawAuthorizationData rawAuthorizationData, final Object requestData, final Object responseData) {
		if (rawAuthorizationData == null) {
			log.error("Unable to produce raw transaction data since rawAuthorizationData object is not initioalized");
		}
		if (requestData != null) {
			try {
				rawAuthorizationData.setRawRequestToProvider(new ObjectMapper().writeValueAsString(requestData));
			} catch (JsonProcessingException e) {
				log.debug("Unable to map raw transaction request for auth request: " + requestData, e);
			}
		}
		if (responseData != null) {
			try {
				rawAuthorizationData.setRawRequestToProvider(new ObjectMapper().writeValueAsString(requestData));
				if (responseData != null) {
					rawAuthorizationData.setRawResponseFromProvider(new ObjectMapper().writeValueAsString(responseData));
				}
			} catch(JsonProcessingException e){
				log.debug("Unable to map raw transaction response for auth request: " + responseData, e);
			}
		}
	}

	/**
	 * Submit a batch for exclusion check, maximum batch size is 1000
	 * @param request BatchExclusionCheckRequest
	 * @return BatchExclusionCheckResponse
	 * @throws Status513InvalidDomainConfigurationException
	 * @throws Status500InternalServerErrorException
	 * @throws Status424InvalidRequestException
	 * @throws Status424InvalidRequestException
	 */
	public BatchExclusionCheckResponse batchExclusionCheck(BatchExclusionCheckRequest request) throws Status424InvalidRequestException, Status512ExclusionCheckException, Status513InvalidDomainConfigurationException, Status551ServiceAccessClientException, UserClientServiceFactoryException, Status551ServiceReportClientException, Status550ServiceDomainClientException {
		if (request == null || CollectionUtils.isEmpty(request.getRequestData())) {
			throw new Status424InvalidRequestException("Batch Request cannot be null");
		}

		if (request.getRequestData().size() > 1000) {
			throw new Status424InvalidRequestException("Batch size not supported");
		}

		BrandsConfigurationBrand config = getBrandConfiguration(moduleName, request.getDomainName());
		RestTemplate restTemplate = restService.restTemplate(config.getConnectTimeout(), config.getConnectionRequestTimeout(), config.getSocketTimeout());
		GamstopAdapter adapter = GamstopAdapter.builder()
				.restTemplate(restTemplate)
				.build();

		List<ExclusionResultLine> resultLines = new ArrayList<>();
		List<ExclusionRequest> requests = new ArrayList<>();
		for (ExclusionRequestLine line : request.getRequestData()) {
			requests.add(line.getExclusionRequest());
		}
		List<ExclusionResult> results = adapter.checkBatchExclusion(config.getBatchPlatformUrl(), config.getApiKey(), requests);
		for (int i = 0; i < results.size(); i++) {
			ExclusionResult result = results.get(i);
			ExclusionRequestLine requestLine = request.getRequestData().get(i);
			log.debug("{}", result);
			ExclusionResult exclusionResult = result;
			resultLines.add(ExclusionResultLine.builder()
					.exclusionResult(exclusionResult)
					.reportRunResultsId(requestLine.getReportRunResultsId())
					.userGuid(result.getCorrelationId())
					.build());
			User user = getUser(result.getCorrelationId());
			try {
				updateExclusion(exclusionResult, user);
			} catch (Throwable ex) {
				// Assuming we don't want to break entire batch process
				// This breaks because someone is using 500 errors as "normal execution error" in limit service
				log.warn("Failed to update exclusion [user="+user+", exclusionResult="+exclusionResult+"] " + ex.getMessage());
			}
		}
		BatchExclusionCheckResponse batchResponse = BatchExclusionCheckResponse.builder()
				.responseData(resultLines)
				.reportId(request.getReportId())
				.reportRunId(request.getReportRunId())
				.build();
		updateReportRunResults(batchResponse);
		return batchResponse;

	}

	/**
	 * Update exclusion
	 * @param user
	 */
	private void updateExclusion(ExclusionResult exclusionResult, User user)
			throws Status551ServiceAccessClientException, UserNotFoundException,
			Status500InternalServerErrorException, UserClientServiceFactoryException, LithiumServiceClientFactoryException, Status489PlayerExclusionNotFoundException, Status551ServiceChangeLogClientException {
		ExclusionClient exclusionClient = apiClientService.getExclusionService();
		PlayerExclusionV2 playerExclusion = exclusionClient.lookup(user.guid());
		String defaultLocale = cachingDomainClientService.domainLocale(user.getDomain().getName());

		if ("Y".equalsIgnoreCase(exclusionResult.getExclusion())) {
			processBlockedResponse(playerExclusion, exclusionClient, user);
		} else if ("N".equalsIgnoreCase(exclusionResult.getExclusion()) || "P".equalsIgnoreCase(exclusionResult.getExclusion())) {
			// If the block was GAMSTOP unblock the player, https://playsafe.atlassian.net/browse/LIVESCORE-777?focusedCommentId=52754.
			if (
					(playerExclusion != null && ADVISOR.equalsIgnoreCase(playerExclusion.getAdvisor())) ||
					(user != null && user.getStatusReason() != null && user.getStatusReason().getName().contentEquals(StatusReason.GAMSTOP_SELF_EXCLUSION.statusReasonName()))
			) {
				log.debug("Removing Gamstop Player Exclusion");
				RemoveExclusionRequest removeExclusionRequest = RemoveExclusionRequest.builder()
						.authorGuid(ADVISOR)
						.playerGuid(user.getGuid())
						.build();
				RemoveExclusionResponse removeExclusionResponse = exclusionClient.remove(removeExclusionRequest);
				log.info("Removed Exclusion, {}", removeExclusionResponse);
			} else {
				String comment = "";
				try {
					if (user.getLastLogin() == null){
						comment += " " + messageSource.getMessage("UI_NETWORK_ADMIN.ACCESS_PROVIDER_GAMSTOP.NO_SE_GAMSTOP", null, Locale.forLanguageTag(defaultLocale));
						List<ChangeLogFieldChange> clfc = changeLogService.copy(user, new User(),
								new String[] {"guid", "createdDate", "status"});
						changeLogService.registerChangesForNotesWithFullNameAndDomain("user.exclusion", "create", user.getId(), User.SYSTEM_GUID, null, comment+" | X-UNIQUE-ID: "+exclusionResult.getMsRequestId(),
								exclusionResult.getMsRequestId(), clfc, Category.ACCOUNT, SubCategory.STATUS_CHANGE, 10, user.getDomain().getName());
					}

				} catch (Exception ex) {
					log.error("Problem writing changelog note: 'Player is not Self-Excluded on Gamstop' for username: "+ user.getUsername(), ex);
				}
			}
		}
	}

	private void processBlockedResponse(PlayerExclusionV2 playerExclusion, ExclusionClient exclusionClient, User user) throws Status500InternalServerErrorException, LithiumServiceClientFactoryException, UserNotFoundException, UserClientServiceFactoryException, Status551ServiceChangeLogClientException {
		lithium.service.limit.client.schemas.exclusion.ExclusionRequest exclusionRequest =
				lithium.service.limit.client.schemas.exclusion.ExclusionRequest.builder()
						.playerGuid(user.guid())
						.advisor(ADVISOR)
						.exclusionSource(ExclusionSource.EXTERNAL)
						//We don't put an expiry date here so the Gamstop never expires
						//.exclusionEndDate(DateTime.now().plusDays(1).toDate()) // Exclude for 1 day from now
						.build();
		if (playerExclusion == null) {
			try {
				exclusionClient.set(exclusionRequest);
			} catch (Exception ex) {
				log.debug("Problem processing exclusion request: " + exclusionRequest, ex);
			}
		} else {
			ChangeLog changeLog = ChangeLog.builder()
					.entityRecordId(user.getId())
					.authorGuid(User.SYSTEM_GUID)
					.type("comment")
					.entity("user")
					.categoryName("Responsible Gaming")
					.changeDate(new Date())
					.priority(1)
					.domainName(user.getDomain().getName())
					.build();
			if(!ADVISOR.equalsIgnoreCase(playerExclusion.getAdvisor())){
				// if a player is already blocked add note https://playsafe.atlassian.net/browse/LIVESCORE-777?focusedCommentId=60870
				changeLog.setComments("Also restricted on gamstop");
			} else {
				changeLog.setComments("Gamstop check performed, player still under exclusion on Gamstop");
			}
			writeChangeLog(changeLog);
		}
	}

	private void writeChangeLog(ChangeLog changeLog) throws Status551ServiceChangeLogClientException {
		ChangeLogClient changeLogClient = apiClientService.getChangeLogClient();
		if (changeLogClient != null) {
			changeLogClient.registerChangesWithDomain(changeLog);
		}
	}

	public boolean alreadyBlocked(String userGuid) throws Status551ServiceAccessClientException {
		ExclusionClient exclusionClient = apiClientService.getExclusionService();
		PlayerExclusionV2 playerExclusionV2 = exclusionClient.lookup(userGuid);
		log.debug("Player exclusion {}", playerExclusionV2);
		return playerExclusionV2 != null && !ADVISOR.equalsIgnoreCase(playerExclusionV2.getAdvisor());
	}

	private void updateReportRunResults(BatchExclusionCheckResponse batchResponse) throws Status551ServiceReportClientException {
		PlayersReportClient reportClient = apiClientService.playersReportClient();
		if (reportClient != null){
			reportClient.updateReportRunResult(batchResponse.getReportId(), batchResponse.getReportRunId(), batchResponse);
		}
	}

}
