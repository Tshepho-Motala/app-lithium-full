package lithium.service.casino.provider.iforium.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lithium.service.casino.provider.iforium.constant.CharacterPatterns;
import lithium.service.casino.provider.iforium.util.BigDecimalSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Balance {

    @NotNull
    @Pattern(regexp = CharacterPatterns.CURRENCY_PATTERN)
    @JsonProperty("CurrencyCode")
    private String currencyCode;

    @NotEmpty
    @JsonProperty("CashFunds")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal cashFunds;

    @JsonProperty("BonusFunds")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal bonusFunds;

    @JsonProperty("FundsPriority")
    private String fundsPriority;

    @JsonProperty("Version")
    private Integer version;
}
