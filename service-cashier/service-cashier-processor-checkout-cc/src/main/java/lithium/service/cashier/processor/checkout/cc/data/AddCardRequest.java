package lithium.service.cashier.processor.checkout.cc.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddCardRequest {
    @NotNull
    private String token;
    @NotNull
    private String returnUrl;
    private String nameOnCard;
}
