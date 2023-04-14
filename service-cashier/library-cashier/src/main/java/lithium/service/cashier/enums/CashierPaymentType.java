package lithium.service.cashier.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum CashierPaymentType {
    CARD(1, "card"),
    BANK(2, "bank"),
    USSD(3, "ussd"),
    BANK_TRANSFER(4, "bank_transfer"),
    QR(5, "qr"),
    MOBILE_MONEY(6, "mobile_money"),
    ACCOUNT(7,"account");

    @Getter
    @Accessors(fluent = true)
    private Integer code;
    @Getter
    @Accessors(fluent = true)
    private String description;

    public static CashierPaymentType fromDescription(String description) {
        if (description == null) return null;
        for (CashierPaymentType pt: CashierPaymentType.values()) {
            if (pt.description.toUpperCase().startsWith(description.toUpperCase())) {
                return pt;
            }
        }
        return null;
    }
}
