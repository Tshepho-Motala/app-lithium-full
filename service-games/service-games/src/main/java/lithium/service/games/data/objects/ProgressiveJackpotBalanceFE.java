package lithium.service.games.data.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.casino.client.objects.GameSupplier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressiveJackpotBalanceFE implements Serializable {
    private String progressiveId;
    private String currencyCode;
    private BigDecimal amount;
    @JsonInclude(JsonInclude.Include.NON_NULL) // FIXME: Consider FE here. They may have an API that requires this property to be present, even if null.
    private BigDecimal wonByAmount;
}
