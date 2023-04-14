package lithium.service.cashier.processor.paysafegateway.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class ThreeDCard {
    private String status;
    private String cardBin;
    private String lastDigits;
    private CardExpiry cardExpiry;
    private String holderName;
    private ThreeDCardType cardType;
    private String cardCategory;
    private Authentication authentication;
}
