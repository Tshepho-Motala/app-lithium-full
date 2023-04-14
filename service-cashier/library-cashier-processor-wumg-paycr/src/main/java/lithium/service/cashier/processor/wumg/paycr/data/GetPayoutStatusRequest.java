package lithium.service.cashier.processor.wumg.paycr.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class GetPayoutStatusRequest {

	private String transactionId;
	private String companyId;
	private String userName;
	private String password;
	
}
