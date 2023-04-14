package lithium.service.games.client.objects.progressive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressiveBalanceData implements Serializable {
    private String domain;
    private String provider;
    private List<ProgressiveBalanceGameData> progressiveBalanceGameDataList;

}
