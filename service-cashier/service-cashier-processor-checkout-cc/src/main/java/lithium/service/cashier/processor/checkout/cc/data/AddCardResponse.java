package lithium.service.cashier.processor.checkout.cc.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddCardResponse {
    private String status;
    private boolean iframeRedirect;
    private String iframeUrl;
}
