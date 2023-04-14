package lithium.service.casino.client;

import lithium.service.casino.client.objects.MockResponse;
import lithium.service.casino.client.objects.ProviderMockPayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;

@FeignClient(name="service-casino", path="/casino/mock")
public interface CasinoProviderMock {

	@RequestMapping(path="/bet", method=RequestMethod.POST)
	public @ResponseBody MockResponse bet(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/win", method=RequestMethod.POST)
	public @ResponseBody MockResponse win(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/negativeBet", method=RequestMethod.POST)
	public @ResponseBody MockResponse negativeBet(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/betAndWin", method= RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> betAndWin(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/refund", method=RequestMethod.POST)
	public @ResponseBody MockResponse refund(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/freespin", method=RequestMethod.POST)
	public @ResponseBody MockResponse freespin(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/freespinFeature", method=RequestMethod.POST)
	public @ResponseBody MockResponse freespinFeature(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/winAndNegativeBet", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> winAndNegativeBet(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/betAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> betAndRefund(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/winAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> winAndRefund(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/negativeBetAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> negativeBetAndRefund(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/winAndNegativeBetAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> winAndNegativeBetAndRefund(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/freespinAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> freespinAndRefund(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/freespinFeatureAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> freespinFeatureAndRefund(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/betAndWinAndRefund", method=RequestMethod.POST)
	public @ResponseBody ArrayList<MockResponse> betAndWinAndRefund(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/balance", method=RequestMethod.POST)
	public @ResponseBody MockResponse balance(@RequestBody ProviderMockPayload mockPayload);

	@RequestMapping(path="/validateUser", method=RequestMethod.POST)
	public @ResponseBody MockResponse validateUser(@RequestBody ProviderMockPayload mockPayload);
}