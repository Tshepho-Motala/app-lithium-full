package lithium.service.casino.mock.twowinpower.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lithium.service.casino.mock.twowinpower.Configuration;
import lithium.service.casino.mock.twowinpower.service.TwoWinPowerMockService;
import lithium.service.casino.provider.twowinpower.response.Response;

@RestController
public class ManualActionController {
	@Autowired
	@Qualifier("lithium.service.casino.mock.twowinpower.resttemplate")
	private RestTemplate restTemplate;
	
	@Autowired
	private Configuration conf;
	@Autowired
	private TwoWinPowerMockService service;
	
	@RequestMapping("/manual")
	public Response headers() {
		HttpHeaders headers = service.buildHeaders(conf.getMerchantId(), conf.getMerchantKey());
		
		return Response.builder().errorDescription(headers.toString()).build();
	}
	
	@RequestMapping("/manual/balance")
	public Response balance(
		@RequestParam String playerGuid
	) throws Exception {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("action", "balance");
		parameters.put("player_id", playerGuid);
		parameters.put("currency", "USD");
		
		HttpEntity<String> entity = new HttpEntity<>(service.buildHeaders(conf.getMerchantId(), conf.getMerchantKey(), parameters));
		
		ResponseEntity<Response> result = restTemplate.exchange(
			conf.getEndpointUrl()+"?action={action}&player_id={player_id}&currency={currency}",
			HttpMethod.GET,
			entity,
			Response.class,
			parameters
		);
		
		return result.getBody();
	}
	
	@RequestMapping("/manual/bet")
	public Response bet(
		@RequestParam String playerGuid,
		@RequestParam String amount,
		@RequestParam String gameUuid,
		@RequestParam String sessionId,
		@RequestParam String transactionId
	) throws Exception {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("action", "bet");
		parameters.put("amount", amount);
		parameters.put("game_uuid", gameUuid);
		parameters.put("player_id", playerGuid);
		parameters.put("session_id", sessionId);
		parameters.put("transaction_id", transactionId);
		
		HttpEntity<String> entity = new HttpEntity<>(service.buildHeaders(conf.getMerchantId(), conf.getMerchantKey(), parameters));
		
		ResponseEntity<Response> result = restTemplate.exchange(
			conf.getEndpointUrl()+"?action={action}&player_id={player_id}&amount={amount}&game_uuid={game_uuid}&session_id={session_id}&transaction_id={transaction_id}",
			HttpMethod.POST,
			entity,
			Response.class,
			parameters
		);
		
		return result.getBody();
	}
}