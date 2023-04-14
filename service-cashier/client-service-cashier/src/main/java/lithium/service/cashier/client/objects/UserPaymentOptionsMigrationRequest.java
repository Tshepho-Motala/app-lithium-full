package lithium.service.cashier.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserPaymentOptionsMigrationRequest {
    private String domainName;
    private String userGuid;
    private String methodCode;
    private String processorCode;
    private String userTokenId;
}
