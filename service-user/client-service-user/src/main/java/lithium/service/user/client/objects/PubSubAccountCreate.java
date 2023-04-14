package lithium.service.user.client.objects;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PubSubAccountCreate implements PubSubObj {
    private PubSubEventType eventType;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String firstName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String lastName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String DOB;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String domain;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String registrationDate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String userName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String cellphoneNumber;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accountId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String bonusCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isEmailValidated;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCellNumberValidated;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isAddressVerified;
}
