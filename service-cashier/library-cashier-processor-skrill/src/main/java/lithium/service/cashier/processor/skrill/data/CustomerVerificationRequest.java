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
public class CustomerVerificationRequest {
	@FormParam(value="merchantId")
	private String merchantId;
	@FormParam(value="password")
	private String password;
	@FormParam(value="email")
	private String email;
}
