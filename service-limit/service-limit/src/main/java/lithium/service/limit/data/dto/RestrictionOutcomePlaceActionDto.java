package lithium.service.limit.data.dto;

import lithium.service.cashier.client.internal.TransactionProcessingCode;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class RestrictionOutcomePlaceActionDto {

    private Long id;
    private DomainRestrictionSetDto set;
    private TransactionProcessingCode code;
}
