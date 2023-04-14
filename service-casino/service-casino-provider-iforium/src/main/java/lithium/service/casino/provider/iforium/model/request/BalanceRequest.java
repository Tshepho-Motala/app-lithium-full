package lithium.service.casino.provider.iforium.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.casino.provider.iforium.constant.CharacterPatterns;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BalanceRequest extends Request {

    @NotNull
    @Size(max = 50)
    @JsonProperty("OperatorAccountID")
    @Pattern(regexp = CharacterPatterns.OPERATOR_ACCOUNT_ID_PATTERN)
    private String operatorAccountId;

    @Size(max = 50)
    @JsonProperty("GameID")
    private String gameId;

    @Size(max = 50)
    @JsonProperty("ContentGameProviderID")
    private String contentGameProviderId;

    @Builder
    public BalanceRequest(String platformKey, String sequence, Date timestamp, String operatorAccountId, String gameId,
                          String contentGameProviderId) {
        super(platformKey, sequence, timestamp);
        this.operatorAccountId = operatorAccountId;
        this.gameId = gameId;
        this.contentGameProviderId = contentGameProviderId;
    }
}
