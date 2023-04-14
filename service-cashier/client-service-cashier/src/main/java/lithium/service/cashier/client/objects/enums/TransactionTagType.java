package lithium.service.cashier.client.objects.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;


@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum TransactionTagType {

    /**
     * ! Cannot be called Starting with "NOT_", like "NOT_USED" or "NOT_FIRST_DEPOSIT". Prefix is reserved.
     */

    FIRST_DEPOSIT("FIRST_DEPOSIT", 1),
    FIRST_WITHDRAWAL("FIRST_WITHDRAWAL", 2),
    AUTO_APPROVED("AUTO_APPROVED", 3),
    WD_ON_BALANCE_LIMIT_RICHED("WD_ON_BALANCE_LIMIT_RICHED", 4),
    RFI_RECEIVED("RFI_RECEIVED", 5);

    private String name;
    private Integer id;
    @JsonValue
    public String getName() {
        return this.name();
    }

    public Integer getId() {
        return id;
    }

    public static TransactionTagType fromId(Integer id) {
        for (TransactionTagType tagType : TransactionTagType.values()) {
            if (tagType.id.equals(id)) {
                return tagType;
            }
        }
        return null;
    }

    @JsonCreator
    public static TransactionTagType fromName(String name) {
        for (TransactionTagType t : TransactionTagType.values()) {
            if (t.name().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }

    public static TransactionTagType fromNameThrowable(String name) {
        for (TransactionTagType t : TransactionTagType.values()) {
            if (t.name().equalsIgnoreCase(name)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + name);
    }
}
