package lithium.service.accounting.client.transactiontyperegister;

import lithium.service.Response;
import lithium.service.accounting.objects.TransactionType;
import lithium.service.accounting.objects.TransactionTypeRegistration;
import lithium.service.accounting.objects.TransactionTypeRegistrationAccount;
import lithium.service.accounting.objects.TransactionTypeRegistrationLabel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionTypeRegisterService {

    private final TransactionTypeRegisterStream stream;

    private ArrayList<TransactionTypeRegistration> list = new ArrayList<>();

    public void register() {
        for (TransactionTypeRegistration transactionTypeRegistration : list) {
            log.info("Sending TransactionTypeRegistration to service accounting internal: " + transactionTypeRegistration);
            stream.register(transactionTypeRegistration);
        }
        list.clear();
    }

    public Response<TransactionType> create(String code) {
        TransactionTypeRegistration t = new TransactionTypeRegistration(code);
        list.add(t);
        return Response.<TransactionType>builder()
                .data(TransactionType.builder().id(t.getId()).build())
                .build();
    }

    private TransactionTypeRegistration findById(Long id) {
        return list.stream().filter(t -> t.getId().equals(id)).findFirst().get();
    }

    public void addLabel(Long id, String label, boolean summarize) {
        findById(id).getLabels().add(TransactionTypeRegistrationLabel.builder()
                .label(label)
                .summarise(summarize)
                .optional(false)
                .unique(false)
                .build());
    }

    public void addLabel(Long id, String label, boolean summarize, boolean summarizeTotal, boolean synchronous) {
        findById(id).getLabels().add(TransactionTypeRegistrationLabel.builder()
                .label(label)
                .summarise(summarize)
                .summariseTotal(summarizeTotal)
                .synchronous(synchronous)
                .optional(false)
                .unique(false)
                .build());
    }

    public void addUniqueLabel(Long id, String label, boolean summarize, String accountTypeCode) {
        findById(id).getLabels().add(TransactionTypeRegistrationLabel.builder()
                .label(label)
                .summarise(summarize)
                .optional(false)
                .unique(true)
                .uniqueAccountTypeCode(accountTypeCode)
                .build());
    }

    public void addUniqueLabel(Long id, String label, boolean summarize, boolean summarizeTotal, boolean synchronous,
            String accountTypeCode) {
        findById(id).getLabels().add(TransactionTypeRegistrationLabel.builder()
                .label(label)
                .summarise(summarize)
                .summariseTotal(summarizeTotal)
                .synchronous(synchronous)
                .optional(false)
                .unique(true)
                .uniqueAccountTypeCode(accountTypeCode)
                .build());
    }

    public void addOptionalLabel(Long id, String label, boolean summarize) {
        findById(id).getLabels().add(TransactionTypeRegistrationLabel.builder()
                .label(label)
                .optional(true)
                .unique(false)
                .summarise(summarize)
                .build());
    }

    public void addOptionalLabel(Long id, String label, boolean summarize, boolean summarizeTotal, boolean synchronous) {
        findById(id).getLabels().add(TransactionTypeRegistrationLabel.builder()
                .label(label)
                .optional(true)
                .unique(false)
                .summarise(summarize)
                .summariseTotal(summarizeTotal)
                .synchronous(synchronous)
                .build());
    }

    public void addAccount(Long id, String accountTypeCode, Boolean debit, Boolean credit) {
        addAccount(id, accountTypeCode, debit, credit, 1);
    }

    public void addAccount(Long id, String accountTypeCode, Boolean debit, Boolean credit, Integer dividerToCents) {
        findById(id).getAccounts().add(TransactionTypeRegistrationAccount.builder()
                .accountTypeCode(accountTypeCode)
                .debit(debit)
                .credit(credit)
                .dividerToCents(dividerToCents)
                .build());
    }
}
