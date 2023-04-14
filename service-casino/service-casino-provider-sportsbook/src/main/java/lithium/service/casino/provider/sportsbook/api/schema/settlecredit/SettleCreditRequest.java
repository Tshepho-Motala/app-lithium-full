package lithium.service.casino.provider.sportsbook.api.schema.settlecredit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SettleCreditRequest {
    String betId;
    String guid;
    Long requestId;
    Double amount;
    Long timestamp;
    String sha256;
}
