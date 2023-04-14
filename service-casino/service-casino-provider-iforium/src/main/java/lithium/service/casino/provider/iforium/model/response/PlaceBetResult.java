package lithium.service.casino.provider.iforium.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceBetResult {

    @JsonProperty("OperatorTransactionReference")
    private String operatorTransactionReference;

    @JsonProperty("OperatorTransactionSplit")
    private OperatorTransactionSplit operatorTransactionSplit;
}
