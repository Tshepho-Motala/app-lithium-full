package lithium.service.cashier.processor.trustly.controllers;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.service.Response;
import lithium.service.cashier.client.frontend.ProcessorAccountResponse;
import lithium.service.cashier.client.frontend.ProcessorAccountResponseStatus;
import lithium.service.cashier.client.internal.AccountProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.internal.DoProcessorResponseStatus;
import lithium.service.cashier.client.objects.GeneralError;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.ProcessorAccountTransactionState;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.objects.ProcessorNotificationData;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.client.service.CashierDoCallbackService;
import lithium.service.cashier.client.service.CashierInternalClientService;
import lithium.service.cashier.processor.trustly.TrustlyService;
import lithium.service.cashier.processor.trustly.api.NotificationHandler;
import lithium.service.cashier.processor.trustly.api.data.Method;
import lithium.service.cashier.processor.trustly.api.data.ResponseStatus;
import lithium.service.cashier.processor.trustly.api.data.notification.Notification;
import lithium.service.cashier.processor.trustly.api.data.notification.NotificationData;
import lithium.service.cashier.processor.trustly.api.data.notification.notificationdata.AccountNotificationData;
import lithium.service.cashier.processor.trustly.api.data.notification.notificationdata.CreditData;
import lithium.service.cashier.processor.trustly.api.data.notification.notificationdata.PayoutConfirmationData;
import lithium.service.cashier.processor.trustly.api.data.response.TrustlyResponse;
import lithium.service.cashier.processor.trustly.api.security.SignatureHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

import static lithium.util.ObjectToFormattedText.objectToPrettyString;

@RestController
@Slf4j
public class CallbackController {

    @Autowired
    CashierDoCallbackService callbackService;

    @Autowired
    CashierInternalClientService cashierService;

    @Autowired
    TrustlyService service;

    @Autowired
    MessageSource messageSource;

