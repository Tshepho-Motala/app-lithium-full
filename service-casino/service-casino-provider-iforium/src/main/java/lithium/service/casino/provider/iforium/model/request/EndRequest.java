package lithium.service.casino.provider.iforium.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.casino.provider.iforium.constant.CharacterPatterns;
import lithium.service.casino.provider.iforium.model.validation.GatewaySessionTokenNotBlankValidation;
import lithium.service.casino.provider.iforium.model.validation.GatewaySessionTokenNotNullValidation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EndRequest extends Request {

    @JsonProperty("JackpotContribution")
    private BigDecimal jackpotContribution;

    @JsonProperty("JackpotWinnings")
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal jackpotWinnings;

    @JsonProperty("GatewaySessionToken")
    @NotEmpty(groups = GatewaySessionTokenNotNullValidation.class)
    @NotBlank(groups = GatewaySessionTokenNotBlankValidation.class)
    @Size(max = 100)
    private String gatewaySessionToken;

    @JsonProperty("OperatorAccountID")
    @NotNull
    @Size(max = 50)
    @Pattern(regexp = CharacterPatterns.OPERATOR_ACCOUNT_ID_PATTERN)
    private String operatorAccountId;

    @JsonProperty("GameRoundID")
    @NotEmpty
    @Size(max = 50)
    private String gameRoundId;

    @JsonProperty("GameID")
    @NotEmpty
    @Size(max = 50)
    private String gameId;

    @JsonProperty("CurrencyCode")
    @NotEmpty
    @Size(max = 3)
    private String currencyCode;

    @JsonProperty("ContentGameProviderID")
    @Size(max = 50)
    private String contentGameProviderId;

    @JsonProperty("FreeGameOfferCode")
    @Size(max = 100)
    private String freeGameOfferCode;
}
