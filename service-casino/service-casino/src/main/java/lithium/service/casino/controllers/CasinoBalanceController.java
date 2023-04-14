package lithium.service.casino.controllers;

import lithium.metrics.TimeThisMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.casino.client.objects.request.BalanceRequest;
import lithium.service.casino.client.objects.response.BalanceResponse;
import lithium.service.casino.config.ServiceCasinoConfigurationProperties;
import lithium.service.casino.service.CasinoService;

@RestController
@EnableConfigurationProperties(ServiceCasinoConfigurationProperties.class)
// no tight coupling between client and service (so no implementation or
// dependence on service-client
public class CasinoBalanceController {
	@Autowired
	ServiceCasinoConfigurationProperties serviceGamesConfig;
//	@Autowired
//	private LithiumServiceClientFactory services;
//	@Autowired
//	private ModelMapper mapper;
	@Autowired
	private CasinoService casinoService;

	@TimeThisMethod
	@RequestMapping("/casino/getBalance")
	public BalanceResponse handleBalanceRequest(@RequestBody BalanceRequest request) throws Exception {
		String currency = (request.getCurrencyCode() != null && !request.getCurrencyCode().isEmpty())
				? request.getCurrencyCode()
				: casinoService.getCurrency(request.getDomainName());
		BalanceResponse br = BalanceResponse.builder()
		.balanceCents(
			casinoService.getCustomerBalanceWithError(
				currency, request.getDomainName(), request.getUserGuid()
			)
		)
		.build();
		return br;
	}
}
