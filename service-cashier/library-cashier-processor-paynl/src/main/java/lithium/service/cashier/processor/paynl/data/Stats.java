package lithium.service.cashier.processor.paynl.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stats {
    private String info;
    private String tool;
    private String extra1;
    private String extra2;
    private String extra3;
    private String domainId;
}
