package lithium.service.cashier.processor.neteller.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown=true)
public class GatewayResponse {
	private String orderId;
	private Long totalAmount;
	private String currency;
	private String status;
	private String lang;
}
