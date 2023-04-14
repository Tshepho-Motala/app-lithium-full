package lithium.service.accounting.objects;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentRequest {

    String domainName;

    @Builder.Default
    ArrayList<AdjustmentRequestComponent> adjustments = new ArrayList<>();

    public void add(AdjustmentRequestComponent adjustment) {
        adjustments.add(adjustment);
    }
}