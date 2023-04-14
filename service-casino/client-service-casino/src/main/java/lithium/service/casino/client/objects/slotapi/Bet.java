package lithium.service.casino.client.objects.slotapi;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Bet {
    private Long id;
    int version;
    private long createdDate;
    private long modifiedDate;
    private BetRound betRound;
    private String betTransactionId;
    private Date transactionTimestamp;
    private BetRequestKind kind;
    private double amount;
    private double balanceAfter;
    private Currency currency;
    private Long lithiumAccountingId;
}
