package lithium.service.casino.provider.supera.mock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.casino.client.objects.EMockResponseStatus;
import lithium.service.casino.client.objects.MockResponse;
import lithium.service.casino.provider.supera.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.supera.service.SuperaService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class MockService {

	@Value("${spring.application.name}")
	private String applicationName; //service-casino-provider-betsoft (this is the provider guid)

	@Autowired
	SuperaService superaService;

	@Autowired
	LithiumConfigurationProperties lithiumConfigProps;

	@Autowired
	RestTemplate restTemplate;
	@Autowired
	ObjectMapper mapper;

	@Autowired
	TokenStore tokenStore;

	private BrandsConfigurationBrand getProviderConfig(final String domainName) {
		BrandsConfigurationBrand brandConfiguration = superaService.getBrandConfiguration(applicationName, domainName);
		return brandConfiguration;
	}

	private Map<String, String> prepForAdjustment(
			String action,
			String actionType,
			String userGuid,
			String transactionId,
			long amountCents,
			String authToken,
			String providerGameId,
			String roundId) {
		//      action=credit&action_type=win&amount=0.50&remote_id=norgeslotteriet/lott102&transaction_id=51101555422619151&game_id=110&round_id=110_1555422619149&session_id=45bb36bd-f48d-41ac-bdbe-788af1b137e4&key=3d961276cb8aa984c694aa14c8914294b2fbedbe
		//      action=credit&action_type=win&amount=0.50&remote_id=norgeslotteriet/lott102&transaction_id=51101555422619151&game_id=110&round_id=110_1555422619149&session_id=45bb36bd-f48d-41ac-bdbe-788af1b137e4
		//		action=credit&action_type=win&amount=0.50&remote_id=norgeslotteriet/lott102&transaction_id=51101555421966533&game_id=110&round_id=110_1555421966526&session_id=45bb36bd-f48d-41ac-bdbe-788af1b137e4
		Map<String, String> parameterMap = new LinkedHashMap<>();
		parameterMap.put("action", action);
		parameterMap.put("action_type", actionType);
		parameterMap.put("amount", (new BigDecimal(amountCents)).movePointLeft(2).toPlainString());
		parameterMap.put("remote_id", userGuid);
		parameterMap.put("transaction_id", transactionId);
		if (providerGameId != null)
			parameterMap.put("game_id", providerGameId);
		if (roundId != null)
			parameterMap.put("round_id", roundId);
		if (authToken != null)
			parameterMap.put("session_id", getApiTokenFromAuthToken(authToken));

		return parameterMap;
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

		return process(userGuid, prepForAdjustment("debit", "bet", userGuid, transactionId, amountCents, authToken, providerGameId, roundId));
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

		return process(userGuid, prepForAdjustment("credit", "win", userGuid, transactionId, amountCents, authToken, providerGameId, roundId));
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

		return process(userGuid, prepForAdjustment("credit", "win", userGuid, transactionId, amountCents, authToken, providerGameId, roundId));
	}

	public MockResponse processRefund(
			String userGuid,
			String transactionId,
			long amountCents) {

		return process(userGuid, prepForAdjustment("credit", "ref", userGuid, transactionId, amountCents, null,null, null));
	}

	public MockResponse processBalance(
			String userGuid) {

		Map<String, String> parameterMap = new LinkedHashMap<>();
		parameterMap.put("action", "balance");
		parameterMap.put("remote_id", userGuid);

		return process(userGuid, parameterMap);
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

		return process(userGuid, prepForAdjustment("credit", "win_free", userGuid, transactionId, amountCents, authToken, providerGameId, roundId));
	}

	/**
	 * Performs provider config lookup, hash generation and remote service call invocation for actions.
	 * @param userGuid
	 * @param parameterMap
	 * @return
	 */
	private MockResponse process(
			String userGuid,
			Map<String, String> parameterMap) {

		final String domainName = userGuid.split("/")[0];
		final BrandsConfigurationBrand brandConfig = getProviderConfig(domainName);

		String parameterString = toHttpParameterMapString(parameterMap);
		String hash = superaService.buildHashKey(parameterString, brandConfig.getSaltKey());
		parameterString += "&key=" + hash;

		String requestUrl = lithiumConfigProps.getGatewayPublicUrl() + //gateway
				"/" + applicationName + //eureka service name
				"/" + applicationName + //provider guid
				"/" + brandConfig.getApiKey() + //API key that does nothing for now
				"/" + domainName; //domain name

		log.info("Request url for mock bet payload: " + requestUrl);

		final String requestToLithium = requestUrl+"/?"+parameterString;
		Instant startExecution = Instant.now();

		String responseFromLithium = restTemplate.getForObject(requestToLithium, String.class);

		Instant endExecution = Instant.now();
		Interval execTime = new Interval(startExecution, endExecution);

		log.info("Response from request submission: " + responseFromLithium);

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
			if (!node.findValue("msg").isNull() || node.findValue("status").asLong() != 200L) {
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
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder builder;
//		try {
//			builder = factory.newDocumentBuilder();
//			Document document = builder.parse(new InputSource(new StringReader(responseString)));
//			NodeList nodeList = document.getElementsByTagName("EXTSYSTEMTRANSACTIONID");
//			if (nodeList != null && nodeList.getLength() > 0) {
//				return Long.parseLong(nodeList.item(0).getFirstChild().getNodeValue());
//			}
//		} catch (Exception e) {
//			log.error("Unable to parse XML string for transaction id" + responseString, e);
//			return null;
//		}
		return null;
	}

	private String getApiTokenFromAuthToken(String authToken) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
		String apiToken = util.getJwtUser().getApiToken();
		//apiToken += "|"+util.getJwtUser().getDomainName() +"/"+util.getJwtUser().getUsername();
		return apiToken;
	}

	private String getuserGuidFromAuthToken(String authToken) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
		String userGuid = util.getJwtUser().getDomainName() +"/"+util.getJwtUser().getUsername();
		return userGuid;
	}

	private String toHttpParameterMapString(Map<String, String> parameterMap) {
		StringBuffer sb = new StringBuffer();

		parameterMap.forEach( (key,value) -> {
			sb.append(key);
			sb.append("=");
			sb.append(value);
			sb.append("&");
		});

		return sb.substring(0, sb.length()-1);
	}
}
