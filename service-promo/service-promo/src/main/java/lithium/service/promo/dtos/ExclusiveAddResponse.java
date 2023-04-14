package lithium.service.promo.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExclusiveAddResponse {
    private boolean success;
    private int validPlayerCount;
    private int invalidPlayerCount;
}
