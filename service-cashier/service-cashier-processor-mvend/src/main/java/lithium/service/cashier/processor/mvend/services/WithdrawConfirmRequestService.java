package lithium.service.cashier.processor.mvend.services;

import lithium.exceptions.NotRetryableErrorCodeException;
import lithium.metrics.CountThisMethod;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.mvend.api.exceptions.Status900InvalidHashException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status901InvalidOrMissingParameters;
import lithium.service.cashier.processor.mvend.api.exceptions.Status901UserNotFoundException;
import lithium.service.cashier.processor.mvend.api.exceptions.Status999GeneralFailureException;
import lithium.service.cashier.processor.mvend.context.DepositRequestContext;
import lithium.service.cashier.processor.mvend.context.WithdrawConfirmationContext;
import lithium.service.cashier.processor.mvend.services.shared.SharedService;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.ExceptionMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WithdrawConfirmRequestService {

    @Autowired
    CashierDoCallbackService cashierService;

    @Autowired
    UserApiInternalClientService userService;

    @Autowired
    SharedService sharedService;

    @TimeThisMethod
    @Retryable(exclude = NotRetryableErrorCodeException.class)
    public void withdrawConfirm(WithdrawConfirmationContext context)
            throws Status999GeneralFailureException, Status901InvalidOrMissingParameters,
            Status900InvalidHashException, Status901UserNotFoundException {

        try {

            validateTransactionId(context);
            validateStatus(context);
            sharedService.getPropertiesDMPFromServiceCashier(context, true, "mvend");
            sharedService.validateUsernameAndPassword(context);
            sharedService.validateHash(context);
            sharedService.getUserFromServiceUser(context);
            completeWithdrawalInServiceCashier(context);

        } catch (UserNotFoundException e) {
            throw new Status901UserNotFoundException();
        } catch (UserClientServiceFactoryException e) {
            throw new Status999GeneralFailureException(e.getMessage());
        }
    }

    private void validateTransactionId(WithdrawConfirmationContext context) throws Status901InvalidOrMissingParameters {
        if ((context.getRequest().getTransactionref() == null) ||
                (context.getRequest().getTransactionref().length() < 1)) {
            throw new Status901InvalidOrMissingParameters("transactionRef should not be empty");
        }

        try {
            Response<DoProcessorRequest> response =
            cashierService.doCallbackGetTransactionFromProcessorReference(context.getRequest().getTransactionref(), "mvend");
            if (response.isSuccessful()) {
                context.setTransactionId(response.getData().getTransactionId());
            } else {
                throw new Exception(response.getStatus() + " " + response.getMessage());
            }
        } catch (Exception e) {
            String message = "Could not find transaction in cashier with this reference: "
                    + context.getRequest().getTransactionref() + " " + ExceptionMessageUtil.allMessages(e);
            log.error(message, e);
            throw new Status901InvalidOrMissingParameters(message);
        }

    }

    private void validateStatus(WithdrawConfirmationContext context) throws Status901InvalidOrMissingParameters {
        String status = context.getRequest().getStatus();
        if (status == null) throw new Status901InvalidOrMissingParameters("Status cannot be empty. " +
                "Expecting either 'Completed' or 'Failed'");
        switch (status) {
            case "Completed": context.setStatus(DoProcessorResponseStatus.SUCCESS); return;
            case "Failed": context.setStatus(DoProcessorResponseStatus.DECLINED); return;
        }
        throw new Status901InvalidOrMissingParameters("Status is invalid. Expecting either 'Completed' or 'Failed'");
    }

    @CountThisMethod
    private void completeWithdrawalInServiceCashier(WithdrawConfirmationContext context) throws Status999GeneralFailureException {
        try {

            Map<Integer, Map<String, String>> outputData = new HashMap<>();
            Map<String, String> output = new HashMap<>();
            String paymentRef = context.getRequest().getPaymentref();

            output.put("paymentRef", paymentRef);
            output.put("confirm-paymentRef", paymentRef);
            output.put("status", context.getRequest().getStatus());
            output.put("processor", context.getRequest().getProcessor());
            
            if (context.getRequest().getProcessor_details() != null) {
                output.put("processor.account", context.getRequest().getProcessor_details().getAccount());
                output.put("processor.bank_code", context.getRequest().getProcessor_details().getBank_code());
                output.put("processor.name", context.getRequest().getProcessor_details().getName());
                output.put("processor.type", context.getRequest().getProcessor_details().getType());
            }

            outputData.put(1, output);

            Response<String> response = cashierService.doCallback(DoProcessorResponse.builder()
                    .transactionId(context.getTransactionId())
                    .processorReference(context.getRequest().getTransactionref())
                    .additionalReference(paymentRef)
                    .outputData(outputData)
                    .rawResponseLog(context.getRequest().toString())
                    .status(context.getStatus()).build());

            if (response.isSuccessful()) return;

            throw new Status999GeneralFailureException(response.getStatus() + " " + response.getMessage());
        } catch (Exception e) {
            log.error("Complete withdrawal failed " + context, e);
            throw new Status999GeneralFailureException("Complete withdrawal failed " + ExceptionMessageUtil.allMessages(e));
        }
    }
}
