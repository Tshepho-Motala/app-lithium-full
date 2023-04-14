package lithium.service.cashier.processor.flutterwave.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountVerificationResponse {
    private String status;
    private String message;
    @JsonProperty("data")
    private DataResponse data;
}
