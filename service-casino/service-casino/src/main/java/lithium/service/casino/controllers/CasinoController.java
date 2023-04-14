package lithium.service.casino.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.client.objects.request.BonusReleaseRequest;
import lithium.service.casino.client.objects.request.BonusWinRequest;
import lithium.service.casino.client.objects.request.RefundRequest;
import lithium.service.casino.client.objects.response.BonusReleaseResponse;
import lithium.service.casino.client.objects.response.BonusWinResponse;
import lithium.service.casino.client.objects.response.RefundResponse;
import lithium.service.casino.config.ServiceCasinoConfigurationProperties;
import lithium.service.casino.service.CasinoService;
import lombok.extern.slf4j.Slf4j;

@EnableConfigurationProperties(ServiceCasinoConfigurationProperties.class)
@Slf4j
@RestController
// no tight coupling between client and service (so no implementation or
// dependence on service-client
public class CasinoController {
	@Autowired
	ServiceCasinoConfigurationProperties serviceGamesConfig;
//	@Autowired
//	private LithiumServiceClientFactory services;
//	@Autowired
//	private ModelMapper mapper;
	@Autowired
	private CasinoService casinoService;
	
	@RequestMapping("/casino/bonusRelease")
	public BonusReleaseResponse handleBonusReleaseRequest(@RequestBody BonusReleaseRequest request) throws Exception {
		BonusReleaseResponse brr = BonusReleaseResponse.builder()
				.build(); // call accounting service for bonus release
		//TODO: method stub. needs service call as body
		return brr;
	}
//	@RequestMapping("/casino/bonusWin")
//	BonusWinResponse handleBonusWinRequest(@RequestBody BonusWinRequest request) throws Exception {
//		BonusWinResponse bwr = BonusWinResponse.builder()
//				.balanceCents(2222L) // call balance service and accounting service
//				.build();
//		//TODO: method stub. needs service call as body
//		return bwr;
//	}
	@RequestMapping("/casino/refund")
	RefundResponse handleRefundRequest(@RequestBody RefundRequest request) throws Exception {
		RefundResponse rr = RefundResponse.builder()
				.extSystemTransactionId("transactionidhere") // call accounting service
				.build();
		//TODO: method stub. needs service call as body
		return rr;
	}
}
