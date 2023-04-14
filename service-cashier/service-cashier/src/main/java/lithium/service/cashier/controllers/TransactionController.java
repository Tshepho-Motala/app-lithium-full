package lithium.service.cashier.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lithium.exceptions.Status415NegativeBalanceException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.exceptions.Status414AccountingTransactionDataValidationException;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.accounting.objects.LabelValue;

import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountVerificationType;
import lithium.service.cashier.client.objects.TransactionRemarkType;
import lithium.service.cashier.data.objects.ManualCashierAdjustmentRequest;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.ProcessorAccountStatus;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionData;
import lithium.service.cashier.data.entities.TransactionPaymentType;
import lithium.service.cashier.data.entities.TransactionProcessingAttempt;
import lithium.service.cashier.data.entities.TransactionRemark;
import lithium.service.cashier.data.entities.TransactionStatus;
import lithium.service.cashier.data.entities.TransactionWorkflowHistory;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.entities.backoffice.CashierTransactionBO;
import lithium.service.cashier.data.entities.backoffice.ManualCashierAdjustmentAccountCode;
import lithium.service.cashier.data.entities.frontend.TransactionRemarkFE;
import lithium.service.cashier.data.entities.frontend.TransactionWorkflowHistoryFE;
import lithium.service.cashier.data.objects.LastXTransactionResponseBO;
import lithium.service.cashier.data.objects.ProcessorAccountDetails;
import lithium.service.cashier.client.objects.TransactionFilterRequest;
import lithium.service.cashier.data.repositories.TransactionCommentRepository;
import lithium.service.cashier.data.views.Views;
import lithium.service.cashier.services.ProcessorAccountService;
import lithium.service.cashier.services.ProcessorAccountSummaryService;
import lithium.service.cashier.services.ResultBatchExcelService;
import lithium.service.cashier.services.TransactionProcessingAttemptService;
import lithium.service.cashier.services.TransactionService;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.limit.client.objects.AutoRestrictionTriggerData;
import lithium.service.limit.client.stream.AutoRestrictionTriggerStream;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/cashier/transaction")
public class TransactionController {
    @Autowired
    private UserApiInternalClientService userApiInternalClientService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ResultBatchExcelService resultBatchExcelService;
    @Autowired
    private ProcessorAccountService processorAccountService;
    @Autowired
    private ProcessorAccountSummaryService paSummaryService;
    @Autowired
    AutoRestrictionTriggerStream autoRestrictionTriggerStream;
    @Autowired
    private TransactionProcessingAttemptService transactionProcessingAttemptService;
    @Autowired
    private TransactionCommentRepository commentRepo;

    @GetMapping("/table")
    @JsonView(Views.Public.class)
    public DataTableResponse<CashierTransactionBO> table(TransactionFilterRequest filter, DataTableRequest request) {

        Page<Transaction> transactions =
                transactionService.findByFilter(request, filter);

        for (Transaction transaction : transactions) {
            TransactionRemark top1TransactionRemark = transactionService.getTop1TransactionRemark(transaction);
            transaction.setHasRemarks(top1TransactionRemark != null);
        }

        Page<CashierTransactionBO> transactionsFE = transactions.map(this::buildTransactionFullFE);
        return new DataTableResponse<>(request, transactionsFE);
    }

