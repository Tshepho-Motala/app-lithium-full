package lithium.service.cashier.processor.flutterwave.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bank implements Comparable<Bank>{
    String code;
    String name;

    @Override
    public int compareTo(Bank o) {
        return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
    }
}
