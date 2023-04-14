package lithium.service.user.client.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class ReCaptchaApiResponse {
    private boolean success;
    private double score;
    private String action;
    @JsonProperty("challenge_ts")
    private Timestamp challengeTs;
    private String hostname;
    @JsonProperty("error-codes")
    private List<String> errorCodes;
}
