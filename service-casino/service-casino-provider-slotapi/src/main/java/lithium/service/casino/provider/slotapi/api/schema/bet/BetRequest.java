
package lithium.service.casino.provider.slotapi.api.schema.bet;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BetRequest {
    private String roundId;
    private String betTransactionId;
    private String gameId;
    private BetRequestKindEnum kind;
    private Double amount;
    private String currencyCode;
    private long transactionTimestamp;
    private int sequenceNumber;
    private String sha256;
}
