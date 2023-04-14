package lithium.service.kyc.provider.onfido.objects;

import lithium.service.kyc.provider.onfido.entitites.CheckStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusResponse {
    private String status;
    
    public static StatusResponse mapStatus(CheckStatus checkStatus) {
        switch (checkStatus) {
            case INITIATED:
            case PROCESSING:
                return new StatusResponse("pending");
            case COMPLETE:
                return new StatusResponse("success");
            case FAIL:
                return new StatusResponse("fail");
        }
        return new StatusResponse();
    }
}
