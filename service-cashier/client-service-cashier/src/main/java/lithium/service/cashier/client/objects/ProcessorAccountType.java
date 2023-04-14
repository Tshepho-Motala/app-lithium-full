package lithium.service.cashier.client.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
public enum ProcessorAccountType {
    CARD("CARD"),
    BANK("BANK"),
    USSD("USSD"),
    PAYPAL("PayPal"),
    HISTORIC("Historic");

    private String name;

    ProcessorAccountType(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return this.name;
    }

    @JsonCreator
    public static ProcessorAccountType fromName(String name) {
        for (ProcessorAccountType t: ProcessorAccountType.values()) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }
}
