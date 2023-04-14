package lithium.service.casino.provider.roxor.api.schema.checktransfer;

import lithium.service.casino.provider.roxor.api.schema.SuccessStatus;
import lithium.service.casino.provider.roxor.api.schema.Transfer;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class CheckTransferResponse {
    private SuccessStatus status;
    private Transfer transfer;
}
