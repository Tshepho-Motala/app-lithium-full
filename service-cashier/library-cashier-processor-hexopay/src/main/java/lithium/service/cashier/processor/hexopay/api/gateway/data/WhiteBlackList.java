package lithium.service.cashier.processor.hexopay.api.gateway.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhiteBlackList {
    @Builder.Default
    private String email = "absent";
    @Builder.Default
    private String ip = "absent";
    @Builder.Default
    private String card_number = "absent";
}
