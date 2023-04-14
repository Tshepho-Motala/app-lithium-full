package lithium.service.cashier.processor.btc.globalbitlocker.data;

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
public class GetStatusRequest {
	public static final String API = "updates";
	@FormParam("key")
	private String apiKey;
	private String id;
}