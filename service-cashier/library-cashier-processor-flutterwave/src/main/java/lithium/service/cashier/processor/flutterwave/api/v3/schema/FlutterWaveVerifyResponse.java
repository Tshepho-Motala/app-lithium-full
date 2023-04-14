package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class FlutterWaveVerifyResponse {
    private String status;
    private String message;
    private FlutterWaveVerifyResponseData data;
}
