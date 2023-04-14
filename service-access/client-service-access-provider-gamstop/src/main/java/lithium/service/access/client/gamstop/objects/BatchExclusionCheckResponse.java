package lithium.service.access.client.gamstop.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchExclusionCheckResponse {
    private Long reportId;
    private Long reportRunId;
    private String status;
    private List<ExclusionResultLine> responseData;
}
