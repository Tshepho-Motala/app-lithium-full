package lithium.service.cashier.processor.nuvei.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Nuvei3DNotificationData {
    private String threeDSServerTransID;
    private String acsTransID;
    private String messageType;
    private String messageVersion;
    private String transStatus;
    //ignore messageExtension
    private String acsSignedContent;
}
