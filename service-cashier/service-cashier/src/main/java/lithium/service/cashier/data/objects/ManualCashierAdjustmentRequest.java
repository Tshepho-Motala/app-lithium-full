package lithium.service.cashier.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ManualCashierAdjustmentRequest {
    private Long transactionId;
    private String accountCode;
    @NotBlank
    private String comment;
}
