package lithium.service.casino.provider.rival.mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.casino.client.objects.EMockResponseStatus;
import lithium.service.casino.client.objects.MockResponse;
import lithium.service.casino.provider.rival.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.rival.data.request.ValidateRequest;
import lithium.service.casino.provider.rival.service.RivalService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class MockService {

	@Value("${spring.application.name}")
	private String applicationName; //service-casino-provider-betsoft (this is the provider guid)

	@Autowired
	RivalService rivalService;

	@Autowired
	LithiumConfigurationProperties lithiumConfigProps;

	@Autowired
	RestTemplate restTemplate;
	@Autowired
	ObjectMapper mapper;

	@Autowired
	TokenStore tokenStore;

	private BrandsConfigurationBrand getProviderConfig(final String domainName) {
		BrandsConfigurationBrand brandConfiguration = rivalService.getBrandConfiguration(applicationName, domainName);
		return brandConfiguration;
	}

	/**
	 * Generic function to cater for balance adjustment type transactions
	 * @param roundId
	 * @param userGuid
	 * @param authToken
	 * @param amountBetCents
	 * @param amountWinCents
	 * @param transactionId
	 * @param providerGameId
	 * @param parentTransactionId Original unique identifier when performing bonus type transactions, null otherwise
	 * @param rootGameId Original game id when performing bonus type transaction, null otherwise
	 * @return
	 */
	private Map<String, String> genericUpdateBalance(String roundId, String userGuid, String authToken, long amountBetCents, long amountWinCents, String transactionId, String providerGameId, String parentTransactionId, String rootGameId) {
		BigDecimal bet = new BigDecimal(amountBetCents);
		bet =  bet.movePointLeft(2).negate();
		BigDecimal win = new BigDecimal(amountWinCents);
		win =  win.movePointLeft(2);

		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("function", "updatebalance"); //The endpoint identifier
		paramMap.put("id", roundId);
		paramMap.put("playerid", userGuid);
		paramMap.put("sessionid", getApiTokenFromAuthToken(authToken));
		paramMap.put("amount",win.add(bet).toString()); //Result of adding a negated bet value to win. (lithium will split this into 2 transactions if a minbalance is present.
		paramMap.put("minbalance", bet.toString());
		paramMap.put("transid", transactionId);
		paramMap.put("gameid", providerGameId);
		if (rootGameId !=null && !rootGameId.isEmpty()) {
			paramMap.put("rootgameid", rootGameId);
		}
		if (parentTransactionId != null && !parentTransactionId.isEmpty()) {
			paramMap.put("parenttransid", parentTransactionId);
		}

		return paramMap;
	}

	public MockResponse processBet(
			String userGuid,
			String transactionId,
			long amountCents,
			String authToken,
			String providerGameId,
			String currency,
			String roundId,
			boolean roundEnd) {

		return process(userGuid, genericUpdateBalance(roundId, userGuid, authToken, amountCents, 0L, transactionId, providerGameId, null, null));
	}

	public MockResponse processWin(
			String userGuid,
			String transactionId,
			long amountCents,
			String authToken,
			String providerGameId,
			String currency,
			String roundId,
			boolean roundEnd) {

		return process(userGuid, genericUpdateBalance(roundId, userGuid, authToken, 0L, amountCents, transactionId, providerGameId, null, null));
	}

	public MockResponse processNegativeBet(
			String userGuid,
			String transactionId,
			long amountCents,
			String authToken,
			String providerGameId,
			String currency,
			String roundId,
			boolean roundEnd) {

		return process(userGuid, genericUpdateBalance(roundId, userGuid, authToken, 0L, amountCents, transactionId, providerGameId, null, null));
	}

	public MockResponse processRollback(
			String userGuid,
			String authToken,
			String transactionId) {

		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("function", "rollback"); //The endpoint identifier
		paramMap.put("id", transactionId);
		paramMap.put("playerid", userGuid);
		paramMap.put("sessionid", getApiTokenFromAuthToken(authToken));

		return process(userGuid, paramMap);
	}

	public MockResponse processBalance(
			String userGuid,
			String authToken) {

		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("function", "getbalance"); //The endpoint identifier
		paramMap.put("playerid", userGuid);
		paramMap.put("sessionid", getApiTokenFromAuthToken(authToken));

		return process(userGuid, paramMap);
	}

	public MockResponse processValidateUser(
			String userGuid,
			String authToken) {

		Map<String,String> paramMap = new HashMap<>();
		paramMap.put("function", "validate"); //The endpoint identifier
		paramMap.put("playerid", userGuid);
		paramMap.put("sessionid", getApiTokenFromAuthToken(authToken));

		return process(userGuid, paramMap);
	}

	public MockResponse processFreespin(
			String userGuid,
			String transactionId,
			long amountCents,
			String authToken,
			String providerGameId,
			String currency,
			String roundId,
			boolean roundEnd) {

		return process(userGuid, genericUpdateBalance(roundId, userGuid, authToken, 0L, amountCents, transactionId+"0001", "RandomBonusGameId", transactionId, providerGameId));
	}


	public MockResponse processBetAndWin(
			String userGuid,
			String transactionId,
			long amountCentsBet,
			long amountCentsWin,
			String authToken,
			String providerGameId,
			String currency,
			String roundId,
			boolean roundEnd) {

		return process(userGuid, genericUpdateBalance(roundId, userGuid, authToken, amountCentsBet, amountCentsWin, transactionId, providerGameId, null, null));
	}

	public MockResponse processWinAndNegativeBet(
			String userGuid,
			String transactionId,
			long amountCentsWin,
			long amountCentsNegativeBet,
			String authToken,
			String providerGameId,
			String currency,
			String roundId,
			boolean roundEnd) {

		return process(userGuid, genericUpdateBalance(roundId, userGuid, authToken, 0L, amountCentsWin+amountCentsNegativeBet, transactionId, providerGameId, null, null));
	}

	/**
	 * Performs provider config lookup, hash generation and remote service call invocation for actions.
	 * @param userGuid
	 * @param paramMap
	 * @return
	 */
	private MockResponse process(
			String userGuid,
			Map<String, String> paramMap) {

		final String domainName = userGuid.split("/")[0];
		final BrandsConfigurationBrand brandConfig = getProviderConfig(domainName);

		paramMap.put("hmac", rivalService.generateHash(brandConfig.getHashPassword(), paramMap));

		String requestUrl = lithiumConfigProps.getGatewayPublicUrl() + //gateway
				"/" + applicationName + //eureka service name
				"/" + applicationName + //provider guid
				"/" + 12345 +//brandConfig.getApiKey() + //API key that does nothing for now
				"/" + domainName; //domain name
		log.info("Request url for mock bet payload: " + requestUrl);

		final String requestToLithium = requestUrl+"?jsoncall="+paramMap.getOrDefault("function", "unknown");

		Instant startExecution = Instant.now();

		String responseFromLithium = restTemplate.postForObject(requestToLithium, paramMap, String.class);

		Instant endExecution = Instant.now();
		Interval execTime = new Interval(startExecution, endExecution);

		log.info("Response from bet request submission: " + responseFromLithium);

		MockResponse mockResponse = new MockResponse();
		mockResponse.setEmulatedRequestToLithium(requestToLithium);
		mockResponse.setResponseFromLithium(responseFromLithium);
		mockResponse.setExecutionTimeMs(execTime.toDurationMillis());
		mockResponse.setMockResponseStatus(parseResponseFromLithiumForStatus(responseFromLithium));
		if (mockResponse.getMockResponseStatus() == EMockResponseStatus.SUCCESS) {
			mockResponse.setLithiumTransactionId(parseResponseFromLithiumForTransactionId(responseFromLithium));
		}

		return mockResponse;
	}
	/**
	 * Helper method to produce a {@link EMockResponseStatus} from the response returned from Lithium.
	 * @param responseString
	 * @return {@link EMockResponseStatus}
	 */
	private EMockResponseStatus parseResponseFromLithiumForStatus(final String responseString) {
		try {
			ObjectMapper om = new ObjectMapper();
			JsonNode node = om.readTree(responseString);
			if (node.findValue("error") != null || node.findValue("user_error") != null) {
				return EMockResponseStatus.FAIL;
			}

			if (node.findValue("balance") != null) {
				return EMockResponseStatus.SUCCESS;
			}
		} catch (Exception e) {
			log.error("Unable to parse JSON string to status." + responseString, e);
			return EMockResponseStatus.ERROR;
		}
		return EMockResponseStatus.UNIMPLEMENTED;
	}

	/**
	 * Helper method to get the lithium transaction id from the lithium result data.
	 * @param responseString
	 * @return
	 */
	private Long parseResponseFromLithiumForTransactionId(final String responseString) {
		//Rival responses don't contain the transaction id from lithium
		return null;
	}

	private String getApiTokenFromAuthToken(String authToken) {
		OAuth2AccessToken at = tokenStore.readAccessToken(authToken);
		Object userData = at.getAdditionalInformation().getOrDefault("jwtUser", "");
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
		String apiToken = util.getJwtUser().getApiToken();
		//apiToken += "|"+util.getJwtUser().getDomainName() +"/"+util.getJwtUser().getUsername();
		return apiToken;
	}

	private String toHttpParameterMapString(Map<String, String> parameterMap) {
		StringBuffer sb = new StringBuffer();

		parameterMap.forEach( (key,value) -> {
			if (key.equalsIgnoreCase("function")) { //Not sure why this is in the call, no record of it in rival docs.
				sb.append("jsoncall");
				sb.append("=");
				sb.append(value);
				sb.append("&");
			}
			sb.append(key);
			sb.append("=");
			sb.append(value);
			sb.append("&");
		});

		return sb.substring(0, sb.length()-1);
	}
}
