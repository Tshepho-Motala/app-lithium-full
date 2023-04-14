package lithium.service.cashier.client.internal;

import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VerifyProcessorAccountRequest {
    private String userGuid;
    private boolean update;
    private ProcessorAccount processorAccount;
    private List<ProcessorAccountVerificationType> verifications;
}
