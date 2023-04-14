package lithium.service.cashier.processor.hexopay.api.gateway.data.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum TransactionType {
    PAYMENT("payment"),
    AUTHORIZATION("authorization"),
    PAYOUT("payout"),
    CAPTURE("capture"),
    VOID("void");

    @Accessors(fluent=true)
    String type;

    TransactionType(String type) {
        this.type = type;
    }

    @JsonValue
    public String type() {
        return type;
    }

    @JsonCreator
    public static TransactionType fromName(String name) {
        for (TransactionType t: TransactionType.values()) {
            if (t.type.equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }
}
