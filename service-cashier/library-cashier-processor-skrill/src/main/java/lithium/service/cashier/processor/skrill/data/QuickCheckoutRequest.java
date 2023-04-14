package lithium.service.cashier.processor.skrill.data;

import lithium.util.FormParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuickCheckoutRequest {
	/**
	 * Required.
	 * Email address of your Skrill merchant account.
	 */
	@FormParam(value="pay_to_email")
	private String payToEmail;
	/**
	 * A description to be shown on the Skrill payment page in the logo area if there is no logo_url parameter. If no value is submitted and there is no
	 * logo, the pay_to_email value is shown as the recipient of the payment. (Max 30 characters)
	 */
	@FormParam(value="recipient_description")
	private String recipientDescription;
	/**
	 * Your unique reference or identification number for the transaction. (Must be unique for each payment)
	 */
	@FormParam(value="transaction_id")
	private String transactionId;
	/**
	 * URL to which the customer is returned once the payment is made. If this field is not filled, the Skrill Quick Checkout page closes automatically at the end of the
	 * transaction and the customer is returned to the page on your website from where they were redirected to Skrill. A secure return URL option is available.
	 */
	@FormParam(value="return_url")
	private String returnUrl;
	/**
	 * The text on the button when the customer finishes their payment.
	 */
	@FormParam(value="return_url_text")
	private String returnUrlText;
	/**
	 * Specifies a target in which the return_url value is displayed upon successful payment from the customer. Default value is 1.
	 * 1 = '_top'
	 * 2 = '_parent'
	 * 3 = '_self'
	 * 4= '_blank'
	 */
	@FormParam(value="return_url_target")
	private String returnUrlTarget;
	/**
	 * URL to which the customer is returned if the payment is cancelled or fails. If no cancel URL is provided the Cancel button is not displayed.
	 */
	@FormParam(value="cancel_url")
	private String cancelUrl;
	/**
	 * Specifies a target in which the cancel_url value is displayed upon cancellation of payment by the customer. Default value is 1.
	 * 1 = '_top'
	 * 2 = '_parent'
	 * 3 = '_self'
	 * 4= '_blank'
	 */
	@FormParam(value="cancel_url_target")
	private String cancelUrlTarget;
	/**
	 * URL to which the transaction details are posted after the payment process is complete. Alternatively, you may specify an email address where the results are sent.
	 * If the status_url is omitted, no transaction details are sent.
	 * See https://www.skrill.com/fileadmin/content/pdf/Skrill_Quick_Checkout_Guide_v7.10.pdf for port restrictions.
	 */
	@FormParam(value="status_url")
	private String statusUrl;
	/**
	 * Second URL to which the transaction details are posted after the payment process is complete. Alternatively, you may specify an email address where the results are sent.
	 * See https://www.skrill.com/fileadmin/content/pdf/Skrill_Quick_Checkout_Guide_v7.10.pdf for port restrictions.
	 */
	@FormParam(value="status_url2")
	private String statusUrl2;
	/**
	 * 2-letter code of the language used for Skrill’s pages.
	 */
	@FormParam(value="language")
	private String language;
	/**
	 * The URL of the logo which you would like to appear in the top right of the Skrill page. The logo must be accessible via HTTPS or it will not be shown.
	 * The logo will be resized to fit. To avoid scaling distortion, the minimum size should be as follows:
	 * • If the logo width > height - at least 107px width.
	 * • If logo width > height - at least 65px height
	 * Avoid large images (much greater than 256 by 256px) to minimise the page loading time.
	 */
	@FormParam(value="logo_url")
	private String logoUrl;
	/**
	 * Forces only the SID to be returned without the actual page. Useful when using the secure method to redirect the customer to Quick Checkout.
	 * Accepted values are 0 (default) and 1 (prepare only).
	 */
	@FormParam(value="prepare_only")
	private Integer prepareOnly;
	/**
	 * When a customer pays through Skrill, Skrill submits a preconfigured descriptor with the transaction, containing your business trading
	 * name/ brand name. The descriptor is typically displayed on the bank or credit card statement of the customer. For Klarna and Direct
	 * Debit payment methods, you can submit a dynamic_descriptor, which will override the default value stored by Skrill.
	 */
	@FormParam(value="dynamic_descriptor")
	private String dynamicDescriptor;
	/**
	 * This is an optional parameter containing the Session ID returned by the prepare_only call. If you use this parameter you should not
	 * supply any other parameters.
	 */
	@FormParam(value="sid")
	private String sid;
	/**
	 * You can pass a unique referral ID or email of an affiliate from which the customer is referred. The rid value must be included within the actual payment request.
	 */
	@FormParam(value="rid")
	private String rid;
	/**
	 * You can pass additional identifier in this field in order to track your affiliates. You must inform your account manager about the exact value that will be submitted so that
	 * affiliates can be tracked.
	 */
	@FormParam(value="ext_ref_id")
	private String extRefId;
	/**
	 * A comma-separated list of field names that are passed back to your web server when the payment is confirmed (maximum 5 fields).
	 */
	@FormParam(value="merchant_fields")
	private String merchantFields;
	@FormParam(value="Field1")
	private String field1;
	@FormParam(value="Field2")
	private String field2;
	@FormParam(value="Field3")
	private String field3;
	@FormParam(value="Field4")
	private String field4;
	@FormParam(value="Field5")
	private String field5;

	/**
	 * Email address of the customer who is making the payment. If provided, this field is hidden on the payment form. If left empty, the customer has
	 * to enter their email address.
	 */
	@FormParam(value="pay_from_email")
	private String payFromEmail;
	@FormParam(value="firstname")
	private String firstName;
	@FormParam(value="lastname")
	private String lastName;
	/**
	 * Date of birth of the customer. The format is ddmmyyyy. Only numeric values are accepted.
	 * If provided this field will be prefilled in the Payment form. This saves time for SEPA payments and Skrill Wallet sign-up which require the customer
	 * to enter a date of birth.
	 */
	@FormParam(value="date_of_birth")
	private String dateOfBirth;
	/**
	 * Customer’s address (for example: street)
	 */
	@FormParam(value="address")
	private String address;
	/**
	 * Customer’s address (for example: town)
	 */
	@FormParam(value="address2")
	private String address2;
	/**
	 * Customer’s phone number. Only numeric values are accepted
	 */
	@FormParam(value="phone_number")
	private String phoneNumber;
	/**
	 * Customer’s postal code/ZIP Code.
	 * Only alphanumeric values are accepted (for example:, no punctuation marks or dashes)
	 */
	@FormParam(value="postal_code")
	private String postalCode;
	/**
	 * Customer’s city or postal area
	 */
	@FormParam(value="city")
	private String city;
	/**
	 * Customer’s state or region
	 */
	@FormParam(value="state")
	private String state;
	/**
	 * Customer’s country in the 3-digit ISO Code
	 */
	@FormParam(value="country")
	private String country;
	/**
	 * Neteller customer account email or account ID
	 */
	@FormParam(value="neteller_id")
	private String netellerId;
	/**
	 * Secure ID or Google Authenticator One Time Password for the customer’s Neteller account
	 */
	@FormParam(value="neteller_secure_id")
	private String netellerSecureId;

	/**
	 * Required.
	 * The total amount payable.
	 * Note: Do not include the trailing zeroes if the amount is a natural number. For example: “23” (not “23.00”).
	 */
	@FormParam(value="amount")
	private String amount;
	/**
	 * Required.
	 * 3-letter code of the currency of the amount according to ISO 4217
	 */
	@FormParam(value="currency")
	private String currency;
	/**
	 * You can include a calculation for the total amount payable, which is displayed in the More information section in the header of the Skrill payment form.
	 * Note that Skrill does not check the validity of this data.
	 */
	@FormParam(value="amount2_description")
	private String amount2Description;
	/**
	 * This amount in the currency defined in the field 'currency' will be shown next to amount2_description.
	 */
	@FormParam(value="amount2")
	private String amount2;
	@FormParam(value="amount3_description")
	private String amount3Description;
	@FormParam(value="amount3")
	private String amount3;
	@FormParam(value="amount4_description")
	private String amount4Description;
	@FormParam(value="amount4")
	private String amount4;
	/**
	 * You can show up to five additional details about the product in the More information section in the header of Quick Checkout.
	 */
	@FormParam(value="detail1_description")
	private String detail1Description;
	@FormParam(value="detail1_text")
	private String detail1Text;
	@FormParam(value="detail2_description")
	private String detail2Description;
	@FormParam(value="detail2_text")
	private String detail2Text;
	@FormParam(value="detail3_description")
	private String detail3Description;
	@FormParam(value="detail3_text")
	private String detail3Text;
	@FormParam(value="detail4_description")
	private String detail4Description;
	@FormParam(value="detail4_text")
	private String detail4Text;
	@FormParam(value="detail5_description")
	private String detail5Description;
	@FormParam(value="detail5_text")
	private String detail5Text;

	@FormParam(value="payment_methods")
	private String paymentMethods;
}
