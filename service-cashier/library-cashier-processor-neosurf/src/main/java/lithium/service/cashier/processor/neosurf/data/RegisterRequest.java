package lithium.service.cashier.processor.neosurf.data;

import java.math.BigDecimal;

import lithium.service.cashier.processor.neosurf.util.HashCalculator;
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
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class RegisterRequest {

	private String merchantId;
	private String userKyc;
	private String currency;
	private String expiry;
	private String hash;
	private String kycNeosurfEmail;
	private String kycNeosurfPin3;
	private String language;
	private String merchantTransactionId;

	private String subMerchantId;
	private String prohibitedForMinors;
	private String test;
	private String urlOk;
	private String urlKo;
	private String urlPending;
	private String urlCallback;
	private String userCity;
	private String userCountry;
	private String userDateOfBirth;
	private String userEmail;
	private String userFirstName;
	private String userGender;
	private String userIp;
	private String userLastName;
	private String userLatitude;
	private String userLongitude;
	private String userStreet;
	private String userTelephoneNumber;
	private String userZipCode;
	@Builder.Default
	private String version = "3";
	private BigDecimal amount;
	private String checksum;

	public String generateHash(String checksum) {
		HashCalculator calc = new HashCalculator(checksum);

		log.debug("Application Secret " + checksum + "\n");
		if (this.amount.floatValue() > 0) {
			calc.addItem("ammount", this.amount.toString());
			log.debug("ammount added " + this.amount.toString() + " \n");
		}

		if (this.currency != null && !this.currency.isEmpty()) {
			calc.addItem("currency", this.currency);
			log.debug("currency added;" + this.currency + "\n");
		}

		if (this.expiry != null && !this.expiry.isEmpty()) {
			calc.addItem("expiry", this.expiry);
			log.debug("expiry added" + this.expiry + "\n");
		}

		if (this.kycNeosurfEmail != null && !this.kycNeosurfEmail.isEmpty()) {
			calc.addItem("kycNeosurfEmail", this.kycNeosurfEmail);
			log.debug("kycNeosurfEmail added " + this.kycNeosurfEmail + ";\n");
		}

		if (this.kycNeosurfPin3 != null && !this.kycNeosurfPin3.isEmpty()) {
			calc.addItem("kycNeosurfPin3", this.kycNeosurfPin3);
			log.debug("kycNeosurfPin3 added;" + this.kycNeosurfPin3 + "\n");
		}

		if (this.language != null && !this.language.isEmpty()) {
			calc.addItem("language", this.language);
			log.debug("language added;" + this.language + "\n");
		}

		if (this.merchantId != null && !this.merchantId.isEmpty()) {
			calc.addItem("merchantId", String.valueOf(this.merchantId));
			log.debug("merchantId added;" + String.valueOf(this.merchantId) + "\n");
		}

		if (this.merchantTransactionId != null && !this.merchantTransactionId.isEmpty()) {
			calc.addItem("merchantTransactionId", this.merchantTransactionId);
			log.debug("merchantTransactionId added " + this.merchantTransactionId + ";\n");
		}

		if (this.prohibitedForMinors != null && !prohibitedForMinors.isEmpty()) {
			calc.addItem("prohibitedForMinors", this.prohibitedForMinors);
			log.debug("prohibitedForMinors added " + this.prohibitedForMinors + ";\n");
		}

		if (this.subMerchantId != null && !this.subMerchantId.isEmpty()) {
			calc.addItem("subMerchantId", this.subMerchantId);
			log.debug("subMerchantId added " + this.subMerchantId + ";\n");
		}

		if (this.test != null && !this.test.isEmpty()) {
			calc.addItem("test", this.test);
			log.debug("test added " + this.test + ";\n");
		}

		if (this.urlCallback != null && !this.urlCallback.isEmpty()) {
			calc.addItem("urlCallback", this.urlCallback);
			log.debug("urlCallback added " + this.urlCallback + ";\n");
		}

		if (this.urlKo != null && !this.urlKo.isEmpty()) {
			calc.addItem("urlKo", this.urlKo);
			log.debug("urlKo added" + this.urlKo + ";\n");
		}

		if (this.urlOk != null && !this.urlOk.isEmpty()) {
			calc.addItem("urlOk", this.urlOk);
			log.debug("urlOk added :" + this.urlOk + '\n');
		}

		if (this.urlPending != null && !this.urlPending.isEmpty()) {
			calc.addItem("urlPending", this.urlPending);
			log.debug("urlPending added" + this.urlPending + ";\n");
		}

		if (this.userCity != null && !this.userCity.isEmpty()) {
			calc.addItem("userCity", this.userCity.toLowerCase());
			log.debug("userCity added;" + this.userCity.toLowerCase() + "\n");
		}

		if (this.userCountry != null && this.userCountry.isEmpty()) {
			calc.addItem("userCountry", this.userCountry);
			log.debug("userCountry added:" + this.userCountry + ";\n");
		}

		if (this.userDateOfBirth != null && this.userDateOfBirth.isEmpty()) {
			calc.addItem("userDateOfBirth", this.userDateOfBirth);
			log.debug("userDateOfBirth added:" + this.userDateOfBirth + ";\n");
		}

		if (this.userEmail != null && !this.userEmail.isEmpty()) {
			calc.addItem("userEmail", this.userEmail);
			log.debug("userEmail added:" + this.userEmail + "\n");
		}

		if (this.userFirstName != null && !this.userFirstName.isEmpty()) {
			calc.addItem("userFirstName", this.userFirstName);
			log.debug("userFirstName added:" + this.userFirstName + "\n");
		}
		
		if (this.userGender != null && !this.userGender.isEmpty()) {
			calc.addItem("userGender", this.userGender);
			log.debug("userGender added:" + this.userGender + "\n");
		}
		if (this.userIp != null && !this.userIp.isEmpty()) {
			calc.addItem("userIp", this.userIp);
			log.debug("userIp added:" + this.userIp + "\n");
		}
		if (this.userKyc != null && !this.userKyc.isEmpty()) {
			calc.addItem("userKyc", this.userKyc);
			log.debug("userKyc added :" + this.userKyc + "\n");
		}
		if (this.userLastName != null && !this.userLastName.isEmpty()) {
			calc.addItem("userLastName", this.userLastName);
			log.debug("userLastName added " + this.userLastName + ";\n");
		}
		if (this.userLongitude != null && !this.userLongitude.isEmpty()) {
			calc.addItem("userLongitude", this.userLongitude);
			log.debug("userLongitude added" + this.userLongitude + ";\n");
		}
		if (this.userStreet != null && !this.userStreet.isEmpty()) {
			calc.addItem("userStreet", this.userStreet);
			log.debug("userStreet added" + this.userStreet + ";\n");
		}
		if (this.userTelephoneNumber != null && !this.userTelephoneNumber.isEmpty()) {
			calc.addItem("userTelephoneNumber", this.userTelephoneNumber);
			log.debug("userTelephoneNumber added " + this.userTelephoneNumber + ";\n");
		}
		if (this.userZipCode != null && !this.userZipCode.isEmpty()) {
			calc.addItem("userZipCode", this.userZipCode);
			log.debug("  userZipCode added " + this.userZipCode + "\n");
		}
		if (this.version != null && !this.version.isEmpty()) {
			calc.addItem("version", this.version);
			log.debug("version added " + this.version + "\n");
		}
		return calc.calculateHash();
	}
}