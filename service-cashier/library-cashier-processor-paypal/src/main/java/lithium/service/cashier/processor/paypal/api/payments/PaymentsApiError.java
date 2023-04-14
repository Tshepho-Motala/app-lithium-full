package lithium.service.cashier.processor.paypal.api.payments;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentsApiError {
    private String name;
    private String message;
    @JsonProperty("debug_id")
    private String debugId;
    @JsonProperty("information_link")
    private String informationLink;
    private List<Details> details;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Details {
        private String field;
        private String location;
        private String issue;
    }
}
