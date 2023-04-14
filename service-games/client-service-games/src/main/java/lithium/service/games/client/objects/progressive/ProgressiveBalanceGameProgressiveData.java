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
public class ProgressiveBalanceGameProgressiveData implements Serializable {
    private String progressiveId;
    private List<ProgressiveBalanceGameProgressiveAmountData> progressiveBalanceGameProgressiveAmountDataList;

}
