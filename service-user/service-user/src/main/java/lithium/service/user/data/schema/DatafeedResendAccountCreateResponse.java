package lithium.service.user.data.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.service.user.data.entities.User;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class DatafeedResendAccountCreateResponse {
    private String guid;
    private User user;
    private String error;
}
