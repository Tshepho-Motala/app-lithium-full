package lithium.service.casino.pubsub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PubSubCasinoBetMessage  implements PubSubCasinoMessage {
    private String eventType;
    private String accountGuid;
    private Double value;
    private String product;
    private Long betId;
    private Long  createdDate;
    private String betKind;
}
