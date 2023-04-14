package lithium.service.limit.client.objects;

import lombok.Data;
import lombok.Builder;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PlayerTimeSlotLimitResponse {

    private String fromTimestampUTC;

    private String toTimestampUTC;

    private String fromTimestampWithZone = null;

    private String toTimestampWithZone = null;

}
