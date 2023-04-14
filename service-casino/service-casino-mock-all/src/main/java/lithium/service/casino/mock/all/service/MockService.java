package lithium.service.casino.mock.all.service;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.accounting.client.stream.transactionlabel.TransactionLabelStream;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.accounting.objects.TransactionLabelContainer;
import lithium.service.casino.client.CasinoProviderMock;
import lithium.service.casino.client.objects.MockResponse;
import lithium.service.casino.client.objects.ProviderMockPayload;
import lithium.service.casino.mock.all.entities.MockActivity;
import lithium.service.casino.mock.all.entities.MockActivityExecution;
import lithium.service.casino.mock.all.entities.MockSession;
import lithium.service.casino.mock.all.repositories.MockActivityExecutionRepository;
import lithium.service.casino.mock.all.repositories.MockActivityRepository;
import lithium.service.casino.mock.all.repositories.MockSessionRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
@Service
public class MockService {

	@Autowired
	protected ModelMapper mapper;
	@Autowired
	protected LithiumServiceClientFactory clientFactory;
	@Autowired
	protected TokenStore tokenStore;
	@Autowired
	protected MockSessionRepository mockSessionRepository;
	@Autowired
	private LithiumConfigurationProperties config;
	@Autowired
	private MockActivityRepository mockActivityRepository;
	@Autowired
	private MockActivityExecutionRepository mockActivityExecutionRepository;
	@Autowired
	private TransactionLabelStream transactionLabelStream;

