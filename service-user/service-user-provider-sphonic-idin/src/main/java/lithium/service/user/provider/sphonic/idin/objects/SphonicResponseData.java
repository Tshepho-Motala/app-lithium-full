package lithium.service.user.provider.sphonic.idin.objects;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SphonicResponseData {

    private LocalDateTime responseDateTime;
    private String livescoreRequestId;
    private String livescoreAppliantId;
    private String sphonicTranscationId;
    private String bluemTransactionId;
    private String identURL;
}
