package lithium.service.cashier.processor.smartcash.data.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum SmartcashTransactionStatus {
    unknown("unknown"),
    TS("TS"),
    TF("TF"),
    TIP("TIP"),
    TA("TA");

    @Getter
    @Accessors(fluent = true)
    private String code;

    public static SmartcashTransactionStatus fromCode(String name) {
        try {
            return SmartcashTransactionStatus.valueOf(name);
        } catch (Exception e) {
            log.info("Unknown smartcash error: " + name, e);
            return SmartcashTransactionStatus.unknown;
        }
    }
}
