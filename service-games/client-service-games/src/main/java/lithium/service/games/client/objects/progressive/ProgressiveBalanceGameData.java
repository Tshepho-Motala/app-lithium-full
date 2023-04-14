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
public class ProgressiveBalanceGameData implements Serializable {
    private String gameGuid;
    private List<ProgressiveBalanceGameProgressiveData> progressiveBalanceGameProgressiveDataList;

}
