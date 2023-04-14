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
public class TransferRequest {
	/**
	 * Required.
	 * The required action. In the second step, this is ‘transfer’.
	 */
	@FormParam(value="action")
	private String action;
	/**
	 * Required.
	 * Session identifier returned in response to the prepare request.
	 */
	@FormParam(value="sid")
	private String sid;
}
