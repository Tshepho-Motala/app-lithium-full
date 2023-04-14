package lithium.service.document.provider.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FKScores {
    @JsonProperty("social_fraud")
    private Map<String, BigDecimal> socialFraud;

    @JsonProperty("id_score")
    private BigDecimal idScore;

    @JsonProperty("id_confidence")
    private Map<String, BigDecimal> idConfidence;

    private Integer over18;

}
