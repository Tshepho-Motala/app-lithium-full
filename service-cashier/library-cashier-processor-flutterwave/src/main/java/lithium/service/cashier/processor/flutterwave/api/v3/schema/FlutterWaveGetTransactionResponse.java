package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class FlutterWaveGetTransactionResponse {

    String status;
    String message;
    FlutterWaveGetTransactionResponseData data;
}
