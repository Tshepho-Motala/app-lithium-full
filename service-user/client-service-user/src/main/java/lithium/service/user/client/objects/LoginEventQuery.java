package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginEventQuery {
    private String userGuid;
    private Date startDate;
    private Date endDate;
    private Integer size;
    private Integer page;
}