    @PostMapping("/public/notify")
    public TrustlyResponse webhook(@RequestBody String data) throws Exception {
        log.debug("Notification is received from trustly: " + data);

        try {
            Notification notification = NotificationHandler.handleNotification(data);

            Long transactionId = Long.parseLong(notification.getParams().getData().getMessageId());

            Response<DoProcessorRequest> processorRequestResponse = getCallbackGetTransaction(transactionId);

            DoProcessorRequest processorRequest = processorRequestResponse.getData();

            PrivateKey privateKey = SignatureHandler.getPrivateKey(Base64.getDecoder().decode(processorRequest.getProperty("rsa_private_key")), processorRequest.getProperty("rsa_private_key_password"));
            PublicKey publicKey = SignatureHandler.getPublicKey(Base64.getDecoder().decode(processorRequest.getProperty("rsa_public_key")));

            NotificationHandler.verifyNotification(notification, publicKey);

            //successful notification for the deposit
            if (notification.getMethod() == Method.CREDIT && processorRequest.getTransactionType() == TransactionType.DEPOSIT) {

                DoProcessorResponse response = DoProcessorResponse.builder()
                        .transactionId(processorRequest.getTransactionId())
                        .build();

                CreditData creditData = (CreditData) notification.getParams().getData();
                if (!Boolean.parseBoolean(processorRequest.getProperty("skip_amount_check"))) {
                    response.setAmountCentsReceived(CurrencyAmount.fromAmountString(creditData.getAmount()).toCents().intValue());
                }
                response.setStatus(DoProcessorResponseStatus.SUCCESS);
                response.addRawRequestLog("Received trustly credit notification: " + objectToPrettyString(notification));
                callbackService.doSafeCallback(response);

            //failed notification for the payout
            } else if (notification.getMethod() == Method.CREDIT && processorRequest.getTransactionType() == TransactionType.WITHDRAWAL) {
                DoProcessorResponse response = DoProcessorResponse.builder()
                        .transactionId(processorRequest.getTransactionId())
                        .rawRequestLog("Failed payout!!! Received trustly CREDIT notification on payout " + objectToPrettyString(notification))
                        .build();
                if (!processorRequest.isTransactionFinalized()) {
                    response.setStatus(DoProcessorResponseStatus.DECLINED);
                    response.setDeclineReason(Method.CREDIT.name() + " notification is received");
                }
                ProcessorNotificationData mailData = new ProcessorNotificationData();
                mailData.setRecipientTypes(new String[] {"internal"});
                mailData.setTemplateName(processorRequest.getProperty("reversal_notification_template"));
                mailData.setPlaceholders(service.constructPlaceholders(processorRequest, notification.getMethod().name()));
                response.setNotificationData(mailData);
                log.info("Failed payout!!! TransactionId: " + transactionId +". Received trustly CREDIT notification on payout");

                callbackService.doCallback(response);
                return NotificationHandler.prepareNotificationResponse(notification.getMethod(), notification.getUUID(), ResponseStatus.OK, privateKey);
            //successful notification for the withdraw (should not be a case)
            } else if (notification.getMethod() == Method.DEBIT && processorRequest.getTransactionType() == TransactionType.WITHDRAWAL) {
                //in case accountPayout flow there will be no DEBIY notification from trastly, payoutConfirmation notification instead
                DoProcessorResponse response = DoProcessorResponse.builder()
                        .transactionId(processorRequest.getTransactionId())
                        .rawRequestLog("Received trustly DEBIT notification: " + objectToPrettyString(notification))
                        .build();

                callbackService.doCallback(response);
                log.info("Received trustly DEBIT notification for payout. Should not be a case. TransactionId: " + transactionId);
            //successful notification for the payout
            } else if (notification.getMethod() == Method.PAYOUT_CONFIRMATION) {
                PayoutConfirmationData payoutConfirmationData = (PayoutConfirmationData) notification.getParams().getData();
                BigDecimal finalAmount = new BigDecimal(payoutConfirmationData.getAmount());
                DoProcessorResponse response = DoProcessorResponse.builder()
                        .transactionId(processorRequest.getTransactionId())
                        .build();
                if (!Boolean.parseBoolean(processorRequest.getProperty("skip_amount_check"))) {
                    response.setAmountCentsReceived(CurrencyAmount.fromAmount(finalAmount).toCents().intValue());
                }
                response.setStatus(DoProcessorResponseStatus.SUCCESS);
                response.addRawRequestLog("Received trustly PAYOUT_CONFIRMATION notification: " + objectToPrettyString(notification));
                callbackService.doCallback(response);
                log.info("Received trustly PAYOUT_CONFIRMATION notification for payout. Should not be a case. TransactionId: " + transactionId);
            //failed notification for the deposit
            } else if (notification.getMethod() == Method.DEBIT && processorRequest.getTransactionType() == TransactionType.DEPOSIT) {
                DoProcessorResponse response = DoProcessorResponse.builder()
                        .transactionId(processorRequest.getTransactionId())
                        .rawRequestLog("Failed deposit!!! Received trustly debit notification after credit: " + objectToPrettyString(notification))
                        .build();

                ProcessorNotificationData mailData = new ProcessorNotificationData();
                mailData.setRecipientTypes(new String[] {"external"});
                mailData.setTemplateName(processorRequest.getProperty("reversal_notification_template"));
                mailData.setPlaceholders(service.constructPlaceholders(processorRequest, notification.getMethod().name()));
                mailData.setTo(processorRequest.getProperty("trustly_email"));
                response.setNotificationData(mailData);

                log.info("Failed deposit!!!. Transaction: " + transactionId +". Initiate auto refund on trustly side by sending FAILED notification response.");
                callbackService.doCallback(response);
                return NotificationHandler.prepareNotificationResponse(notification.getMethod(), notification.getUUID(), ResponseStatus.FAILED, privateKey);
            //player closes trustly deposit widget
            } else if (notification.getMethod() == Method.CANCEL && processorRequest.getTransactionType() == TransactionType.DEPOSIT) {
                DoProcessorResponse response = DoProcessorResponse.builder()
                        .transactionId(processorRequest.getTransactionId())
                        .status(DoProcessorResponseStatus.DECLINED)
                        .declineReason(Method.CANCEL.name() + " notification is received")
                        .rawRequestLog("Received trustly CANCEL notification " + objectToPrettyString(notification))
                        .build();
                log.error("TransactionId: " + transactionId +". Was canceled by player");
                callbackService.doCallback(response);
            //failed payout can come before response on payout on step 1 !!!
            }  else if (notification.getMethod() == Method.CANCEL && processorRequest.getTransactionType() == TransactionType.WITHDRAWAL) {
                DoProcessorResponse response = DoProcessorResponse.builder()
                        .transactionId(processorRequest.getTransactionId())
                        .status(DoProcessorResponseStatus.DECLINED)  //trying to set to declined
                        .rawRequestLog("Failed payout!!! Received trustly CANCEL notification on payout " + objectToPrettyString(notification))
                        .declineReason(Method.CANCEL.name() + " notification is received")
                        .build();
                log.error("Failed payout!!! TransactionId: " + transactionId +". Received trustly CANCEL notification on payout");
                callbackService.doCallback(response);
            } else if (notification.getMethod() == Method.ACCOUNT && Boolean.parseBoolean(processorRequest.stageInputData(1, "save_card"))) {
                //workaround to minimize the case when credit notification comes in the same time
                if (!processorRequest.isTransactionFinalized()) {
                    Thread.sleep(3000);
                }
                AccountNotificationData notificationData = (AccountNotificationData)notification.getParams().getData();
                ProcessorAccount processorAccount = ProcessorAccount.builder()
                        .reference(notificationData.getAccountId())
                        .status(PaymentMethodStatusType.ACTIVE)
                        .hideInDeposit(true)
                        .type(ProcessorAccountType.BANK)
                        .providerData(notificationData.getOrderId())
                        .name(notificationData.getAttributes().get("name")==null ? null : notificationData.getAttributes().get("name").toString())
                        .descriptor(notificationData.getAttributes().containsKey("lastdigits") ? notificationData.getAttributes().get("lastdigits").toString() : null)
                        .data(notificationData.getAttributes().entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, e -> (String)e.getValue())))
                        .build();

                DoProcessorResponse response = DoProcessorResponse.builder()
                        .transactionId(processorRequest.getTransactionId())
                        .processorAccount(processorAccount)
                        .rawRequestLog("Received trustly account notification: " + objectToPrettyString(notificationData))
                        .rawResponseLog("Saving processor account: " + objectToPrettyString(processorAccount))
                        .build();

                callbackService.doCallback(response);
            } else {
                log.info("Unsupported notification method " + notification);
            }