	public Optional<CasinoProviderMock> findCasinoProviderMock(String providerGuid) {

		CasinoProviderMock cl = null;
		try {
			cl = clientFactory.target(CasinoProviderMock.class, providerGuid, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error("Problem getting CasinoProviderMock service", e);
		}

		return Optional.ofNullable(cl);
	}

	public String generateMockGameStartUrl(final MockSession mockSession) {
		return config.getGatewayPublicUrl() + "/service-casino-mock-all/launchGame?mockSessionId=" + mockSession.getId().toString();
	}

	/**
	 * Find or create a mock session using a provided auth token, game url and game provider
	 * @param authToken
	 * @param gameStartUrl
	 * @param providerGuid
	 * @return
	 */

	public MockSession findOrRegisterSession(final String authToken, final String gameStartUrl, final String providerGuid, final String providerGameId, final String currency) {
		MockSession mockSession = mockSessionRepository.findByAuthTokenAndGameStartUrl(authToken, gameStartUrl);

		if (mockSession == null) {
			mockSession = MockSession.builder()
					.authToken(authToken)
					.gameStartUrl(gameStartUrl)
					.userGuid(getUserGuidFromAuthToken(authToken))
					.providerGuid(providerGuid)
					.providerGameId(providerGameId)
					.currency(currency)
					.build();
			mockSession = mockSessionRepository.save(mockSession);
		}

		return mockSession;
	}

	public Optional<MockSession> findMockSessionById(final long mockSessionId) {
		return mockSessionRepository.findById(mockSessionId);
	}

	public MockActivity registerMockActivity(final MockSession mockSession) {
		final String currentMs = System.currentTimeMillis()+"";
		return mockActivityRepository.save(MockActivity.builder()
				.mockSession(mockSession)
				//.transactionId(""+mockSession.getId()+mockSession.getProviderGameId()+currentMs)
				.roundId(mockSession.getProviderGameId()+"_"+currentMs)
				.mockActivityExecutionList(new ArrayList<>())
				.build());
	}


	/**
	 * Helper to create treansaction id or use the one provided in the method parameters
	 * @param mockSession
	 * @param transactionId
	 * @return
	 */
	public String produceTransactionIdIfNotProvided(final MockSession mockSession, final String transactionId) {
		final String currentMs = System.currentTimeMillis()+"";
		String usableTransactionId = transactionId;
		if (transactionId == null || transactionId.trim().isEmpty()) {
			usableTransactionId = ""+mockSession.getId()+mockSession.getProviderGameId()+currentMs;
		}
		return usableTransactionId;
	}

	//FIXME: The below bet structure can be copied for other executions
	/**
	 * Provide a mechanism to execute a bet request and
	 * populate the mock activity object with relevant response information.
	 *
	 * Providing no transaction id will create a unique id using the established format.
	 *
	 * @param casinoProviderMock
	 * @param mockSession
	 * @param mockActivity
	 * @param transactionId
	 * @param amountCents
	 * @param roundEnd
	 */
	public void executeBet(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCents,
			final boolean roundEnd) {

		MockResponse mockResponse = casinoProviderMock.bet(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCents)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockActivity
				.getMockActivityExecutionList()
				.add(createMockActivityExecutionFromMockResponse(mockResponse, mockActivity, transactionId));
	}

	/**
	 * Utility method for identifying unique transaction numbers and adding mock labels to them in the accounting system.
	 * @param mockActivity
	 */
	public void addMockLabelsToTransactions(final MockActivity mockActivity) {

		ArrayList<TransactionLabelBasic> labelList = new ArrayList<>();
		labelList.add(TransactionLabelBasic.builder().labelName("mock").labelValue("true").summarize(false).build());

		HashSet<Long> uniqueTrans = new HashSet<>();
		mockActivity.getMockActivityExecutionList().forEach(execution -> {
			uniqueTrans.add(execution.getLithiumTransactionId());
		});

		// Send a label request to accounting to mark the transaction as a mock transaction.
		try {
			uniqueTrans.forEach(tranId -> {
				if (tranId != null) {
					TransactionLabelContainer entry = TransactionLabelContainer.builder().transactionId(tranId).labelList(labelList).build();
					log.debug("Register TransactionLabelContainer: " + entry);
					transactionLabelStream.register(entry);
				}
			});
		} catch (Exception e) {
			log.error("Unable to add labels to transactions: " + mockActivity, e);
		}
	}

	public void executeWin(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCents,
			final boolean roundEnd) {

		MockResponse mockResponse = casinoProviderMock.win(
				ProviderMockPayload.builder()
				.userGuid(mockSession.getUserGuid())
				.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
				.amountCentsPrimaryAction(amountCents)
				.authToken(mockSession.getAuthToken())
				.providerGameId(mockSession.getProviderGameId())
				.currency(mockSession.getCurrency())
				.roundId(mockActivity.getRoundId())
				.roundEnd(roundEnd)
				.build());

		mockActivity
				.getMockActivityExecutionList()
				.add(createMockActivityExecutionFromMockResponse(mockResponse, mockActivity, transactionId));
	}

	public void executeNegativeBet(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCents,
			final boolean roundEnd) {

		MockResponse mockResponse = casinoProviderMock.negativeBet(

				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCents)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockActivity
				.getMockActivityExecutionList()
				.add(createMockActivityExecutionFromMockResponse(mockResponse, mockActivity, transactionId));
	}

	public void executeRefund(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCents,
			final boolean roundEnd) {

		MockResponse mockResponse = casinoProviderMock.refund(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCents)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockActivity
				.getMockActivityExecutionList()
				.add(createMockActivityExecutionFromMockResponse(mockResponse, mockActivity, transactionId));
	}

	public void executeFreespin(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCents,
			final boolean roundEnd) {

		MockResponse mockResponse = casinoProviderMock.freespin(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCents)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockActivity
				.getMockActivityExecutionList()
				.add(createMockActivityExecutionFromMockResponse(mockResponse, mockActivity, transactionId));
	}

	public void executeFreespinFeature(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCents,
			final boolean roundEnd) {

		MockResponse mockResponse = casinoProviderMock.freespinFeature(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCents)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockActivity
				.getMockActivityExecutionList()
				.add(createMockActivityExecutionFromMockResponse(mockResponse, mockActivity, transactionId));
	}

	public void executeBetAndWin(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCentsPrimary,
			final long amountCentsSecondary,
			final boolean roundEnd) {

		ArrayList<MockResponse> mockResponseList = casinoProviderMock.betAndWin(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCentsPrimary)
						.amountCentsSecondaryAction(amountCentsSecondary)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockResponseList.forEach(response -> {
			mockActivity
					.getMockActivityExecutionList()
					.add(createMockActivityExecutionFromMockResponse(response, mockActivity, transactionId));
		});
	}

	public void executeWinAndNegativeBet(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCentsPrimary,
			final long amountCentsSecondary,
			final boolean roundEnd) {

		ArrayList<MockResponse> mockResponseList = casinoProviderMock.winAndNegativeBet(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCentsPrimary)
						.amountCentsSecondaryAction(amountCentsSecondary)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockResponseList.forEach(response -> {
			mockActivity
					.getMockActivityExecutionList()
					.add(createMockActivityExecutionFromMockResponse(response, mockActivity, transactionId));
		});
	}

	public void executeBetAndRefund(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCentsPrimary,
			final long amountCentsSecondary,
			final boolean roundEnd) {

		ArrayList<MockResponse> mockResponseList =  casinoProviderMock.betAndRefund(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCentsPrimary)
						.amountCentsSecondaryAction(amountCentsSecondary)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockResponseList.forEach(response -> {
			mockActivity
					.getMockActivityExecutionList()
					.add(createMockActivityExecutionFromMockResponse(response, mockActivity, transactionId));
		});
	}

	public void executeWinAndRefund(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCentsPrimary,
			final long amountCentsSecondary,
			final boolean roundEnd) {

		ArrayList<MockResponse> mockResponseList =  casinoProviderMock.winAndRefund(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCentsPrimary)
						.amountCentsSecondaryAction(amountCentsSecondary)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockResponseList.forEach(response -> {
			mockActivity
					.getMockActivityExecutionList()
					.add(createMockActivityExecutionFromMockResponse(response, mockActivity, transactionId));
		});
	}

	public void executeNegativeBetAndRefund(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCentsPrimary,
			final long amountCentsSecondary,
			final boolean roundEnd) {

		ArrayList<MockResponse> mockResponseList =  casinoProviderMock.negativeBetAndRefund(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCentsPrimary)
						.amountCentsSecondaryAction(amountCentsSecondary)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockResponseList.forEach(response -> {
			mockActivity
					.getMockActivityExecutionList()
					.add(createMockActivityExecutionFromMockResponse(response, mockActivity, transactionId));
		});
	}

	public void executeFreespinAndRefund(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCentsPrimary,
			final long amountCentsSecondary,
			final boolean roundEnd) {

		ArrayList<MockResponse> mockResponseList =  casinoProviderMock.freespinAndRefund(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCentsPrimary)
						.amountCentsSecondaryAction(amountCentsSecondary)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockResponseList.forEach(response -> {
			mockActivity
					.getMockActivityExecutionList()
					.add(createMockActivityExecutionFromMockResponse(response, mockActivity, transactionId));
		});
	}

	public void executeFreespinFeatureAndRefund(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCentsPrimary,
			final long amountCentsSecondary,
			final boolean roundEnd) {

		ArrayList<MockResponse> mockResponseList =  casinoProviderMock.freespinFeatureAndRefund(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCentsPrimary)
						.amountCentsSecondaryAction(amountCentsSecondary)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockResponseList.forEach(response -> {
			mockActivity
					.getMockActivityExecutionList()
					.add(createMockActivityExecutionFromMockResponse(response, mockActivity, transactionId));
		});
	}

	public void executeBetAndWinAndRefund(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCentsPrimary,
			final long amountCentsSecondary,
			final boolean roundEnd) {

		ArrayList<MockResponse> mockResponseList =  casinoProviderMock.betAndWinAndRefund(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCentsPrimary)
						.amountCentsSecondaryAction(amountCentsSecondary)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockResponseList.forEach(response -> {
			mockActivity
					.getMockActivityExecutionList()
					.add(createMockActivityExecutionFromMockResponse(response, mockActivity, transactionId));
		});
	}

	public void executeWinAndNegativeBetAndRefund(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity,
			final String transactionId,
			final long amountCentsPrimary,
			final long amountCentsSecondary,
			final boolean roundEnd) {

		ArrayList<MockResponse> mockResponseList = casinoProviderMock.winAndNegativeBetAndRefund(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.transactionId(produceTransactionIdIfNotProvided(mockSession, transactionId))
						.amountCentsPrimaryAction(amountCentsPrimary)
						.amountCentsSecondaryAction(amountCentsSecondary)
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.roundEnd(roundEnd)
						.build());

		mockResponseList.forEach(response -> {
			mockActivity
					.getMockActivityExecutionList()
					.add(createMockActivityExecutionFromMockResponse(response, mockActivity, transactionId));
		});
	}

	public void executeBalance(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity) {

		MockResponse mockResponse = casinoProviderMock.balance(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.build());

		mockActivity
				.getMockActivityExecutionList()
				.add(createMockActivityExecutionFromMockResponse(mockResponse, mockActivity, null));
	}


	public void executeValidateUser(
			final CasinoProviderMock casinoProviderMock,
			final MockSession mockSession,
			MockActivity mockActivity) {

		MockResponse mockResponse = casinoProviderMock.validateUser(
				ProviderMockPayload.builder()
						.userGuid(mockSession.getUserGuid())
						.authToken(mockSession.getAuthToken())
						.providerGameId(mockSession.getProviderGameId())
						.currency(mockSession.getCurrency())
						.roundId(mockActivity.getRoundId())
						.build());

		mockActivity
				.getMockActivityExecutionList()
				.add(createMockActivityExecutionFromMockResponse(mockResponse, mockActivity, null));
	}

	/**
	 * Produce a mock activity execution container to use for attachement to the activity list.
	 * @param mockResponse
	 * @return MockActivityExecution
	 */
	private MockActivityExecution createMockActivityExecutionFromMockResponse(final MockResponse mockResponse, final MockActivity mockActivity, final String transactionId) {
		MockActivityExecution mockActivityExecution = new MockActivityExecution();
		if (mockResponse != null) {
			mockActivityExecution.setEmulatedProviderRequest(mockResponse.getEmulatedRequestToLithium()); //Request to lithium
			mockActivityExecution.setEmulatedLithiumResponse(mockResponse.getResponseFromLithium()); //Response from lithium
			mockActivityExecution.setResponse(mockResponse.getMockResponseStatus().getStatus());
			mockActivityExecution.setExecutionDurationMs(mockResponse.getExecutionTimeMs());
			mockActivityExecution.setLithiumTransactionId(mockResponse.getLithiumTransactionId());
		}
		mockActivityExecution.setMockActivity(mockActivity);
		mockActivityExecution.setTransactionId(transactionId);
		mockActivityExecutionRepository.save(mockActivityExecution);

		return mockActivityExecution;
	}
	/**
	 * Produce a concatinated API token with user guid from auth token.
	 *
	 * @param authToken
	 * @return
	 */
	private String getApiTokenFromAuthToken(String authToken) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
		String apiToken = util.getJwtUser().getApiToken();
		apiToken += "|" + util.getJwtUser().getDomainName() + "/" + util.getJwtUser().getUsername();
		return apiToken;
	}

	/**
	 * Extract user guid from auth token.
	 *
	 * @param authToken
	 * @return
	 */
	private String getUserGuidFromAuthToken(String authToken) {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, authToken).build();
		String userGuid = util.getJwtUser().getDomainName() + "/" + util.getJwtUser().getUsername();
		return userGuid;
	}

	public void saveActivity(MockActivity mockActivity) {
		mockActivity = mockActivityRepository.save(mockActivity);
	}
}
