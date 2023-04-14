package lithium.service.cashier.mock.vespay.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.processor.vespay.data.RouterTransactionResponse;

@RestController
@RequestMapping("/v2/transaction")
public class RouterTransactionController {
	@Autowired LithiumConfigurationProperties config;
	
	@RequestMapping("/request")
	public RouterTransactionResponse routerTransaction(
		@RequestParam("apikey") String apiKey,
		@RequestParam("firstname") String firstName,
		@RequestParam("lastname") String lastName,
		@RequestParam("email") String email,
		@RequestParam("address") String address,
		@RequestParam("postcode") String postCode,
		@RequestParam("city") String city,
		@RequestParam("countrycode") String countryCode,
		@RequestParam("statecode") String stateCode,
		@RequestParam("phonehome") String phoneHome,
		@RequestParam("phonemobile") String phoneMobile,
		@RequestParam("language") String language,
		@RequestParam("webreaderagent") String webReaderAgent,
		@RequestParam("ipaddress") String ipAddress,
		@RequestParam("traceid") String traceId,
		@RequestParam("username") String userName,
		@RequestParam("usercreateddate") String userCreatedDate,
		@RequestParam(value="userprofile", required=false) String userProfile,
		@RequestParam(value="blackbox", required=false) String blackBox,
		@RequestParam(value="amount", required=false) Integer amount,
		@RequestParam(value="currency", required=false) String currency,
		HttpServletRequest webRequest,
		HttpServletResponse webResponse
	) {
		RouterTransactionResponse response = RouterTransactionResponse.builder()
		.sender("router")
		.apiKey(apiKey)
		.approved(true)
		.errorCode(0)
		.errorDescription("")
		.parameterErrors("")
		.redirectUrl(
			config.getGatewayPublicUrl() +
			"/service-cashier-mock-vespay/v2/shop/purchase?" +
			"apikey=" + apiKey +
			"&traceid=" + traceId
		)
		.transactionId(Integer.parseInt(traceId))
		.traceId(traceId)
		.dateTimeCreated("2018-11-26 13:36:52")
		.timeZone("")
		.build();
		return response;
	}
}
