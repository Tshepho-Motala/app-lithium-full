package lithium.service.kyc.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KycAttemptsCheckResponse {
    private boolean isMethodUsed;
}
