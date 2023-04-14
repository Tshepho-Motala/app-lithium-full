package lithium.service.cashier.config.upo.migration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserPaymentOptionsMigration {
    private int dlqRetries;
    private ProcessorAccountTypeMapping processorAccountTypeMapping;
}
