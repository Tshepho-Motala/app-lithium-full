package lithium.service.cashier.processor.cc.ecardon.data;

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
public class DebitRequest {
	
	@FormParam("authentication.userId")
	private String authenticationUserId;
	@FormParam("authentication.password")
	private String authenticationPassword;
	@FormParam("authentication.entityId")
	private String authenticationEntityId;
	
	private String merchantTransactionId;
	private String amount;
	private String currency;
	private String paymentBrand;
	private String paymentType;
	@FormParam("card.number")
	private String cardNumber;
	@FormParam("card.holder")
	private String cardHolder;
	@FormParam("card.expiryMonth")
	private String cardExpiryMonth;
	@FormParam("card.expiryYear")
	private String cardExpiryYear;
	@FormParam("card.cvv")
	private String cardSecurityCode;
	
	@FormParam("customer.merchantCustomerId")
	private String customerMerchantCustomerId;
	@FormParam("customer.givenName")
	private String customerGivenName;
	@FormParam("customer.surname")
	private String customerSurname;
	@FormParam("customer.birthDate")
	private String customerBirthDate;
	@FormParam("customer.phone")
	private String customerPhone;
	@FormParam("customer.mobile")
	private String customerMobile;
	@FormParam("customer.email")
	private String customerEmail;
	@FormParam("customer.ip")
	private String customerIp;
//	@FormParam("customer.status")
//	private String customerStatus;
	
	@FormParam("billing.street1") //The door number, floor, building number, building name, and/or street name of the billing address	AN100
	private String billingStreet1;
	@FormParam("billing.street2") //The adjoining road or locality (if required) of the billing address	AN100
	private String billingStreet2;
	@FormParam("billing.city") //The town, district or city of the billing address	AN80
	private String billingCity;
	@FormParam("billing.state") //The county, state or region of the billing address	AN50
	private String billingState;
	@FormParam("billing.postcode") //The postal code or zip code of the billing address	AN30
	private String billingPostCode;
	@FormParam("billing.country") //The country of the billing address (ISO 3166-1)	A2
	private String billingCountry;
	
	private String shopperResultUrl;
	
	private String testMode;
}