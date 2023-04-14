package lithium.service.cashier.machine.postprocessors;

import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.frontend.UserRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.TransactionRemarkType;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.machine.DoMachineContext;
import lithium.service.cashier.services.DirectWithdrawalService;
import lithium.service.cashier.services.DomainMethodProcessorService;
import lithium.service.cashier.services.ProcessorAccountService;
import lithium.service.cashier.services.TransactionService;
import lithium.service.user.client.objects.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class RefundMoneyForUnverifiedProcessorAccount implements OnSuccessTransactionProcessor {

    private final DomainMethodProcessorService dmpService;
    private final DirectWithdrawalService directWithdrawalService;
    private final TransactionService transactionService;
    private final ProcessorAccountService processorAccountService;

    @Override
    public void runPostProcessor(DoMachineContext context) {
        try {
            Transaction transaction = context.getTransaction();
            if (!TransactionType.DEPOSIT.equals(transaction.getTransactionType())) {
                return;
            }

            boolean reversalOnInvalidAccount = Optional.ofNullable(dmpService.getPropertyValue(context.getProcessor(), "reversal_on_invalid_account"))
                    .map(Boolean::parseBoolean)
                    .orElse(false);
            log.info("Checking feature 'reversal_on_invalid_account' (" + context.getTransaction().getId() + ", " + context.getUser().getGuid() + "): " + reversalOnInvalidAccount);

            if (!reversalOnInvalidAccount) {
                return;
            }

            ProcessorAccount processorAccount = context.getProcessorResponse().getProcessorAccount();

            boolean accountVerificationFail = Optional.ofNullable(processorAccount)
                .map(account -> BooleanUtils.isFalse(account.getVerified()))
                .orElse(false);
            log.info("Checking account verification fail (" + context.getTransaction().getId() + ", " + context.getUser().getGuid() + "): " + accountVerificationFail+ ". ProcessorAccount: " + processorAccount);
            if (!accountVerificationFail) {
                return;
            }

            String withdrawMethodCode = Optional.ofNullable(dmpService.getPropertyValue(context.getProcessor(), "withdraw_method_code"))
                    .orElse("");
            if (withdrawMethodCode.isEmpty()) {
                log.warn("Missing 'withdraw_method_code' property in deposit payment (" + context.getTransaction().getId() + ", " + context.getUser().getGuid() + ")");
                throw new Exception("Missing 'withdraw_method_code' property in deposit payment ");
            }

            if (processorAccount.getId() == null) {
                log.warn("Failed to initiate revert withdraw for transaction " + transaction.getId() + ". Processor account is not saved: " + processorAccount);
                return;
            }

            if (transaction.getLinkedTransaction() != null) {
                log.warn("Revert withdraw id: " + transaction.getLinkedTransaction().getLinkedTransaction() + " was already done for transaction: " + transaction.getId());
                return;
            }

            log.info("Initiating money refund due unverified processor account (" + context.getTransaction().getId() + ", " + context.getUser().getGuid() + ")");
            String domainName = context.getDomainMethod().getDomain().getName();
            String amount = BigDecimal.valueOf(transaction.getAmountCents()).movePointLeft(2).toPlainString();
            Map<String, String> fields = new HashMap<>();
            fields.put("processorAccountId", String.valueOf(transaction.getPaymentMethod().getId()));

            Map<String, String> headers = Optional.ofNullable(context.getUserRequest()).map(UserRequest::getHeaders).orElse(new HashMap<>());
            String ipAddr = Optional.ofNullable(context.getUserRequest()).map(UserRequest::getIpAddr).orElse(null);

            DoResponse withdrawalResponse = directWithdrawalService.getDirectWithdrawalResponse(domainName, withdrawMethodCode, amount, fields, context.getSessionId(),
                    context.getUser().guid(), User.SYSTEM_GUID, false, ipAddr, headers, transaction.getId());
            transactionService.addTransactionRemark(withdrawalResponse.getTransactionId(), "Withdraw created due user's payment method verification failed", TransactionRemarkType.ADDITIONAL_INFO);
            log.info("Direct withdraw (" + withdrawalResponse.getTransactionId() + ") created for reversal funds of previous deposit (" + transaction.getId() + ") due user's payment method verification failed");
        } catch (Exception e) {
            log.error("Got error during reversal funds (" + context + ")", e);
        }
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean shouldProcess(DoMachineContext context, String previousState) {
        return context.getState().equals(DoMachineState.SUCCESS);
    }
}
