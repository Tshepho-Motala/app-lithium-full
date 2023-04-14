package lithium.service.cashier.processor.paynl.data.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paynl.data.Amount;
import lithium.service.cashier.processor.paynl.data.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayoutResponse {
    private Transaction transaction;
    private Amount amount;
    @JsonProperty("_links")
    private List<Links> links;
}
