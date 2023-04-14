package lithium.service.casino.client;

import lithium.service.casino.client.objects.GenericMockPayload;
import lithium.service.casino.client.objects.StartGameMock;
import lombok.Getter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@FeignClient(name="service-casino", path="/casino/mock")
public interface CasinoGenericMock {

	@RequestMapping(path="/bet", method=RequestMethod.POST)
	public @ResponseBody Object bet(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/win", method=RequestMethod.POST)
	public @ResponseBody Object win(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/negativeBet", method=RequestMethod.POST)
	public @ResponseBody Object negativeBet(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/betAndWin", method= RequestMethod.POST)
	public @ResponseBody Object betAndWin(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/refund", method=RequestMethod.POST)
	public @ResponseBody Object refund(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/freespin", method=RequestMethod.POST)
	public @ResponseBody Object freespin(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/freespinFeature", method=RequestMethod.POST)
	public @ResponseBody Object freespinFeature(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/winAndNegativeBet", method=RequestMethod.POST)
	public @ResponseBody Object winAndNegativeBet(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/betAndRefund", method=RequestMethod.POST)
	public @ResponseBody Object betAndRefund(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/winAndRefund", method=RequestMethod.POST)
	public @ResponseBody Object winAndRefund(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/negativeBetAndRefund", method=RequestMethod.POST)
	public @ResponseBody Object negativeBetAndRefund(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/winAndNegativeBetAndRefund", method=RequestMethod.POST)
	public @ResponseBody Object winAndNegativeBetAndRefund(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/freespinAndRefund", method=RequestMethod.POST)
	public @ResponseBody Object freespinAndRefund(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/freespinFeatureAndRefund", method=RequestMethod.POST)
	public @ResponseBody Object freespinFeatureAndRefund(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/betAndWinAndRefund", method=RequestMethod.POST)
	public @ResponseBody Object betAndWinAndRefund(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/balance", method=RequestMethod.POST)
	public @ResponseBody Object balance(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/validateUser", method=RequestMethod.POST)
	public @ResponseBody Object validateUser(@RequestBody GenericMockPayload mockPayload);

	@RequestMapping(path="/startGame", method=RequestMethod.POST)
	public @ResponseBody String startGame(@RequestBody StartGameMock startGameMock);


	/**
	 * Identification method for the single or chained actions that will be performed on the game provider implementation
	 */
	public enum MockActionType {
		BET ("BET"),
		WIN ("WIN"),
		NEGATIVE_BET ("NEGATIVE_BET"),
		FREESPIN ("FREESPIN"),
		FREESPIN_FEATURE ("FREESPIN_FEATURE"),
		BET_AND_WIN ("BET_AND_WIN"),
		WIN_AND_NEGATIVE_BET ("WIN_AND_NEGATIVE_BET"),
		REFUND_NON_EXISTENT_TRANSACTION ("REFUND_NON_EXISTENT_TRANSACTION"),
		BET_AND_REFUND ("BET_AND_REFUND"),
		WIN_AND_REFUND ("WIN_AND_REFUND"),
		NEGATIVE_BET_AND_REFUND ("NEGATIVE_BET_AND_REFUND"),
		FREESPIN_AND_REFUND ("FREESPIN_AND_REFUND"),
		FREESPIN_FEATURE_AND_REFUND ("FREESPIN_FEATURE_AND_REFUND"),
		BET_AND_WIN_AND_REFUND ("BET_AND_WIN_AND_REFUND"),
		WIN_AND_NEGATIVE_BET_AND_REFUND ("WIN_AND_NEGATIVE_BET_AND_REFUND"),
		BALANCE ("BALANCE"),
		VALIDATE_USER ("VALIDATE_USER");

		@Getter
		String label;

		MockActionType(String label) {
			this.label = label;
		}
	}
}