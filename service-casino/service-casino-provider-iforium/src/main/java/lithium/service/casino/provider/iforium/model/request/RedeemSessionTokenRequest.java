package lithium.service.casino.provider.iforium.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.casino.provider.iforium.constant.CharacterPatterns;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RedeemSessionTokenRequest extends Request {

    @NotNull
    @Size(max = 50)
    @Pattern(regexp = CharacterPatterns.SESSION_TOKEN_PATTERN)
    @JsonProperty("SessionToken")
    private String sessionToken;

    @NotEmpty
    @JsonProperty("IPAddress")
    private String iPAddress;

    @Builder
    public RedeemSessionTokenRequest(String platformKey, String sequence, Date timestamp, String sessionToken, String iPAddress) {
        super(platformKey, sequence, timestamp);
        this.sessionToken = sessionToken;
        this.iPAddress = iPAddress;
    }
}
