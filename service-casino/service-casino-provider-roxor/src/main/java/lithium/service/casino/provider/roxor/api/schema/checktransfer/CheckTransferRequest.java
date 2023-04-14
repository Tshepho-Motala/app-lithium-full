package lithium.service.casino.provider.roxor.api.schema.checktransfer;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CheckTransferRequest {
    private String gamePlayId;
    private String transferId;
    private String gameKey;
    private String website;
}
