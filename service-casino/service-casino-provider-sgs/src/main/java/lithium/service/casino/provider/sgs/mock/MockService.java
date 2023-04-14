package lithium.service.casino.provider.sgs.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.casino.client.objects.EMockResponseStatus;
import lithium.service.casino.client.objects.MockResponse;
import lithium.service.casino.provider.sgs.config.BrandsConfigurationBrand;
import lithium.service.casino.provider.sgs.data.MessageTypes;
import lithium.service.casino.provider.sgs.data.request.*;
import lithium.service.casino.provider.sgs.service.SGSService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

@Slf4j
@Service
public class MockService {

	@Value("${spring.application.name}")
	private String applicationName; //service-casino-provider-betsoft (this is the provider guid)

	@Autowired
	SGSService sgsService;

	@Autowired
	LithiumConfigurationProperties lithiumConfigProps;

	@Autowired
	RestTemplate restTemplate;
	@Autowired
	ObjectMapper mapper;

	@Autowired
	TokenStore tokenStore;

	private BrandsConfigurationBrand getProviderConfig(final String domainName) {
		BrandsConfigurationBrand brandConfiguration = sgsService.getBrandConfiguration(applicationName, domainName);
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
			boolean roundEnd,
			boolean roundStart) {

		Request request = basicRequestBuilder(userGuid, authToken, MessageTypes.PLAY);
		request.getMethodCall().getCall().setPlayType("bet");
		request.getMethodCall().getCall().setGameId(roundId);
		request.getMethodCall().getCall().setActionId(transactionId);
		request.getMethodCall().getCall().setGameReference(providerGameId);
		request.getMethodCall().getCall().setAmount(amountCents);
		request.getMethodCall().getCall().setCurrency(currency);
		request.getMethodCall().getCall().setStart(roundStart);

		return process(userGuid, request);
	}

	public MockResponse processWin(
			String userGuid,
			String transactionId,
			long amountCents,
			String authToken,
			String providerGameId,
			String currency,
			String roundId,
			boolean roundEnd,
			boolean roundStart) {

		Request request = basicRequestBuilder(userGuid, authToken, MessageTypes.PLAY);
		request.getMethodCall().getCall().setPlayType("win");
		request.getMethodCall().getCall().setGameId(roundId);
		request.getMethodCall().getCall().setActionId(transactionId);
		request.getMethodCall().getCall().setGameReference(providerGameId);
		request.getMethodCall().getCall().setAmount(amountCents);
		request.getMethodCall().getCall().setCurrency(currency);
		request.getMethodCall().getCall().setStart(roundStart);

		return process(userGuid, request);
	}

	public MockResponse processNegativeBet(
			String userGuid,
			String transactionId,
			long amountCents,
			String authToken,
			String providerGameId,
			String currency,
			String roundId,
			boolean roundEnd,
			boolean roundStart) {

		return processWin(userGuid, transactionId, amountCents, authToken, providerGameId, currency, roundId, roundEnd, roundStart);
	}

	public MockResponse processRefund(
			String userGuid,
			String transactionId,
			long amountCents,
			String authToken,
			String providerGameId,
			String currency,
			String roundId,
			boolean roundEnd,
			boolean roundStart) {

		Request request = basicRequestBuilder(userGuid, authToken, MessageTypes.PLAY);
		request.getMethodCall().getCall().setPlayType("refund");
		request.getMethodCall().getCall().setGameId(roundId);
		request.getMethodCall().getCall().setActionId(transactionId);
		request.getMethodCall().getCall().setGameReference(providerGameId);
		request.getMethodCall().getCall().setAmount(amountCents);
		request.getMethodCall().getCall().setCurrency(currency);
		request.getMethodCall().getCall().setStart(roundStart);

		return process(userGuid, request);
	}

	public MockResponse processBalance(
			String userGuid,
			String authToken) {
		Request request = basicRequestBuilder(userGuid, authToken, MessageTypes.GET_BALANCE);

		return process(userGuid, request);
	}

