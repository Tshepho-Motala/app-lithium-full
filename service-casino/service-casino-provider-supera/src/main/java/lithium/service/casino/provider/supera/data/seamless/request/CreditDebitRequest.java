package lithium.service.casino.provider.supera.data.seamless.request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class CreditDebitRequest {
	private String actionType;
	private Integer remoteId;
	private BigDecimal amount;
	private Integer gameId;
	private Integer transactionId;
	private Integer roundId;
	private String remoteData;
	private String sessionId;
}