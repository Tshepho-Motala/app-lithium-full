package lithium.service.cashier.mock.vespay.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.cashier.processor.vespay.data.VoucherRedemptionResponse;

@RestController
@RequestMapping("/v2/voucher")
public class VoucherRedemptionController {
	@RequestMapping("/redeem")
	public VoucherRedemptionResponse redeemVoucher(
		@RequestParam("apikey") String apiKey,
		@RequestParam("firstname") String firstName,
		@RequestParam("lastname") String lastName,
		@RequestParam("email") String email,
		@RequestParam("language") String language,
		@RequestParam("webreaderagent") String webReaderAgent,
		@RequestParam("ipaddress") String ipAddress,
		@RequestParam("traceid") String traceId,
		@RequestParam("username") String username,
		@RequestParam("usercreateddate") String userCreatedDate,
		@RequestParam(value="userprofile", required=false) String userProfile,
		@RequestParam(value="blackbox", required=false) String blackBox,
		@RequestParam("vouchercode") String voucherCode
	) {
		VoucherRedemptionResponse response = VoucherRedemptionResponse.builder()
		.sender("voucher")
		.apiKey(apiKey)
		.approved(true)
		.errorCode(0)
		.errorDescription("")
		.parameterErrors("")
		.traceId(traceId)
		.transactionId(Integer.parseInt(traceId))
		.orderId(Integer.parseInt(traceId))
		.amount(2500)
		.currency("USD")
		.bin(123)
		.billingDescriptor("")
		.messageClient("")
		.dateTimeCreated("2019-04-18 13:20:37")
		.build();
		return response;
	}
}
