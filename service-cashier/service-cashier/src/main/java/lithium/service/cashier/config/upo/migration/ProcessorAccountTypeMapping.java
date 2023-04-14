package lithium.service.cashier.config.upo.migration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProcessorAccountTypeMapping {
    private DomainMethodProcessorMapping card;
    private DomainMethodProcessorMapping historic;
    private DomainMethodProcessorMapping paypal;
}
