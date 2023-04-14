package lithium.service.cashier.mock.quickbit.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.mock.quickbit.Configuration;
import lithium.service.cashier.processor.quickbit.data.TransactionResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/direct_api")
@Slf4j
public class TransactionController {
	@Autowired LithiumConfigurationProperties properties;
	@Autowired Configuration config;
	
	@PostMapping("/create_transaction")
	public TransactionResponse createTransaction(
		@RequestParam("first_name") String firstName,
		@RequestParam("last_name") String lastName,
		@RequestParam("email") String email,
		@RequestParam("dob") String dob,
		@RequestParam(value="phone_no", required=false) String phoneNo,
		@RequestParam("address") String address,
		@RequestParam("state_code") String stateCode,
		@RequestParam("postal_code") String postalCode,
		@RequestParam("city") String city,
		@RequestParam("country_code") String countryCode,
		@RequestParam(value="fiat_amount", required=false) String fiatAmount,
		@RequestParam("fiat_currency") String fiatCurrency,
		@RequestParam(value="crypto_amount", required=false) String cryptoAmount,
		@RequestParam("crypto_currency") String cryptoCurrency,
		@RequestParam("affiliate_referral_code") String affiliateReferralCode,
		@RequestParam("callback_url") String callbackUrl,
		@RequestParam("request_reference") String requestReference,
		@RequestParam("merchant_profile") String merchantProfile,
		@RequestParam("affiliate_redirect_url") String affiliateRedirectUrl,
		@RequestParam(value="settlement_currency", required=false) String settlementCurrency,
		@RequestParam("checksum") String checksum
	) throws UnsupportedEncodingException {
		log.info("Received transaction request (firstName: " + firstName + ", lastName: " + lastName + ", email: " + email
				+ ", dob: " + dob + ", phoneNo: " + phoneNo + ", address: " + address + ", stateCode: " + stateCode
				+ ", postalCode: " + postalCode + ", city: " + city + ", countryCode: " + countryCode + ", fiatAmount: " + fiatAmount
				+ ", fiatCurrency: " + fiatCurrency + ", cryptoAmount: " + cryptoAmount + ", cryptoCurrency: " + cryptoCurrency
				+ ", affiliateReferralCode: " + affiliateReferralCode + ", callbackUrl: " + callbackUrl + ", requestReference: " + requestReference
				+ ", merchantProfile: " + merchantProfile + ", affiliateRedirectUrl: " + affiliateRedirectUrl + ", callbackUrl: " + callbackUrl
				+ ", settlementCurrency: " + settlementCurrency + ", checksum: " + checksum);
		
		TransactionResponse response = TransactionResponse.builder()
		.statusCode("201")
		.statusMsg("Transaction Request Accepted. You may now redirect the user to the url provided in the redirect_url key.")
		.redirectUrl(
			properties.getGatewayPublicUrl() + "/service-cashier-mock-quickbit/process-direct-api-buy-request/?"
			+ "redirecturl=" + URLEncoder.encode(affiliateRedirectUrl, "UTF-8")
			+ "&requestreference=" + requestReference
			+ "&fiatamount=" + fiatAmount
		)
		.requestReference(requestReference)
		.build();
		
		response.setChecksum(response.calculateHash(config.getSecretKey()));
		
		return response;
	}
}