            return NotificationHandler.prepareNotificationResponse(notification.getMethod(), notification.getUUID(), ResponseStatus.OK, privateKey);
        } catch (Exception ex) {
           log.error("Failed to handle trustly notification: " + data + " Exception:" + ex.getMessage());
            throw new Status500InternalServerErrorException("Failed to handle notification.");
        }
    }
    private Response<DoProcessorRequest> getCallbackGetTransaction(Long transactionId) throws Exception {
        Response<DoProcessorRequest> processorRequestResponse = callbackService.doCallbackGetTransaction(transactionId, "trustly");
            if (!processorRequestResponse.isSuccessful()) {
                log.error("Failed to get transactionid: " + transactionId);
                throw new Exception(processorRequestResponse.getMessage());
            }
            return processorRequestResponse;
    }

    @PostMapping("/public/account/notify")
    public TrustlyResponse selectAccountWebhook(@RequestBody String data) throws Exception {
        log.debug("Notification is received from trustly: " + data);

        try {
            Notification notification = NotificationHandler.handleNotification(data);

            NotificationData notificationData = (NotificationData)notification.getParams().getData();

            Long transactionId = Long.parseLong(notificationData.getMessageId().split("_")[1]);

            AccountProcessorRequest processorRequest = cashierService.getAccountProcessorRequest(transactionId);

            PrivateKey privateKey = SignatureHandler.getPrivateKey(Base64.getDecoder().decode(processorRequest.getProperty("rsa_private_key")), processorRequest.getProperty("rsa_private_key_password"));
            PublicKey publicKey = SignatureHandler.getPublicKey(Base64.getDecoder().decode(processorRequest.getProperty("rsa_public_key")));

            NotificationHandler.verifyNotification(notification, publicKey);

            if (notification.getMethod() == Method.ACCOUNT) {
                AccountNotificationData accountNotificationData = (AccountNotificationData)notificationData;
                //TODO: handle accountNotificationData.getVerified() ?
                ProcessorAccountResponse processorAccountResponse = ProcessorAccountResponse.builder()
                        .processorAccount(ProcessorAccount.builder()
                                .reference((accountNotificationData).getAccountId())
                                .status(PaymentMethodStatusType.ACTIVE)
                                .hideInDeposit(true)
                                .type(ProcessorAccountType.BANK)
                                .providerData(notificationData.getOrderId())
                                .name(notificationData.getAttributes().get("name")==null ? null : notificationData.getAttributes().get("name").toString())
                                .descriptor(notificationData.getAttributes().containsKey("lastdigits") ? notificationData.getAttributes().get("lastdigits").toString() : null)
                                .data(notificationData.getAttributes().entrySet().stream()
                                        .collect(Collectors.toMap(Map.Entry::getKey, e -> (String)e.getValue())))
                                .build())
                        .transactionId(transactionId)
                        .status(ProcessorAccountResponseStatus.SUCCESS)
                        .build();

                cashierService.saveProcessorAccount(processorAccountResponse);
            } else if (notification.getMethod() == Method.CANCEL) {
                ProcessorAccountResponse processorAccountResponse = ProcessorAccountResponse.builder()
                            .processorAccount(null)
                            .transactionId(transactionId)
                            .status(ProcessorAccountResponseStatus.CANCELED)
                            .errorMessage("Canceled by player.")
                            .generalError(GeneralError.CANCEL_ADD_ACCOUNT.getResponseMessageLocal(messageSource, processorRequest.getUser().getDomain(), processorRequest.getUser().getLanguage()))
                            .build();

                    cashierService.saveProcessorAccount(processorAccountResponse);
            }  else {
                log.info("Unsupported notification method " + notification);
            }
            return NotificationHandler.prepareNotificationResponse(notification.getMethod(), notification.getUUID(), ResponseStatus.OK, privateKey);
        } catch (Exception ex) {
            log.error("Failed to handle trustly notification: " + data + " Exception:" + ex.getMessage());
            throw new Status500InternalServerErrorException("Failed to handle notification.");
        }
    }

    @RequestMapping("/public/redirect/{transactionId}/{status}")
    public ModelAndView depositRedirect(
            @PathVariable("transactionId") Long transactionId,
            @PathVariable("status") String status) throws Exception  {
        try {
            log.debug("Received " + status + " redirect from trustly for transaction: " + transactionId);
            return new ModelAndView(processTrustlyDepositRedirect(transactionId, status.equalsIgnoreCase("success")));
        } catch (Exception ex) {
            log.error("Failed to process " + status + " redirect from trustly. TransactionId: " + transactionId, ex);
            throw ex;
        }
    }

    @RequestMapping("/public/account/redirect/{transactionId}/{status}")
    public ModelAndView accountRedirect(
            @PathVariable("transactionId") Long transactionId,
            @PathVariable("status") String status) throws Exception  {
        try {
            log.debug("Received " + status + " account creation redirect from trustly for transaction: " + transactionId);
            return new ModelAndView(processTrustlyAccountRedirect(transactionId, status.equalsIgnoreCase("success")));
        } catch (Exception ex) {
            log.error("Failed to process " + status +  " account redirect from trustly. TransactionId: " + transactionId, ex);
            throw ex;
        }
    }

    private String processTrustlyDepositRedirect(Long transactionId, boolean isSuccess) throws Exception{

        Response<DoProcessorRequest> processorRequestResponse = callbackService.doCallbackGetTransaction(transactionId, "trustly");
        if (!processorRequestResponse.isSuccessful()) {
            log.error("Failed to get transactionid: " + transactionId);
            throw new Exception(processorRequestResponse.getMessage());
        }
        DoProcessorRequest doProcessorRequest = processorRequestResponse.getData();

        try {
            DoProcessorResponse doProcessorResponse = DoProcessorResponse.builder()
                    .rawRequestLog("Received " + (isSuccess ? "successful" : "failed") + " redirect from Trustly.")
                    .transactionId(transactionId)
                    .build();

            //log.debug("Sending request to service-cashier: " + doProcessorRequest.toString());
            //Response<DoResponse> response = callbackService.doSafeCallback(doProcessorResponse);
            //log.debug("Received response from service-cashier: " + response.toString());

            if (!doProcessorRequest.isTransactionFinalized() && Boolean.parseBoolean(doProcessorRequest.getProperty("pending_on_redirect"))) {
                return "redirect:" + doProcessorRequest.stageInputData(1).get("return_url") + "?status=pending&tnx_id=" + transactionId;
            } else {
                return "redirect:" + doProcessorRequest.stageInputData(1).get("return_url") + "?status=" + (isSuccess ? "success" : "failed&error=" + GeneralError.VERIFY_INPUT_DETAILS.getResponseMessageLocal(messageSource, doProcessorRequest.getUser().getDomain(), doProcessorRequest.getUser().getLanguage()));
            }
        } catch (Exception ex) {
            log.error("Failed to process redirect from trustly. TransactionId: " + transactionId, ex);
        }
        return "redirect:" + doProcessorRequest.stageInputData(1).get("return_url") + "?status=failed";
    }

    private String processTrustlyAccountRedirect(Long accountTransactionId, boolean isSuccess) throws Exception {
        lithium.service.cashier.client.objects.ProccesorAccountTransaction processorAccountTransaction = cashierService.getAccountProcessorTransaction(accountTransactionId);
        String redirectUrl =  "redirect:" + processorAccountTransaction.getRedirectUrl() + "?status=" + processorAccountTransaction.getState().toLowerCase() + "&tnx_id=" + processorAccountTransaction.getId();
        switch (ProcessorAccountTransactionState.fromName(processorAccountTransaction.getState())) {
            case CANCELED:
            case FAILED:
                redirectUrl += "&error=" + processorAccountTransaction.getGeneralError();
        }
        return redirectUrl;
    }
}
