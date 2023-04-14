package lithium.service.casino.provider.roxor.api.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Accrual {
    private String currency;
    private Long amount;
}
