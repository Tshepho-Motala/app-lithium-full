package lithium.service.cashier.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
public class BulkResult {
    private List<Long> proceedIds;
    private List<Long> failedIds;
}
