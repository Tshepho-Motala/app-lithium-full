package lithium.service.user.client.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserVerificationStatusUpdate {
    private Long userId;
    private Long statusId;
    private String comment;
    private String userGuid;
    private Boolean ageVerified;
    private Boolean addressVerified;
    private Boolean sendSms = true;
    private String authorName;
}
