package lithium.service.cashier.processor.quickbit.data.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
public enum StatusCode {
	I000(000, "No corresponding error code"),
	I200(200, "Transaction Completed Successfully."),
	I201(201, "Transaction Request Accepted. You may now redirect the user to the url provided in the redirect_url key."),
	I203(203, "User has redirected successfully."),
	I210(210, "Your transaction has been marked as disputed."),
	I307(307, "User has been redirected to the \"buy bitcoins\" page."),
	I308(308, "User has been redirected to the \"enter wallet address\" page."),
	I309(309, "User has been redirected to the \"enter card information\" page."),
	I400(400, "[parameter_name] parameter or value is missing. Please provide all required fields."),
	I401(401, "Request source is not authentic. Checksum value is not correct."),
	I402(402, "The value [value] for the parameter [parameter_name] isn't in the correct format."),
	I404(404, "No affiliate found with [referral_code] referral code. Please provide a valid referral code."),
	I408(408, "Transaction timeout."),
	I411(411, "The [parameter_name] parameter value exceeds its maximum character limit. The maximum character limit for this parameter is [limit]."),
	I412(412, "The parameter [parameter_name] length is less than the required min length. The minimum character limit for this parameter is [limit]."),
	I417(417, "The fiat currency [currency_value] for the parameter [fiat currency] isn’t in the correct format. Supported currencies are listed in the Others section of this document."),
	I418(418, "The user has tried to manipulate wallet address during the transaction."),
	I419(419, "The crypto currency [currency_value] for the parameter [crypto currency] isn’t in the correct format. Supported currencies are listed in the Others section of this document."),
	I422(422, "User is trying create another order against same api request."),
	I423(423, "Request reference must be unique."),
	I424(424, "Your Celoxo merchant account is not connected with your QB direct account."),
	I425(425, "Enter fiat amount or crypto amount must be entered."),
	I500(500, "Sorry, we couldn't process your request at this moment. Please try again later.");
	
	StatusCode(Integer code, String description) {
		this.code = code;
		this.description = description;
	}
	
	@Getter
	private Integer code;
	@Getter
	private String description;
	
	public static StatusCode find(final Integer code) {
		for (StatusCode sc: StatusCode.values()) {
			if (sc.getCode().equals(code)) return sc;
		}
		return I000;
	}
}