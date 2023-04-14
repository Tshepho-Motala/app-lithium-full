package lithium.service.cashier.controllers.internal;

import lithium.service.Response;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.objects.DepositStatus;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.TransactionWorkflowHistory;
import lithium.service.cashier.machine.DoMachine;
import lithium.service.cashier.services.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
@RestController
public class DoRegisterDepositController {

    @Autowired
    WebApplicationContext beanContext;


    @RequestMapping(path = "/internal/deposit/register", method = RequestMethod.GET)
    public Response<Long> registerDeposit(
            @RequestParam("domainMethodProcessorId") Long domainMethodProcessorId,
            @RequestParam("userGuid") String userGuid,
            @RequestParam("currencyCode") String currencyCode,
            @RequestParam("amountInCents") Long amountInCents,
            @RequestParam("reference") String reference,
            @RequestParam("additionalReference") String additionalReference,
            @RequestParam("sessionId") Long sessionId,
            @RequestParam("success") boolean success,
            @RequestParam("paymentType") String paymentType) {

        String context = "Deposit context [domainMethodProcessorId=" + domainMethodProcessorId + ", userGuid=" + userGuid +
             ", currencyCode=" + currencyCode + ", amountInCents=" + amountInCents +
             ", reference=" + reference + ", additionalReference=" + additionalReference + ", sessionId=" + sessionId + ", success=" + success + ", playerPaymentType=" + paymentType + "]";

        DoMachine machine = beanContext.getBean(DoMachine.class);

        try {
            DoResponse response = machine.doAPITransaction(domainMethodProcessorId,
                amountInCents, userGuid, reference, additionalReference, currencyCode, sessionId, success, paymentType);
            if ((response.getError() != null) && (response.getError())) {
                throw new Exception(response.getErrorMessage());
            }

            log.debug("Registered deposit: " + context + ", response: " + response);
            return Response.<Long>builder()
               .data(response.getTransactionId())
               .status(Response.Status.OK)
               .build();

        } catch (Exception e) {
            log.error("Can't register deposit: " + context + " due " + e.getMessage() + ": ", e);
            return Response.<Long>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }
    };

    @RequestMapping(path="/internal/deposit/status", method=RequestMethod.GET)
    public Response<DepositStatus> depositStatus(
            @RequestParam("processorReference") String processorReference,
            @RequestParam("processorCode") String processorCode) {

        TransactionService transactionService = beanContext.getBean(TransactionService.class);

        try {
            Transaction transaction = transactionService.findByProcessorReference(processorReference);
            if (transaction == null) throw new Exception("Invalid transaction processorReference "+processorReference);
            TransactionWorkflowHistory transactionCurrent = transaction.getCurrent();
            if (!transactionCurrent.getProcessor().getProcessor().getCode().equals(processorCode))
                throw new Exception("The transaction does not belong to this processor");
            DepositStatus data = new DepositStatus(transactionCurrent.getStatus().getCode(), transaction.getCreatedOn());
            return Response.<DepositStatus>builder()
                    .data(data)
                    .status(Response.Status.OK)
                    .build();

        } catch (Exception e) {
            return Response.<DepositStatus>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .build();
        }

    }

    @RequestMapping(path = "/internal/summary/pending/amount", method = RequestMethod.GET)
    public Response<Long> pendingAmountCents(
            @RequestParam("userGuid") String userGuid) {

        TransactionService transactionService = beanContext.getBean(TransactionService.class);
        return Response.<Long>builder()
                .data(transactionService.getSummaryPendingAmountForUser(userGuid))
                .status(Response.Status.OK)
                .build();

    }
}