	public MockResponse processAuthenticateUser(
			String userGuid,
			String authToken) {
		Request request = basicRequestBuilder(userGuid, authToken, MessageTypes.LOGIN);

		return process(userGuid, request);
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

		return processWin(userGuid, transactionId, amountCents, authToken, providerGameId, currency, roundId, roundEnd, roundEnd);
	}

	public MockResponse processEndgame(
			String userGuid,
			String transactionId,
			long amountCents,
			String authToken,
			String providerGameId,
			String currency,
			String roundId) {

		Request request = basicRequestBuilder(userGuid, authToken, MessageTypes.PLAY);
		request.getMethodCall().getCall().setPlayType("refund");
		request.getMethodCall().getCall().setGameReference(providerGameId);
		request.getMethodCall().getCall().setGameId(roundId);
		request.getMethodCall().getCall().setAmount(amountCents);
		return process(userGuid, request);
	}

	/**
	 * Used to build a basic request. Just populate additional things in for individual calls
	 * @return
	 */
	private Request basicRequestBuilder(final String userGuid, final String authToken, final MessageTypes messageType) {
		final String domainName = userGuid.split("/")[0];
		final BrandsConfigurationBrand brandConfig = getProviderConfig(domainName);
		Auth auth = new Auth(brandConfig.getApiLogin(), brandConfig.getApiPassword());
		Call call = new Call(UUID.randomUUID().toString(), getApiTokenFromAuthToken(authToken), null, null, null, null, null, null, null
				,null, null);
		Extinfo extInfo = new Extinfo(null, null, null);
		MethodCall methodCall = new MethodCall(messageType.getTypeName(), DateTime.now().toDate(), "casino", auth, call, extInfo);
		Request request = new Request(methodCall);
		return request;
	}

	/**
	 * Performs provider config lookup, hash generation and remote service call invocation for actions.
	 * @param userGuid
	 * @param request
	 * @return MockResponse
	 */
	private <E extends Request> MockResponse process(
			String userGuid,
			E request) {

		final String domainName = userGuid.split("/")[0];
		final BrandsConfigurationBrand brandConfig = getProviderConfig(domainName);

//		String hash = request.calculateHash(brandConfig.getHashPassword());
//		request.setHash(hash);

		String requestUrl = lithiumConfigProps.getGatewayPublicUrl() + //gateway
				"/" + applicationName + //eureka service name
				"/" + applicationName + //provider guid
				"/" + brandConfig.getApiKey() + //API key that does nothing for now
				"/" + domainName + //domain name
				"/endpoint"; //action to perform
		log.info("Request url for mock bet payload: " + requestUrl);

		final String requestToLithium = requestUrl;//+"?"+request.toHttpParameterMapString();

		JAXBContext jaxbContext;
		StringWriter sw = new StringWriter();
		try {
			jaxbContext = JAXBContext.newInstance(Request.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			jaxbMarshaller.marshal(request, sw);

		} catch (JAXBException e) {
			log.error("jaxb context creation error", e);
		}
		Instant startExecution = Instant.now();
		String responseFromLithium = restTemplate.postForObject(requestToLithium, request, String.class);

		Instant endExecution = Instant.now();
		Interval execTime = new Interval(startExecution, endExecution);

		log.info("Response from bet request submission: " + responseFromLithium);

		MockResponse mockResponse = new MockResponse();
		mockResponse.setEmulatedRequestToLithium(requestToLithium+ " | body: " + sw.toString());
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
			NodeList nodeList = document.getElementsByTagName("result");
			if (nodeList != null && nodeList.getLength() > 0) {
				if (nodeList.item(0).getAttributes().getNamedItem("errorcode") != null){
					return EMockResponseStatus.FAIL;
				} else {
					return EMockResponseStatus.SUCCESS;
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
			NodeList nodeList = document.getElementsByTagName("result");
			if (nodeList != null && nodeList.getLength() > 0) {
				if (nodeList.item(0).getAttributes().getNamedItem("exttransactionid") != null) {
					return Long.parseLong(nodeList.item(0).getAttributes().getNamedItem("exttransactionid").getNodeValue());
				}
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
