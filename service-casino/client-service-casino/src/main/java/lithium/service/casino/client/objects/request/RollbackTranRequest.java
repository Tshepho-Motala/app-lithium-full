package lithium.service.casino.client.objects.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RollbackTranRequest extends Request {
	private String userGuid;
	private String transactionId;
	private String currencyCode;
}