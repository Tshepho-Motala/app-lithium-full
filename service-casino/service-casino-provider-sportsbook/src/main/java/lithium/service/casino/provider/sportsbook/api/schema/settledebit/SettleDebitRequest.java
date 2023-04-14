package lithium.service.casino.provider.sportsbook.api.schema.settledebit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SettleDebitRequest {
    String guid;
    Long requestId;
    Double amount;
    Long timestamp;
    String sha256;
}
