package lithium.service.user.client.objects;

import lithium.exceptions.NotRetryableErrorCodeException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AllowedDeposit {
    private Boolean allowed;
    private BigDecimal amount;
    private NotRetryableErrorCodeException exception;
}
