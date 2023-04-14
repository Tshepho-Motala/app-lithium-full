package lithium.service.user.threshold.client.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThresholdsFilterRequest {
    private Date startDateTime;
    private Date endDateTime;
    private String[] domains;
    private String typeName;
    private String playerGuid;
    private String granularity;
}
