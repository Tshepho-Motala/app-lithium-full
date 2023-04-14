package lithium.service.cashier.processor.cc.trustspay.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QueryResponse {

	private String merNo;
	private String gatewayNo;
	private String orderNo;
	private String tradeNo;
	private String tradeDate;
	private String tradeAmount;
	private String tradeCurrency;
	private String sourceWebSite;
	private int queryResult;

}
