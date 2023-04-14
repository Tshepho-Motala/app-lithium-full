package lithium.service.casino.mock.all.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lithium.service.casino.client.CasinoGenericMock;
import lithium.service.casino.client.CasinoProviderMock;
import lithium.service.casino.client.objects.GenericMockPayload;
import lithium.service.casino.client.objects.StartGameMock;
import lithium.service.casino.mock.all.entities.MockActivity;
import lithium.service.casino.mock.all.entities.MockSession;
import lithium.service.casino.mock.all.service.MockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/casino/mock")
public class MockController implements CasinoGenericMock {
	@Autowired protected MockService mockService;
	@Autowired protected TokenStore tokenStore;

	@RequestMapping(path="/startGame", method=RequestMethod.POST)
	public @ResponseBody String startGame(@RequestBody StartGameMock startGameMock) {
		//register session
		//build up url for game start (fake game frontend)
		MockSession mockSession = mockService.findOrRegisterSession(
				startGameMock.getAuthToken(),
				startGameMock.getStartGameUrl(),
				startGameMock.getGameProviderGuid(),
				startGameMock.getProviderGameId(),
				startGameMock.getCurrency());
		return mockService.generateMockGameStartUrl(mockSession);
	}

	@RequestMapping(path="/bet/string", method= RequestMethod.POST)
	public @ResponseBody ArrayList<String> betString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.BET);
	}

	@RequestMapping(path="/win/string", method= RequestMethod.POST)
	public @ResponseBody ArrayList<String> winString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.WIN);
	}

	@RequestMapping(path="/betAndWin/string", method= RequestMethod.POST)
	public @ResponseBody ArrayList<String> betAndWinString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.BET_AND_WIN);
	}

	@RequestMapping(path="/negativeBet/string", method= RequestMethod.POST)
	public @ResponseBody ArrayList<String> negativeBetString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.NEGATIVE_BET);
	}

	@RequestMapping(path="/freespin/string", method=RequestMethod.POST)
	public @ResponseBody ArrayList<String> freespinString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.FREESPIN);
	}

	@RequestMapping(path="/refund/string", method= RequestMethod.POST)
	public @ResponseBody ArrayList<String> refundString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.REFUND_NON_EXISTENT_TRANSACTION);
	}

	@RequestMapping(path="/freespinFeature/string", method=RequestMethod.POST)
	public @ResponseBody ArrayList<String> freespinFeatureString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.FREESPIN_FEATURE);
	}

	@RequestMapping(path="/winAndNegativeBet/string", method=RequestMethod.POST)
	public @ResponseBody ArrayList<String> winAndNegativeBetString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.WIN_AND_NEGATIVE_BET);
	}

	@RequestMapping(path="/betAndRefund/string", method=RequestMethod.POST)
	public @ResponseBody ArrayList<String> betAndRefundString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.BET_AND_REFUND);
	}

	@RequestMapping(path="/winAndRefund/string", method=RequestMethod.POST)
	public @ResponseBody ArrayList<String> winAndRefundString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.WIN_AND_REFUND);
	}

	@RequestMapping(path="/negativeBetAndRefund/string", method=RequestMethod.POST)
	public @ResponseBody ArrayList<String> negativeBetAndRefundString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.NEGATIVE_BET_AND_REFUND);
	}

	@RequestMapping(path="/winAndNegativeBetAndRefund/string", method=RequestMethod.POST)
	public @ResponseBody ArrayList<String> winAndNegativeBetAndRefundString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.WIN_AND_NEGATIVE_BET_AND_REFUND);
	}

	@RequestMapping(path="/freespinAndRefund/string", method=RequestMethod.POST)
	public @ResponseBody ArrayList<String> freespinAndRefundString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.FREESPIN_AND_REFUND);
	}

	@RequestMapping(path="/freespinFeatureAndRefund/string", method=RequestMethod.POST)
	public @ResponseBody ArrayList<String> freespinFeatureAndRefundString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.FREESPIN_FEATURE_AND_REFUND);
	}

	@RequestMapping(path="/betAndWinAndRefund/string", method=RequestMethod.POST)
	public @ResponseBody ArrayList<String> betAndWinAndRefundString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.BET_AND_WIN_AND_REFUND);
	}

	@RequestMapping(path="/balance/string", method=RequestMethod.POST)
	public @ResponseBody ArrayList<String> balanceString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.BALANCE);
	}

	@RequestMapping(path="/validateUser/string", method=RequestMethod.POST)
	public @ResponseBody ArrayList<String> validateUserString(@RequestBody GenericMockPayload mockPayload) {
		return performActionString(mockPayload, MockActionType.VALIDATE_USER);
	}

