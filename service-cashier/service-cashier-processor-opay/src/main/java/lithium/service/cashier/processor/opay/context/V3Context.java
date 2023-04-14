package lithium.service.cashier.processor.opay.context;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode
@ToString(exclude = {"publicKey"})
@Builder
public class V3Context {
    private String url;
    private String publicKey;
    private String merchantId;
}
