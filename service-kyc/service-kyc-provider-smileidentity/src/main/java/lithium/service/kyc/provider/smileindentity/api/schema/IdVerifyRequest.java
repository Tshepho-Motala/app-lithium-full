package lithium.service.kyc.provider.smileindentity.api.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdVerifyRequest {
    @JsonProperty("partner_id")
    private String partnerId;
    private Long timestamp;
    @JsonProperty("sec_key")
    private String secKey;
    private String country;
    @JsonProperty("id_type")
    private String idType;
    @JsonProperty("id_number")
    private String idNumber;
    @JsonProperty("bank_code")
    private String bankCode;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("middle_name")
    private String middleName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("phone_number")
    private String phoneNumber;
    private String dob;
    @JsonProperty("partner_params")
    private PartnerParams partnerParams;


}
