package lithium.service.casino.provider.iforium.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.casino.provider.iforium.constant.CharacterPatterns;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AlertWalletCallbackNotificationRequest extends Request {

    @JsonProperty("GatewaySessionToken")
    @Size(max = 100)
    private String gatewaySessionToken;

    @JsonProperty("OperatorAccountID")
    @Size(max = 50)
    @Pattern(regexp = CharacterPatterns.OPERATOR_ACCOUNT_ID_PATTERN)
    private String operatorAccountId;

    @JsonProperty("Source")
    private String source;

    @JsonProperty("AlertActionID")
    @Size(max = 50)
    private String alertActionId;

    @JsonProperty("OperatorAlertActionReference")
    @Size(max = 50)
    private String operatorAlertActionReference;

    @JsonProperty("OperatorAlertReference")
    @Size(max = 50)
    private String operatorAlertReference;

    @JsonProperty("GamingRegulatorCode")
    @Size(max = 50)
    private String gamingRegulatorCode;

    @JsonProperty("Type")
    @Size(max = 50)
    private String type;

    @JsonProperty("Method")
    @Size(max = 50)
    private String method;

    @JsonProperty("Data")
    @Size(max = 50)
    private String data;

    @Builder
    public AlertWalletCallbackNotificationRequest(String platformKey, String sequence, Date timestamp,
                                                  String gatewaySessionToken, String operatorAccountId, String source,
                                                  String alertActionId, String operatorAlertActionReference,
                                                  String operatorAlertReference, String gamingRegulatorCode, String type,
                                                  String method, String data) {
        super(platformKey, sequence, timestamp);
        this.gatewaySessionToken = gatewaySessionToken;
        this.operatorAccountId = operatorAccountId;
        this.source = source;
        this.alertActionId = alertActionId;
        this.operatorAlertActionReference = operatorAlertActionReference;
        this.operatorAlertReference = operatorAlertReference;
        this.gamingRegulatorCode = gamingRegulatorCode;
        this.type = type;
        this.method = method;
        this.data = data;
    }
}
