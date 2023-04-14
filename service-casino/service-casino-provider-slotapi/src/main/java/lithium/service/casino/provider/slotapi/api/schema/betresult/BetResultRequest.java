package lithium.service.casino.provider.slotapi.api.schema.betresult;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
public class BetResultRequest {
    private String betResultTransactionId;
    private String roundId;
    private BetResultRequestKindEnum kind;
    private Double returns;
    private String currencyCode;
    private boolean roundComplete;
    private long transactionTimestamp;
    private int sequenceNumber;
    private String sha256;
}
