package lithium.service.cashier.processor.hexopay.api.gateway.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThreeDSecureVerification {
    private String status;
    private String message;
    @JsonProperty("ve_status")
    private String veStatus;
    @JsonProperty("acs_url")
    private String acsUrl;
    @JsonProperty("pa_req")
    private String paReq;
    private String md;
    @JsonProperty("pa_res_url")
    private String paResUrl;
    private String eci;
    @JsonProperty("pa_status")
    private String paStatus;
    private String xid;
    private String cavv;
    @JsonProperty("cavv_algorithm")
    private String cavvAlgorithm;
    @JsonProperty("fail_reason")
    private String failReason;
}
