package lithium.service.cashier.client.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.ToString;


@ToString
@AllArgsConstructor()
public enum TransactionRemarkType {
    OPERATOR("OPERATOR"),
    ACCOUNT_DATA("ACCOUNT_DATA"),
    ADDITIONAL_INFO("ADDITIONAL_INFO");

    private String name;

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static TransactionRemarkType fromName(String name) {
        for (TransactionRemarkType t: TransactionRemarkType.values()) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }
}
