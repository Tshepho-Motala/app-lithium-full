package lithium.service.games.client.objects.progressive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressiveBalanceSupplierGameData {
    private String supplier;

    private List<ProgressiveBalanceGameData> progressiveBalanceGameDataList;
}
