package lithium.service.casino.provider.betsoft.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.casino.client.objects.EMockResponseStatus;
import lithium.service.casino.client.objects.MockResponse;
import lithium.service.casino.provider.betsoft.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.betsoft.controllers.BonusWinController;
import lithium.service.casino.provider.betsoft.data.request.*;
import lithium.service.casino.provider.betsoft.service.BetsoftService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class MockService {

	@Value("${spring.application.name}")
	private String applicationName; //service-casino-provider-betsoft (this is the provider guid)

	@Autowired
	BetsoftService betsoftService;

	@Autowired
	LithiumConfigurationProperties lithiumConfigProps;

	@Autowired
	RestTemplate restTemplate;
	@Autowired
	ObjectMapper mapper;

	@Autowired
	TokenStore tokenStore;

	private BrandsConfigurationBrand getProviderConfig(final String domainName) {
		BrandsConfigurationBrand brandConfiguration = betsoftService.getBrandConfiguration(applicationName, domainName);
		return brandConfiguration;
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

		BetRequest betRequest = new BetRequest(
				userGuid,
				amountCents+"|"+transactionId,
				null,
				roundId,
				Integer.parseInt(providerGameId),
				roundEnd,
				transactionId+"_"+roundId,
				null);

		return process(userGuid, betRequest, "bet");
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

		BetRequest betRequest = new BetRequest(
				userGuid,
				null,
				amountCents+"|"+transactionId,
				roundId,
				Integer.parseInt(providerGameId),
				roundEnd,
				transactionId+"_"+roundId,
				null);

		return process(userGuid, betRequest, "bet");
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

		BetRequest betRequest = new BetRequest(
				userGuid,
				null,
				"0"+"|"+transactionId,
				roundId,
				Integer.parseInt(providerGameId),
				roundEnd,
				transactionId+"_"+roundId,
				Integer.parseInt(""+amountCents));

		return process(userGuid, betRequest, "bet");
	}

	public MockResponse processRefund(
			String userGuid,
			String transactionId) {

		RefundRequest refundRequest = new RefundRequest(userGuid, Long.parseLong(transactionId));

		return process(userGuid, refundRequest, "refund");
	}

	public MockResponse processBalance(
			String userGuid) {

		BalanceRequest balanceRequest = new BalanceRequest(userGuid);

		return process(userGuid, balanceRequest, "balance");
	}

	public MockResponse processAuthenticateUser(
			String userGuid,
			String authToken) {


		AuthenticationRequest authRequest = new AuthenticationRequest(getApiTokenFromAuthToken(authToken)+"|"+userGuid);

		return process(userGuid, authRequest, "auth");
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

		BetRequest betRequest = new BetRequest(
				userGuid,
				null,
				amountCentsWin+"|"+transactionId,
				roundId,
				Integer.parseInt(providerGameId),
				roundEnd,
				transactionId+"_"+roundId,
				Integer.parseInt(""+amountCentsNegativeBet));

		return process(userGuid, betRequest, "bet");
	}

	public MockResponse processAuthenticateUSer(
			String userGuid) {

		AuthenticationRequest authenticationRequest = new AuthenticationRequest(userGuid);

		return process(userGuid, authenticationRequest, "auth");
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

		//TODO: Maybe pass in a pre-created bonus id, it seems a bit pointless at this stage though
		Random rand = new Random();
		BonusWinRequest bonusWinRequest = new BonusWinRequest(userGuid, rand.nextInt(), amountCents, transactionId);

		return process(userGuid, bonusWinRequest, "bonuswin");
	}

	/**
	 * Performs provider config lookup, hash generation and remote service call invocation for actions.
	 * @param userGuid
	 * @param request
	 * @param requestType
	 * @param <E>
	 * @return MockResponse
	 */
	private <E extends Request> MockResponse process(
			String userGuid,
			E request,
			String requestType) {

		final String domainName = userGuid.split("/")[0];
		final BrandsConfigurationBrand brandConfig = getProviderConfig(domainName);

		String hash = request.calculateHash(brandConfig.getHashPassword());
		request.setHash(hash);

		String requestUrl = lithiumConfigProps.getGatewayPublicUrl() + //gateway
				"/" + applicationName + //eureka service name
				"/" + applicationName + //provider guid
				"/" + brandConfig.getApiKey() + //API key that does nothing for now
				"/" + domainName + //domain name
				"/" + requestType; //action to perform
		log.info("Request url for mock bet payload: " + requestUrl);

		final String requestToLithium = requestUrl+"?"+request.toHttpParameterMapString();
		Instant startExecution = Instant.now();

		String responseFromLithium = restTemplate.getForObject(requestToLithium, String.class);

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
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(responseString)));
			NodeList nodeList = document.getElementsByTagName("RESULT");
			if (nodeList != null && nodeList.getLength() > 0) {
				if (nodeList.item(0).getFirstChild().getNodeValue().equalsIgnoreCase("OK")) {
					nodeList = document.getElementsByTagName("EXTSYSTEMTRANSACTIONID");
					return EMockResponseStatus.SUCCESS;
				} else {
					return EMockResponseStatus.FAIL;
				}
			}
		} catch (Exception e) {
			log.error("Unable to parse XML string to status." + responseString, e);
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
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new InputSource(new StringReader(responseString)));
			NodeList nodeList = document.getElementsByTagName("EXTSYSTEMTRANSACTIONID");
			if (nodeList != null && nodeList.getLength() > 0) {
				return Long.parseLong(nodeList.item(0).getFirstChild().getNodeValue());
			}
		} catch (Exception e) {
			log.error("Unable to parse XML string for transaction id" + responseString, e);
			return null;
		}
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
}
