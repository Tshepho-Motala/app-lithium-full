package lithium.service.accounting.provider.internal.stream;

import lithium.service.accounting.objects.TransactionTypeRegistration;
import lithium.service.accounting.objects.TransactionTypeRegistrationAccount;
import lithium.service.accounting.objects.TransactionTypeRegistrationLabel;
import lithium.service.accounting.provider.internal.conditional.NotReadOnlyConditional;
import lithium.service.accounting.provider.internal.data.entities.TransactionType;
import lithium.service.accounting.provider.internal.services.TransactionTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Conditional(NotReadOnlyConditional.class)
@EnableBinding(TransactionTypeQueueSink.class)
public class TransactionTypeQueueProcessor {

	@Autowired
	TransactionTypeService service;

	@StreamListener(TransactionTypeQueueSink.INPUT)
	void handle(TransactionTypeRegistration ttr) {
		log.info("Received transaction type registration via queue:" + ttr);
		try {
			TransactionType tt = service.findOrCreate(ttr.getCode());
			for (TransactionTypeRegistrationAccount account : ttr.getAccounts()) {
				service.addAccount(tt, account.getAccountTypeCode(), account.isDebit(), account.isCredit(), account.getDividerToCents());
			}
			for (TransactionTypeRegistrationLabel label : ttr.getLabels()) {
				if (label.isOptional()) {
					service.addOptionalLabel(tt, label.getLabel(), label.isSummarise(), label.isSummariseTotal(),
							label.isSynchronous());
				} else if (label.isUnique()) {
					service.addUniqueLabel(tt, label.getLabel(), label.isSummarise(), label.isSummariseTotal(),
							label.isSynchronous(), label.getUniqueAccountTypeCode());
				} else {
					service.addLabel(tt, label.getLabel(), label.isSummarise(), label.isSummariseTotal(),
							label.isSynchronous());
				}
			}
		} catch (Exception e) {
			log.error("Failed to process transaction type registration: {}, {}", ttr, e.getMessage(), e);
		}
		// TODO implement service method to prune old labels that no longer form part of the tree.
	}

}
