package lithium.service.casino.client.objects.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceRequest extends Request {
	private String userGuid;
	private String currencyCode;
}