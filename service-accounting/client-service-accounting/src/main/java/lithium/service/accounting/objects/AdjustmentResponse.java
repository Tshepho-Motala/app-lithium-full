package lithium.service.accounting.objects;

import java.util.ArrayList;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
public class AdjustmentResponse {

    private ArrayList<AdjustmentTransaction> adjustments = new ArrayList<>();

    public void add(AdjustmentTransaction transaction) {
        adjustments.add(transaction);
    }

}
