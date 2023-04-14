package lithium.service.cashier.processor.ids.idebit.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.util.FormParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRedirectRequest {

	@JsonProperty(value="merchant_id", required = true)
	private String merchantId;
	@JsonProperty(value="merchant_sub_id")
	private String merchantSubId;
	@JsonProperty(value="merchant_user_id", required = true)
	private String userGuid;
	@JsonProperty(value="merchant_txn_num", required = true)
	private String transactionNumber;
	@FormParam("amount")
	@JsonProperty(value="txn_amount", required = true)
	private String amountDecimalString;
	@JsonProperty(value="txn_currency", required = true)
	private String currencyCode;
	@JsonProperty(value="first_name")
	private String firstName;
	@JsonProperty(value="middle_name")
	private String middleName;
	@JsonProperty(value="last_name")
	private String lastName;
	@JsonProperty(value="addr_1")
	private String addressLineOne;
	@JsonProperty(value="addr_2")
	private String addressLineTwo;
	@JsonProperty(value="city")
	private String city;
	@JsonProperty(value="state")
	private String state;
	@JsonProperty(value="zip")
	private String zip;
	@JsonProperty(value="country")
	private String country;
	@JsonProperty(value="hph_area_code")
	private String homePhoneAreaCode;
	@JsonProperty(value="hph_local_number")
	private String homePhoneLocalNumber;
	@JsonProperty(value="dob_day")
	private String dateOfBirthDay;
	@JsonProperty(value="dob_month")
	private String dateOfBirthMonth;
	@JsonProperty(value="dob_year")
	private String dateOfBirthYear;
	@JsonProperty(value="extra_field_1")
	private String additionalData;
	@JsonProperty(value="return_url")
	private String returnUrl;
	@JsonProperty(value="lang")
	private String languageCode;
}
