package lithium.service.casino.provider.incentive.api.schema.settlement;

import lithium.service.casino.provider.incentive.api.schema.placement.request.PlacementRequestEvent;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
public class SettlementRequest {

    private String settlementTransactionId;
    private String betTransactionId;
    private String result;
    private double returns;
    private String currencyCode;
    private long transactionTimestamp;
    private ArrayList<SettlementRequestSelection> selections;
    private String sha256;
}
