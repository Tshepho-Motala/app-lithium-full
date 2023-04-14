package lithium.service.cashier.processor.hexopay.api.page.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Checkout {
    private String token;
    @JsonProperty("redirect_url")
    private String redirectUrl;
}
