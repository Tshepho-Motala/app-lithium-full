package lithium.service.casino.client.objects.response;

import java.util.List;
import lithium.service.casino.client.objects.CasinoBetHistoryCsv;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CasinoBetHistoryCsvResponse {
    private int totalPages;
    private long totalElements;
    private List<CasinoBetHistoryCsv> casinoBetHistoryCsvList;
}
