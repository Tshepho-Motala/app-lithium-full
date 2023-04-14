package lithium.service.document.generation.client.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CsvProvider {
    CASHIER_TRANSACTION("service-csv-provider-cashier-transactions"),
    MAIL("service-csv-provider-mail"),
    THRESHOLD("service-csv-provider-threshold"),
    CASINO("service-csv-provider-casino"),
    USER("service-csv-provider-user");

    @Getter
    @Accessors(fluent = true)
    private String key;

    @JsonCreator
    public static CsvProvider fromKey(String key) {
        for (CsvProvider p : CsvProvider.values()) {
            if (p.key.equalsIgnoreCase(key)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Can't resolve Biometrics Status from value: " + key);
    }
}
