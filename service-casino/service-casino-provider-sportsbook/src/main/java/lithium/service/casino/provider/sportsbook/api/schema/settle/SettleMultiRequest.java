package lithium.service.casino.provider.sportsbook.api.schema.settle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SettleMultiRequest {
    String guid;
    Long requestId;
    Long timestamp;
    String sha256;
    ArrayList<SettleRequest> settleRequests;
}
