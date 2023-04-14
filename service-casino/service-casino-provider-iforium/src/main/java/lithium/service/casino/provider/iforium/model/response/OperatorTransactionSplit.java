package lithium.service.casino.provider.iforium.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lithium.service.casino.provider.iforium.util.BigDecimalSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperatorTransactionSplit {

    @JsonProperty("BonusAmount")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal bonusAmount;

    @JsonProperty("CashAmount")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal cashAmount;
}
