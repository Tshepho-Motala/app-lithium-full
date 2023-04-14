package lithium.service.cashier.processor.paypal.api.payouts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class PayoutsResponse {

    @JsonProperty("batch_header")
    private BatchHeader batchHeader;

    private List<Item> items;

}
