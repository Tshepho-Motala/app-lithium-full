package lithium.service.casino.provider.iforium.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.casino.provider.iforium.constant.CharacterPatterns;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateSessionTokenRequest extends Request {

    @NotNull
    @Size(max = 50)
    @JsonProperty("OperatorAccountID")
    @Pattern(regexp = CharacterPatterns.OPERATOR_ACCOUNT_ID_PATTERN)
    private String operatorAccountId;

    @NotEmpty
    @Size(max = 50)
    @JsonProperty("GameID")
    private String gameId;

    @Builder
    public CreateSessionTokenRequest(String platformKey, String sequence, Date timestamp, String operatorAccountId, String gameId) {
        super(platformKey, sequence, timestamp);
        this.operatorAccountId = operatorAccountId;
        this.gameId = gameId;
    }
}
