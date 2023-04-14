package lithium.service.cashier.processor.quickbit.data;

import java.math.BigDecimal;

import lithium.service.cashier.processor.quickbit.util.HashCalculator;
import lithium.util.FormParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Slf4j
public class TransactionRequest {
	@FormParam("first_name")
	private String firstName;
	@FormParam("last_name")
	private String lastName;
	@FormParam("email")
	private String email;
	@FormParam("dob")
	private String dateOfBirth; // yyyy-mm-dd
	@FormParam("phone_no") // Not required
	private String phoneNumber;
	@FormParam("address")
	private String address;
	@FormParam("state_code")
	private String stateCode;
	@FormParam("postal_code")
	private String postalCode;
	@FormParam("city")
	private String city;
	@FormParam("country_code")
	private String countryCode;
	@FormParam("fiat_amount") // Not required
	private BigDecimal fiatAmount;
	@FormParam("fiat_currency")
	private String fiatCurrency;
	@FormParam("crypto_amount")
	private String cryptoAmount;
	@FormParam("crypto_currency")
	private String cryptoCurrency;
	@FormParam("affiliate_referral_code")
	private String affiliateReferralCode;
	@FormParam("callback_url")
	private String callbackUrl;
	@FormParam("request_reference")
	private String requestReference;
	@FormParam("merchant_profile")
	private Integer merchantProfile; // A unique id to differentiate the users. This value should be between 1 to 7 included.
	@FormParam("affiliate_redirect_url")
	private String affiliateRedirectUrl;
	@FormParam("settlement_currency")
	private String settlementCurrency; // Not required
	@FormParam("checksum")
	private String checksum;
	
	public String calculateHash(String secret) {
		HashCalculator calc = new HashCalculator(secret);
		if (firstName != null && !firstName.isEmpty()) calc.addItem("first_name", firstName);
		if (lastName != null && !lastName.isEmpty()) calc.addItem("last_name", lastName);
		if (email != null && !email.isEmpty()) calc.addItem("email", email);
		if (dateOfBirth != null && !dateOfBirth.isEmpty()) calc.addItem("dob", dateOfBirth);
		if (phoneNumber != null && !phoneNumber.isEmpty()) calc.addItem("phone_no", phoneNumber);
		if (address != null && !address.isEmpty()) calc.addItem("address", address);
		if (stateCode != null && !stateCode.isEmpty()) calc.addItem("state_code", stateCode);
		if (postalCode != null && !postalCode.isEmpty()) calc.addItem("postal_code", postalCode);
		if (city != null && !city.isEmpty()) calc.addItem("city", city);
		if (countryCode != null && !countryCode.isEmpty()) calc.addItem("country_code", countryCode);
		if (fiatAmount != null) calc.addItem("fiat_amount", fiatAmount.toPlainString());
		if (fiatCurrency != null && !fiatCurrency.isEmpty()) calc.addItem("fiat_currency", fiatCurrency);
		if (cryptoAmount != null && !cryptoAmount.isEmpty()) calc.addItem("crypto_amount", cryptoAmount);
		if (cryptoCurrency != null && !cryptoCurrency.isEmpty()) calc.addItem("crypto_currency", cryptoCurrency);
		if (affiliateReferralCode != null && !affiliateReferralCode.isEmpty()) calc.addItem("affiliate_referral_code", affiliateReferralCode);
		if (callbackUrl != null && !callbackUrl.isEmpty()) calc.addItem("callback_url", callbackUrl);
		if (requestReference != null && !requestReference.isEmpty()) calc.addItem("request_reference", requestReference);
		if (merchantProfile != null) calc.addItem("merchant_profile", String.valueOf(merchantProfile));
		if (affiliateRedirectUrl != null && !affiliateRedirectUrl.isEmpty()) calc.addItem("affiliate_redirect_url", affiliateRedirectUrl);
		if (settlementCurrency != null && !settlementCurrency.isEmpty()) calc.addItem("settlement_currency", settlementCurrency);
		String hash = calc.calculateHash();
		log.info("Calculating hash using secret: " + secret + " " + this.toString() + " hash " + hash);
		return hash;
	}
}
