package lithium.service.cashier.processor.paystack.api.schema;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackUSSDVerifyResponse {
    private boolean status;
    private PaystackUSSDData data;

}
