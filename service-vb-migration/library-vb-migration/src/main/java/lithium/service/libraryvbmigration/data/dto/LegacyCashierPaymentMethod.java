package lithium.service.libraryvbmigration.data.dto;

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
public class LegacyCashierPaymentMethod {
    private String domainName;
    private String paymentMethodName;
    private String paymentProviderName;
}
