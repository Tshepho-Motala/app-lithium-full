package lithium.service.kyc.provider.paystack.data.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BvnResolveMeta {
    @JsonProperty("calls_this_month")
    private String callsThisMonth;

    @JsonProperty("free_calls_left")
    private String freeCallsLeft;

}
