package lithium.service.notifications.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserInboxQueryParams {
    private String channels;
    private String locale;
    private Boolean cta;
    private String type;
    private String userGuid;
    private boolean read;

}
