package lithium.service.cashier.mock.neteller.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginForm {
	private String fallBack;
	private String requestId;
	private String callbackPrefix;
	private String consentRemovalPrefix;
	@Builder.Default
	private String username = "0825623882";
	@Builder.Default
	private String email = "vipps@riaan.playsafesa.com";
	@Builder.Default
	private String firstname = "Riaan";
	@Builder.Default
	private String lastname = "Schoeman";
	@Builder.Default
	private String dateOfBirth = "1980-08-31";
	@Builder.Default
	private String mobile = "0825623882";
}