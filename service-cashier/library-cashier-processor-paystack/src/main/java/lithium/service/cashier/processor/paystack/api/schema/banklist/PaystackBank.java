package lithium.service.cashier.processor.paystack.api.schema.banklist;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaystackBank implements Comparable<PaystackBank>{
    String code;
    String name;

    @Override
    public int compareTo(PaystackBank o) {
        return this.getName().toLowerCase().compareTo(o.getName().toLowerCase());
    }
}
