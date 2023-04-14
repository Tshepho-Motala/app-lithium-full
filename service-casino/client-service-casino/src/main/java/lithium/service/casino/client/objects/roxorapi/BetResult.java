package lithium.service.casino.client.objects.roxorapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BetResult {
    private Long id;
    int version;
    private long createdDate;
    private long modifiedDate;
    private String betResultTransactionId;
    private Date transactionTimestamp;
    private BetRound betRound;
    private BetResultKind betResultKind;
    private double returns;
    private double balanceAfter;
    private boolean roundComplete;
    private Currency currency;
    private Long lithiumAccountingId;
}

