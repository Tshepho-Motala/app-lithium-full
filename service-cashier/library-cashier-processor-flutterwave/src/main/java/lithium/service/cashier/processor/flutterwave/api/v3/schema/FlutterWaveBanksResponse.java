package lithium.service.cashier.processor.flutterwave.api.v3.schema;


import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class FlutterWaveBanksResponse {
    String status;
    String message;
    List<FlutterWaveBank> data;
}