//----------------------------
	@RequestMapping(path="/bet", method= RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity bet(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.BET);
	}

	@RequestMapping(path="/win", method= RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity win(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.WIN);
	}

	@RequestMapping(path="/betAndWin", method= RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity betAndWin(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.BET_AND_WIN);
	}

	@RequestMapping(path="/negativeBet", method= RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity negativeBet(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.NEGATIVE_BET);
	}

	@RequestMapping(path="/freespin", method=RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity freespin(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.FREESPIN);
	}

	@RequestMapping(path="/refund", method= RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity refund(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.REFUND_NON_EXISTENT_TRANSACTION);
	}

	@RequestMapping(path="/freespinFeature", method=RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity freespinFeature(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.FREESPIN_FEATURE);
	}

	@RequestMapping(path="/winAndNegativeBet", method=RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity winAndNegativeBet(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.WIN_AND_NEGATIVE_BET);
	}

	@RequestMapping(path="/betAndRefund", method=RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity betAndRefund(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.BET_AND_REFUND);
	}

	@RequestMapping(path="/winAndRefund", method=RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity winAndRefund(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.WIN_AND_REFUND);
	}

	@RequestMapping(path="/negativeBetAndRefund", method=RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity negativeBetAndRefund(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.NEGATIVE_BET_AND_REFUND);
	}

	@RequestMapping(path="/winAndNegativeBetAndRefund", method=RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity winAndNegativeBetAndRefund(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.WIN_AND_NEGATIVE_BET_AND_REFUND);
	}

	@RequestMapping(path="/freespinAndRefund", method=RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity freespinAndRefund(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.FREESPIN_AND_REFUND);
	}

	@RequestMapping(path="/freespinFeatureAndRefund", method=RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity freespinFeatureAndRefund(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.FREESPIN_FEATURE_AND_REFUND);
	}

	@RequestMapping(path="/betAndWinAndRefund", method=RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity betAndWinAndRefund(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.BET_AND_WIN_AND_REFUND);
	}

	@RequestMapping(path="/balance", method=RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity balance(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.BALANCE);
	}

	@RequestMapping(path="/validateUser", method=RequestMethod.POST)
	@Override
	public @ResponseBody MockActivity validateUser(@RequestBody GenericMockPayload mockPayload) {
		return performAction(mockPayload, MockActionType.VALIDATE_USER);
	}

	/**
	 * Generic execution function for mock action requests.
	 * @param mockPayload
	 * @param mockActionType
	 * @return
	 */
	private MockActivity performAction(GenericMockPayload mockPayload, final MockActionType mockActionType) {

		MockSession mockSession = null;
		CasinoProviderMock casinoProviderMock = null;
		MockActivity mockActivity = null;
		String transactionId = null;

		Optional<MockSession> mockSessionO = mockService.findMockSessionById(mockPayload.getMockSessionId());

		if (mockSessionO.isPresent()) {
			mockSession = mockSessionO.get();
			mockActivity = mockService.registerMockActivity(mockSessionO.get());
			mockActivity.setRequest(mockActionType.getLabel() + "_" +mockSession.getProviderGuid());

			//This means all executions will use the same transaction id, if none is provided all executions will have their own
			transactionId = mockService.produceTransactionIdIfNotProvided(mockSessionO.get(), null);

			Optional<CasinoProviderMock> casinoProviderMockO = mockService.findCasinoProviderMock(mockSessionO.get().getProviderGuid());

			if (casinoProviderMockO.isPresent()) {
				casinoProviderMock = casinoProviderMockO.get();

				//Run execution loop for the requested game emulation actions
				for (int i = 0; i <= mockPayload.getResendCount(); i++) {
					switch (mockActionType) {
						case BET:
							mockService.executeBet(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), true);
							break;

						case WIN:
							mockService.executeWin(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), true);
							break;

						case NEGATIVE_BET:
							mockService.executeNegativeBet(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), true);
							break;

						case REFUND_NON_EXISTENT_TRANSACTION:
							mockService.executeRefund(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), true);
							break;

						case FREESPIN:
							mockService.executeFreespin(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), true);
							break;

						case FREESPIN_FEATURE:
							mockService.executeFreespinFeature(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), true);
							break;

						case BET_AND_WIN:
							mockService.executeBetAndWin(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), mockPayload.getAmountCentsSecondaryAction(), true);
							break;

						case WIN_AND_NEGATIVE_BET:
							mockService.executeWinAndNegativeBet(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), mockPayload.getAmountCentsSecondaryAction(), true);
							break;

						case BET_AND_REFUND:
							mockService.executeBetAndRefund(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), mockPayload.getAmountCentsSecondaryAction(), true);
							break;

						case WIN_AND_REFUND:
							mockService.executeWinAndRefund(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), mockPayload.getAmountCentsSecondaryAction(), true);
							break;

						case NEGATIVE_BET_AND_REFUND:
							mockService.executeNegativeBetAndRefund(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), mockPayload.getAmountCentsSecondaryAction(), true);
							break;

						case FREESPIN_AND_REFUND:
							mockService.executeFreespinAndRefund(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), mockPayload.getAmountCentsSecondaryAction(), true);
							break;

						case FREESPIN_FEATURE_AND_REFUND:
							mockService.executeFreespinFeatureAndRefund(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), mockPayload.getAmountCentsSecondaryAction(), true);
							break;

						case BET_AND_WIN_AND_REFUND:
							mockService.executeBetAndWinAndRefund(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), mockPayload.getAmountCentsSecondaryAction(), true);
							break;

						case WIN_AND_NEGATIVE_BET_AND_REFUND:
							mockService.executeWinAndNegativeBetAndRefund(casinoProviderMock, mockSession, mockActivity, transactionId, mockPayload.getAmountCentsPrimaryAction(), mockPayload.getAmountCentsSecondaryAction(), true);
							break;

						case BALANCE:
							mockService.executeBalance(casinoProviderMock, mockSession, mockActivity);
							break;

						case VALIDATE_USER:
							mockService.executeValidateUser(casinoProviderMock, mockSession, mockActivity);
							break;

						default:
							log.error("No implementation for action execution of: " + mockActionType);
					}

					try {
						Thread.sleep(mockPayload.getDelayBetweenResendMs());
					} catch (InterruptedException e) {
						log.warn("Could not sleep execution thread", e);
					}
				}
				// return casinoProviderMock.get().bet(LithiumTokenUtil.builder(tokenStore, mockPayload.getJwt().replace("Bearer ", "")).build().guid(), "someTransactionId", mockPayload.getAmountCentsPrimaryAction());

			} else{
				log.error("Client service lookup not present for: " + mockPayload.toString());
			}
		}
		mockService.saveActivity(mockActivity);

		//Add mock label to trans
		mockService.addMockLabelsToTransactions(mockActivity);

		return mockActivity;
	}

	/**
	 * Generic execution function for mock action requests.
	 * @param mockPayload
	 * @param mockActionType
	 * @return
	 */
	//TODO: Decide if we want to move this to service level.
	private ArrayList<String> performActionString(GenericMockPayload mockPayload, final MockActionType mockActionType) {

		ArrayList<String> al = new ArrayList<>();

		ObjectMapper mappr = new ObjectMapper();
		mappr.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			al.add(mappr.writeValueAsString(performAction(mockPayload, mockActionType)));
		} catch (JsonProcessingException e) {
			log.warn("Unable to serialize response object");
		}

		return al;
	}

	@RequestMapping(path="/test")
	public @ResponseBody
	MockSession test(@RequestBody String mockSessionId) {
		return mockService.findMockSessionById(Long.parseLong(mockSessionId)).get();
	}

	@RequestMapping(path="/test/string")
	public @ResponseBody
	String testString(@RequestBody String mockSessionId) {
		return mockService.findMockSessionById(Long.parseLong(mockSessionId)).get().toString();
	}
}