    @PostMapping("/search")
    @JsonView(Views.Public.class)
    public DataTableResponse<CashierTransactionBO> transactionSearch(
            @RequestBody TransactionFilterRequest filter,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size,
            @RequestParam(value = "order", required = false,  defaultValue = "id") String orderDirection,
            @RequestParam(value = "sort", required = false,  defaultValue = "asc") String sort
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sort), new String[]{orderDirection});
        DataTableRequest request = new DataTableRequest();
        request.setPageRequest(pageRequest);

        Page<Transaction> transactions =
                transactionService.findByFilter(request, filter);

        for (Transaction transaction : transactions) {
            TransactionRemark top1TransactionRemark = transactionService.getTop1TransactionRemark(transaction);
            transaction.setHasRemarks(top1TransactionRemark != null);
        }
        Page<CashierTransactionBO> transactionsFE = transactions.map(this::buildTransactionFullFE);
        return new DataTableResponse<>(
                request,
                transactionsFE,
                transactionsFE.getTotalElements(),
                transactionsFE.getPageable().getPageNumber(),
                transactionsFE.getTotalPages()
        );
    }

    @GetMapping("/lastXtransactions")
    @JsonView(Views.Public.class)
    public Response<LastXTransactionResponseBO> lastXTable(
            @RequestParam(name = "trId") long transactionId,
            @RequestParam(name = "count") int count
    ) throws Exception {
        log.trace("transactionService.findLast10");

        LastXTransactionResponseBO result =
                transactionService.findLastXByUser(transactionId, count);

        return Response.<LastXTransactionResponseBO>builder()
                .data(result)
                .status(Status.OK)
                .build();
    }

    @GetMapping("/{transactionId}")
    @JsonView(Views.Public.class)
    public Response<?> transaction(
            @PathVariable("transactionId") Transaction transaction
    ) {
        return Response.<CashierTransactionBO>builder()
                .data(buildTransactionFullFE(transaction))
                .status(Status.OK)
                .build();
    }


    /**
     * Returns a transaction that is linked to the provided transaction id or null if none is found.
     *
     * @param transaction
     * @return
     */
    @GetMapping("/linkof/{transactionId}")
    @JsonView(Views.Public.class)
    public Response<?> linkedTransaction(
            @PathVariable("transactionId") Transaction transaction
    ) {

        return Response.<Transaction>builder()
                .data(transaction.getLinkedTransaction())
                .status(Status.OK)
                .build();
    }

    @GetMapping("/{transactionId}/labels")
    @JsonView(Views.Public.class)
    public Response<?> labels(
            @PathVariable("transactionId") Transaction transaction
    ) {
        return Response.<List<LabelValue>>builder()
                .data(transactionService.accountingLabels(transaction))
                .status(Status.OK)
                .build();
    }

    @GetMapping("/{transactionId}/workflow")
    @JsonView(Views.Public.class)
    public Response<?> workflow(@PathVariable("transactionId") Transaction transaction,
                                @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize,
                                @RequestParam(value = "truncate", required = false, defaultValue = "false") boolean truncate) {
        Page<TransactionWorkflowHistoryFE> transactionWorkflowHistoryFE = transactionService.workflow(transaction, page, pageSize, truncate).map(w -> convertTransactionWorkflowHistory(w));
        return Response.<List<TransactionWorkflowHistoryFE>>builder()
                .data(transactionWorkflowHistoryFE.getContent())
                .status(Status.OK)
                .build();
    }

    @GetMapping("/{transactionId}/attempts")
    @JsonView(Views.Public.class)
    public Response<?> attempts(
            @PathVariable("transactionId") Transaction transaction
    ) {
        return Response.<List<TransactionProcessingAttempt>>builder()
                .data(transactionProcessingAttemptService.attempts(transaction))
                .status(Status.OK)
                .build();
    }

    @GetMapping("/{transactionId}/attempt/{workflowToId}")
    @JsonView(Views.Public.class)
    public Response<?> attempt(
            @PathVariable("transactionId") Transaction transaction,
            @PathVariable("workflowToId") TransactionWorkflowHistory transactionWorkflowHistory
    ) {
        return Response.<TransactionProcessingAttempt>builder()
                .data(transactionProcessingAttemptService.attempt(transaction, transactionWorkflowHistory))
                .status(Status.OK)
                .build();
    }

    @GetMapping("/{transactionId}/data/{stage}")
    @JsonView(Views.Public.class)
    public Response<?> dataPerStage(
            @PathVariable("transactionId") Transaction transaction,
            @PathVariable("stage") Integer stage
    ) {
        return Response.<List<TransactionData>>builder()
                .data(transactionService.dataPerStage(transaction, stage))
                .status(Status.OK)
                .build();
    }

    @GetMapping("/{transactionId}/data")
    @JsonView(Views.Public.class)
    public Response<?> data(
            @PathVariable("transactionId") Transaction transaction
    ) {
        return Response.<List<TransactionData>>builder()
                .data(transactionService.data(transaction))
                .status(Status.OK)
                .build();
    }

    @GetMapping("/statuses")
    @JsonView(Views.Public.class)
    public Response<?> statuses() {
        return Response.<List<TransactionStatus>>builder()
                .data(transactionService.statuses())
                .status(Status.OK)
                .build();
    }

    @GetMapping("/paymentTypes")
    @JsonView(Views.Public.class)
    public Response<List<TransactionPaymentType>> paymentTypes() {
        return Response.<List<TransactionPaymentType>>builder()
                .data(transactionService.paymentTypes())
                .status(Status.OK)
                .build();
    }

    @PostMapping("/{transactionId}/add-transaction-remark")
    public Response<TransactionRemark> addTransactionRemark(
            LithiumTokenUtil tokenUtil,
            @PathVariable("transactionId") Transaction transaction,
            @RequestBody lithium.service.cashier.data.objects.TransactionRemark transactionRemark
    ) {
        return Response.<TransactionRemark>builder().data(transactionService.addTransactionRemark(transaction,
                tokenUtil.guid(), transactionRemark.getMessage(), TransactionRemarkType.OPERATOR)).status(Status.OK).build();
    }

    @GetMapping("/{transactionId}/get-transaction-remarks")
    public Response<List<TransactionRemarkFE>> getTransactionRemarks(
            @PathVariable("transactionId") Transaction transaction
    ) {
        List<TransactionRemarkFE> transactionRemarksFE = new ArrayList<>();
        transactionService.getTransactionRemarks(transaction).stream().forEach(transactionRemark -> {
            User author = transactionRemark.getAuthor();
            TransactionRemarkFE transactionRemarkFE = TransactionRemarkFE.builder()
                    .id(transactionRemark.getId())
                    .timestamp(transactionRemark.getTimestamp())
                    .transaction(transactionRemark.getTransaction())
                    .author(author)
                    .message(transactionRemark.getMessage())
                    .build();
            if (author != null) {
                transactionRemarkFE.setAuthorName(userApiInternalClientService.getUserName(author.getGuid()));
            }
            transactionRemarksFE.add(transactionRemarkFE);
        });

        return Response.<List<TransactionRemarkFE>>builder().data(transactionRemarksFE)
                .status(Status.OK).build();
    }

    @RequestMapping("/user/payment-methods")
    public Response<List<ProcessorAccountDetails>> userPaymentMethodsByUser(
            @RequestParam(name = "userGuid", required = true) String userGuid,
            @RequestParam(name = "domain", required = false) String domainName){
        try {
            List<ProcessorAccountDetails> userPaymentMethods = paSummaryService.getProcessorAccountDetailsByUser(userGuid, domainName);

            return Response.<List<ProcessorAccountDetails>>builder()
                    .data(transactionService.updatePaymentMethodsCreationDate(userGuid, userPaymentMethods))
                    .status(Response.Status.OK)
                    .build();
        } catch (Exception e) {
            log.error("Could not retrieve payment account for user " + userGuid + " | " + e.getMessage(), e);
            return Response.<List<ProcessorAccountDetails>>builder().status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @RequestMapping("/{tranId}/payment-methods")
    public Response<List<ProcessorAccountDetails>> userPaymentMethodsByTransactionId(
            @PathVariable Long tranId) {

        Transaction transaction = transactionService.findById(tranId);

        return userPaymentMethodsByUser(transaction.getUser().getGuid(), transaction.getDomainMethod().getDomain().getName());
    }

    //rename to pa
    @RequestMapping("/payment-methods/status-all")
    public Response<Iterable<ProcessorAccountStatus>> paymentMethodsStatusAll() {
        return Response.<Iterable<ProcessorAccountStatus>>builder().data(processorAccountService.getProcessorAccountStatusAll()).build();
    }

    @RequestMapping("{domainName}/payment-methods/{id}/status-update")
    public Response<ProcessorAccount> processorAccountsStatusUpdate(
            @PathVariable("domainName") String domainName,
            @PathVariable("id") Long processorAccountId,
            @RequestParam(name = "statusId", required = true) ProcessorAccountStatus status,
            @RequestParam(name = "verified", required = false) Boolean verified,
            @RequestParam(name = "contraAccount", required = false) Boolean contraAccount,
            @RequestParam(name = "comment", required = false) String comment, LithiumTokenUtil tokenUtil) {
        try {
            if (BooleanUtils.isTrue(contraAccount) && !BooleanUtils.isTrue(verified)) {
                return Response.<ProcessorAccount>builder().status(Status.BAD_REQUEST).message("Unverified account can not be set as contra.").build();
            }
            ProcessorUserCard processorAccountEntry = processorAccountService.updateProcessorAccount(processorAccountId, status, null, null, null, verified, BooleanUtils.isFalse(verified) ? ProcessorAccountVerificationType.MANUAL_VERIFICATION : null,
                    contraAccount, null, comment, tokenUtil);
            //check processor account restrictions
            autoRestrictionTriggerStream.trigger(AutoRestrictionTriggerData.builder()
                    .userGuid(processorAccountEntry.getUser().getGuid()).build());
            return Response.<ProcessorAccount>builder().data(processorAccountService.processorAccountFromEntity(processorAccountEntry, true)).status(Status.OK).build();
        } catch (Exception e) {
            log.error("Could not update processor account status " + processorAccountId + " | " + e.getMessage(), e);
            return Response.<ProcessorAccount>builder().status(Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{transactionId}/workflow-pageable")
    @JsonView(Views.Public.class)
    public Response<Page<TransactionWorkflowHistoryFE>> countTransactionWorkflowHistory(
            @PathVariable("transactionId") Transaction transaction,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize) {
        Page<TransactionWorkflowHistoryFE> transactionWorkflowHistoryFE = transactionService.workflow(transaction, page, pageSize, false)
                .map(w -> convertTransactionWorkflowHistory(w));
        return Response.<Page<TransactionWorkflowHistoryFE>>builder()
                .status(Status.OK)
                .data(transactionWorkflowHistoryFE)
                .build();
    }

    @PostMapping("/reverse")
    public Response<AdjustmentTransaction> reverse(
            @Valid @RequestBody ManualCashierAdjustmentRequest request,
            LithiumTokenUtil token) 
            throws Status414AccountingTransactionDataValidationException, Status415NegativeBalanceException, Status500InternalServerErrorException {
        try {
            return transactionService.reverse(request.getTransactionId(), request.getAccountCode(), request.getComment(), token);
        } catch (IllegalArgumentException e) {
            return Response.<AdjustmentTransaction>builder().status(Status.BAD_REQUEST).message(e.getMessage()).build();
        }
    }
    
    private CashierTransactionBO buildTransactionFullFE(Transaction transaction) {
        User reviewedBy = transaction.getReviewedBy();
        Long reviewedById = null;
        String reviewedByName = null;
        if (reviewedBy != null) {
            reviewedByName = userApiInternalClientService.getUserName(transaction.getReviewedBy().guid());
            reviewedById = reviewedBy.getId();
        }
        List<ManualCashierAdjustmentAccountCode> manualCashierAdjustmentAccountCodes = null;
        if (transaction.getManualCashierAdjustmentId() == null && !transaction.getCurrent().getStatus().getActive()) {
            manualCashierAdjustmentAccountCodes = ManualCashierAdjustmentAccountCode.getAllowedCodes(transaction);
        }

        return CashierTransactionBO.builder()
                .id(transaction.getId())
                .version(transaction.getVersion())
                .createdOn(transaction.getCreatedOn())
                .updatedOn(transaction.getCurrent().getTimestamp())
                .domainMethod(transaction.getDomainMethod())
                .directWithdrawal(transaction.getDirectWithdrawal())
                .initiationAuthorFullName(transaction.getInitiationAuthor() == null ? null : userApiInternalClientService.getUserName(transaction.getInitiationAuthor().guid()))
                .user(transaction.getUser())
                .current(transaction.getCurrent())
                .amountCents(transaction.getAmountCents())
                .feeCents(transaction.getFeeCents())
                .currencyCode(transaction.getCurrencyCode())
                .transactionType(transaction.getTransactionType())
                .processorReference(transaction.getProcessorReference())
                .ttl(transaction.getTtl())
                .bonusCode(transaction.getBonusCode())
                .accountInfo(transaction.getAccountInfo())
                .manual(transaction.isManual())
                .forcedSuccess(transaction.isForcedSuccess())
                .bonusId(transaction.getBonusId())
                .retryProcessing(transaction.isRetryProcessing())
                .linkedTransaction(transaction.getLinkedTransaction())
                .accRefToWithdrawalPending(transaction.getAccRefToWithdrawalPending())
                .accRefFromWithdrawalPending(transaction.getAccRefFromWithdrawalPending())
                .additionalReference(transaction.getAdditionalReference())
                .sessionId(transaction.getSessionId())
                .transactionPaymentType(transaction.getTransactionPaymentType())
                .testAccount(transaction.getUser().isTestAccount())
                .autoApproved(transaction.isAutoApproved())
                .reviewedByFullName(reviewedByName)
                .declineReason(transaction.getDeclineReason())
                .paymentMethod(transaction.getPaymentMethod())
                .hasRemarks(transaction.getHasRemarks())
                .runtime(transaction.getRuntime())
                .reviewedById(reviewedById)
                .tags(transaction.getTags().stream().map(transactionTag -> transactionTag.getType().getName()).collect(Collectors.toList()))
                .manualCashierAdjustmentAccountCodes(manualCashierAdjustmentAccountCodes)
                .manualCashierAdjustmentId(transaction.getManualCashierAdjustmentId())
                .build();
    }

    private TransactionWorkflowHistoryFE convertTransactionWorkflowHistory(TransactionWorkflowHistory transactionWorkflowHistory) {
        User author = transactionWorkflowHistory.getAuthor();
        TransactionWorkflowHistoryFE transactionWorkflowFE = TransactionWorkflowHistoryFE.builder()
                .id(transactionWorkflowHistory.getId())
                .author(author)
                .timestamp(transactionWorkflowHistory.getTimestamp())
                .transaction(transactionWorkflowHistory.getTransaction())
                .processor(transactionWorkflowHistory.getProcessor())
                .status(transactionWorkflowHistory.getStatus())
                .assignedTo(transactionWorkflowHistory.getAssignedTo())
                .accountingReference(transactionWorkflowHistory.getAccountingReference())
                .stage(transactionWorkflowHistory.getStage())
                .source(transactionWorkflowHistory.getSource())
                .billingDescriptor(transactionWorkflowHistory.getBillingDescriptor())
                .comments(commentRepo.findByWorkflow(transactionWorkflowHistory))
                .build();
        if (author != null) {
            transactionWorkflowFE.setAuthorName(userApiInternalClientService.getUserName(author.guid()));
        }
        return transactionWorkflowFE;
    }

}
