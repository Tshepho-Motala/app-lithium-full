package lithium.service.cashier.mock.btc.globalbitlocker.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import lithium.service.cashier.processor.btc.globalbitlocker.data.ReceiveAddressRequest;
import lithium.service.cashier.processor.btc.globalbitlocker.data.ReceiveAddressResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v2/receive")
@Slf4j
public class GlobalBitLockerMockController {
//	@Autowired
//	private Configuration config;
	
	@GetMapping
	public ReceiveAddressResponse generateAddress(ReceiveAddressRequest request, WebRequest webRequest) throws Exception {
		log.info("GenerateAddressRequest "+request+", WebRequest "+webRequest);
//		RequestOrResponse<DepositRequest> request = new RequestOrResponse<>(config.getApiSecret());
//		request.payloadFromHeaders(webRequest, DepositRequest.class);
//		
//		Long amountCents = Long.parseLong(request.getData().getAmountUsdCents());
//		log.info("Amount cents " + amountCents);
//		double amountBtc = amountCents.floatValue() / 6400000.00;
//		log.info("Amount btc " + amountBtc);
//		Long amountSatoshis = Math.round(amountBtc / 0.00000001);
//		log.info("Amount satoshis: " + amountSatoshis);
//		
//		DepositResponse response = new DepositResponse();
//		response.setSuccess("true");
//		response.setAddress("1812xUMpCLvEdJgJxEy4XYTTDBsXVRquxM");
//		response.setId(new Long(new Date().getTime()).toString());
//		response.setRequestAmountBtcSatoshis(amountSatoshis.toString());
		
		ReceiveAddressResponse processorResponse = ReceiveAddressResponse.builder()
			.address("3QJmV3qfvL9SuYo34YihAf3sRCW3qSinyC")
			.build();
//		
		return processorResponse;
	}
}