package lithium.service.accounting.objects;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
public class TransactionTypeRegistration {
    private static Long lastId = 0L;
    private final Long id = ++lastId;
    private final String code;
    private final ArrayList<TransactionTypeRegistrationAccount> accounts = new ArrayList<>();
    private final ArrayList<TransactionTypeRegistrationLabel> labels = new ArrayList<>();
}
