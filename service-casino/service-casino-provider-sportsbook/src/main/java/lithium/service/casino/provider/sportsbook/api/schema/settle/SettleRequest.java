package lithium.service.casino.provider.sportsbook.api.schema.settle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SettleRequest {
    String betId;
    Double amount;
}
