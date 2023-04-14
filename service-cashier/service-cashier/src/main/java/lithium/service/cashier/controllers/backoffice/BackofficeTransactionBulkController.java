package lithium.service.cashier.controllers.backoffice;

import lithium.service.Response;
import lithium.service.cashier.client.internal.TransactionProcessingCode;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionWorkflowHistory;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.entities.backoffice.TransactionBulkResponse;
import lithium.service.cashier.data.objects.BulkResult;
import lithium.service.cashier.client.objects.TransactionFilterRequest;
import lithium.service.cashier.services.TransactionService;
import lithium.service.cashier.services.TranslationService;
import lithium.service.cashier.services.transactionbulk.TransactionBulkProcessingService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/backoffice/cashier/transaction-bulk-processing")
@AllArgsConstructor
@Slf4j
public class BackofficeTransactionBulkController {
    private final TransactionBulkProcessingService transactionBulkProcessingService;
    private final TransactionService transactionService;
    private final TranslationService translationService;


    @GetMapping("/list")
    public DataTableResponse<TransactionBulkResponse> list(TransactionFilterRequest filter, DataTableRequest request) {

        Page<Transaction> transactions =
                transactionService.findByFilter(request, filter);

        Page<TransactionBulkResponse> bulkResponses = transactions.map(this::buildTransactionBulkResponse);
        return new DataTableResponse<>(request, bulkResponses);
    }

    private TransactionBulkResponse buildTransactionBulkResponse(Transaction transaction) {
        User user = transaction.getUser();
        boolean enoughBalance = transactionService.hasEnoughBalance(user.domainName(), user.getGuid(),
                transaction.getCurrencyCode(), transaction.getId(), transaction.getAccRefToWithdrawalPending() != null);
        String comment = null;
        if (!enoughBalance) {
            comment = translationService.translate(user.domainName(),"SERVICE-CASHIER.ERROR_INSUFFICIENT_FUNDS");
        }
        return TransactionBulkResponse.builder()
                .id(transaction.getId())
                .createdOn(transaction.getCreatedOn())
                .directWithdrawal(transaction.getDirectWithdrawal())
                .user(user)
                .amountCents(transaction.getAmountCents())
                .feeCents(transaction.getFeeCents())
                .currencyCode(transaction.getCurrencyCode())
                .transactionType(transaction.getTransactionType())
                .manual(transaction.isManual())
                .sessionId(transaction.getSessionId())
                .testAccount(user.isTestAccount())
                .autoApproved(transaction.isAutoApproved())
                .declineReason(transaction.getDeclineReason())
                .paymentMethod(transaction.getPaymentMethod())
                .status(transaction.getStatus())
                .processorDescription(Optional.of(transaction.getCurrent())
                        .map(TransactionWorkflowHistory::getProcessor)
                        .map(DomainMethodProcessor::getDescription)
                        .orElse(null))
                .canApprove(enoughBalance && transaction.getStatus().isAbleToApprove())
                .canOnHold(transaction.getStatus().isAbleToHold())
                .comment(comment)
                .build();
    }


    @PostMapping("/proceed-by-guid")
    public Response<Integer> proceedByGuid(
            @RequestParam("guid") String guid,
            @RequestParam("comment") String comment,
            @RequestParam("code") TransactionProcessingCode transactionProcessingCode,
            LithiumTokenUtil token
    ) {
        try {
            Integer transactionCount = transactionBulkProcessingService.proceed(transactionProcessingCode, guid, comment, token);
            if (transactionCount < 0) {
                log.error("Failed to move withdrawals on hold for guid=" + guid);
                return Response.<Integer>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message("Failed to move withdrawals to " + transactionProcessingCode).build();
            }
            return Response.<Integer>builder().data(transactionCount).status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to move withdrawals to " + transactionProcessingCode + " for guid=" + guid + " :" + e.getMessage(), e);
            return Response.<Integer>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }

    @PostMapping("/proceed-by-ids")
    public Response<BulkResult> proceedByIds(
            @RequestParam("ids") List<Long> transactionIds,
            @RequestParam("code") TransactionProcessingCode transactionProcessingCode,
            LithiumTokenUtil token
    ) {
        try {
            BulkResult approveResponses = transactionBulkProcessingService.proceed(transactionProcessingCode, transactionIds, "Bulk approved", token);
            return Response.<BulkResult>builder().data(approveResponses).status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to proceed " + transactionProcessingCode + " transactions " + transactionIds, e);
            return Response.<BulkResult>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }

    @PostMapping("/cancel-by-ids")
    public Response<BulkResult> cancelByIds(
            @RequestParam("ids") List<Long> transactionIds,
            LithiumTokenUtil token
    ) {
        try {
            BulkResult cancelResponses = transactionBulkProcessingService.proceed(TransactionProcessingCode.CANCEL, transactionIds, "Transaction cancelled via bulk operations", token);
            return Response.<BulkResult>builder().data(cancelResponses).status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to proceed Cancel transactions " + transactionIds, e);
            return Response.<BulkResult>builder().status(Response.Status.INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
        }
    }
}
