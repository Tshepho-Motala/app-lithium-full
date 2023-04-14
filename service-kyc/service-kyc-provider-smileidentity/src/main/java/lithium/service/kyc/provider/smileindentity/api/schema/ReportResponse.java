package lithium.service.kyc.provider.smileindentity.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportResponse {

    @JsonProperty("JSONVersion")
    private String jsonVersion;
    @JsonProperty("SmileJobID")
    private String smileJobID;
    @JsonProperty("PartnerParams")
    private PartnerParams partnerParams;
    @JsonProperty("ResultType")
    private String resultType;
    @JsonProperty("ResultText")
    private String resultText;
    @JsonProperty("ResultCode")
    private String resultCode;
    @JsonProperty("IsFinalResult")
    private Boolean isFinalResult;
    @JsonProperty("Actions")
    private Actions actions;
    @JsonProperty("Country")
    private String country;
    @JsonProperty("IDType")
    private String idType;
    @JsonProperty("IDNumber")
    private String idNumber;
    @JsonProperty("ExpirationDate")
    private String expirationDate;
    @JsonProperty("FullName")
    private String fullName;
    @JsonProperty("DOB")
    private String dob;
    @JsonProperty("Photo")
    private String photo;
    @JsonProperty("PhoneNumber")
    private String phoneNumber;
    @JsonProperty("Gender")
    private String gender;
    @JsonProperty("Address")
    private String address;
    @JsonProperty("IDNumberPreviouslyRegistered")
    private Boolean idNumberPreviouslyRegistered;
    @JsonProperty("UserIDsOfPreviousRegistrants")
    private List<String> userIDsOfPreviousRegistrants;
    @JsonProperty("FullData")
    private Map<String, Object> fullData;
    @JsonProperty("sec_key")
    private String secKey;
    private Object timestamp;
    private String error;
    private String code;
}
