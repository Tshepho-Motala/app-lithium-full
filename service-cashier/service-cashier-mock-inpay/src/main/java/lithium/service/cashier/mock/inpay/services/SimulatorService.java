package lithium.service.cashier.mock.inpay.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.mock.inpay.InpayConfiguration;
import lithium.service.cashier.mock.inpay.data.Scenario;
import lithium.service.cashier.mock.inpay.data.entities.InpayDebtorAccount;
import lithium.service.cashier.mock.inpay.data.entities.InpayReason;
import lithium.service.cashier.mock.inpay.data.entities.InpayTransaction;
import lithium.service.cashier.mock.inpay.data.repositories.InpayDebtorAccountRepository;
import lithium.service.cashier.mock.inpay.data.repositories.InpayReasonRepository;
import lithium.service.cashier.mock.inpay.data.repositories.InpayTransactionRepository;
import lithium.service.cashier.processor.inpay.api.data.InpayRequestData;
import lithium.service.cashier.processor.inpay.api.data.InpayState;
import lithium.service.cashier.processor.inpay.api.data.InpayTransactionData;
import lithium.service.cashier.processor.inpay.api.data.InpayWebhookData;
import lithium.service.cashier.processor.inpay.api.data.InpayWebhookDataV2;
import lithium.service.cashier.processor.inpay.services.InpayCryptoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;


@Slf4j
@AllArgsConstructor
@Service
public class SimulatorService {


    private final InpayConfiguration properties;
    private final InpayTransactionRepository transactionRepository;
    private final InpayDebtorAccountRepository debtorAccountRepository;
    private final InpayReasonRepository reasonRepository;
    private final NotificationService notificationService;
    private final TaskScheduler taskScheduler;
	private final InpayCryptoService cryptoService;
	private final ObjectMapper mapper;

    public void simulatePayment(InpayTransactionData inpayTransactionData) {
        Scenario scenario = Scenario.getByAmount(new BigDecimal(inpayTransactionData.getAmount()).movePointRight(2).longValue());
        Long delay = Long.parseLong(properties.getDelayBetweenTransactionSteps());
        switch (scenario) {
            case COMPLETED:
                scheduleStep(inpayTransactionData, InpayState.PROCESSING.getStatus(), delay, true);
                scheduleStep(inpayTransactionData, InpayState.COMPLETED.getStatus(), delay * 2, true);
                break;

            case REJECTED:
                scheduleStep(inpayTransactionData, InpayState.REJECTED.getStatus(), delay, true);
                break;

            case RETURNED:
                scheduleStep(inpayTransactionData, InpayState.PROCESSING.getStatus(), delay, true);
                scheduleStep(inpayTransactionData, InpayState.COMPLETED.getStatus(), delay * 2, true);
                scheduleStep(inpayTransactionData, InpayState.RETURNED.getStatus(), delay * 3, true);
                break;

            case PENDING:
                inpayTransactionData.setState(InpayState.PENDING.getStatus());
                scheduleStep(inpayTransactionData, InpayState.PENDING.getStatus(), delay, true);
                break;

            case NO_NOTIFICATION:
                scheduleStep(inpayTransactionData, InpayState.PROCESSING.getStatus(), delay, false);
                scheduleStep(inpayTransactionData, InpayState.COMPLETED.getStatus(), delay, false);
                break;

            case COMPLETED_NO_DELAY:
                scheduleStep(inpayTransactionData, InpayState.PROCESSING.getStatus(), 0L, true);
                scheduleStep(inpayTransactionData, InpayState.COMPLETED.getStatus(), 0L, true);
                break;

            case INCORRECT_FINAL_AMOUNT:
                InpayTransaction transaction = transactionRepository.findTransactionByInpayUniqueReference(inpayTransactionData.getInpayUniqueReference());
                transaction.setAmount(transaction.getAmount() - 1L);
                transactionRepository.save(transaction);
                InpayTransactionData incorrectAmountTransaction = mapTransaction(transaction);
                scheduleStep(incorrectAmountTransaction, InpayState.PROCESSING.getStatus(), delay, true);
                scheduleStep(incorrectAmountTransaction, InpayState.COMPLETED.getStatus(), delay * 2, true);
                break;
        }
    }

    public InpayTransactionData simulateReceived(InpayRequestData inpayRequestData, String xRequestId, boolean showNotification) throws Exception {
        InpayDebtorAccount debtorAccount = debtorAccountRepository.findBySchemeName(inpayRequestData.getDebtorAccount().getSchemeName())
                .orElseGet(() -> InpayDebtorAccount.builder()
                        .debtorAccountId(inpayRequestData.getDebtorAccount().getId())
                        .schemeName(inpayRequestData.getDebtorAccount().getSchemeName())
                        .build());
        debtorAccountRepository.save(debtorAccount);

        InpayTransaction transaction = InpayTransaction.builder()
                .debtorAccount(debtorAccount)
                .endToEndId(inpayRequestData.getEndToEndId())
                .inpayUniqueReference(RandomStringUtils.random(7, true, true).toUpperCase())
                .amount(new BigDecimal(inpayRequestData.getAmount()).movePointRight(2).longValue())
                .currency(inpayRequestData.getCurrencyCode())
                .timestamp(new Date())
                .state(InpayState.RECEIVED.getStatus())
                .xRequestId(xRequestId)
                .build();

        transactionRepository.save(transaction);

        InpayTransactionData transactionData = mapTransaction(transaction);

        if (showNotification) {
            if (isV2Webhook(transaction)) {
                String encryptedResponse = buildEncryptedWebhookV2Data(transactionData, InpayState.RECEIVED.getStatus());
                notificationService.callWebhookV2(encryptedResponse);
            } else {
                InpayWebhookData webhookData = buildWebhookData(transactionData, InpayState.RECEIVED.getStatus());
                notificationService.callWebhook(webhookData);
            }
        }
        return transactionData;
    }

    private boolean isV2Webhook(InpayTransaction transaction) {
        return transaction.getAmount() % 100 > 0;
    }

	private InpayTransactionData simulateTransactionStep(InpayTransactionData inpayTransactionData, String newStatus, boolean showNotification) throws Exception {
        log.info("simulate transaction step: " + inpayTransactionData + " \nnew status: " + newStatus);
        InpayTransaction transaction = transactionRepository.findTransactionByInpayUniqueReference(inpayTransactionData.getInpayUniqueReference());
        transaction.setState(newStatus);
        inpayTransactionData.setState(newStatus);
        transaction = appendReason(transaction);
        if (showNotification) {
            if (isV2Webhook(transaction)) {
                String encryptedResponse = buildEncryptedWebhookV2Data(inpayTransactionData, newStatus);
                notificationService.callWebhookV2(encryptedResponse);
            } else {
                InpayWebhookData webhookData = buildWebhookData(inpayTransactionData, newStatus);
                notificationService.callWebhook(webhookData);
            }
        }
        return mapTransaction(transaction);
    }

    public InpayWebhookData buildWebhookData(InpayTransactionData transactionData, String status) {
        return InpayWebhookData.builder()
                .api_version(properties.getApiVersion())
                .bank_owner_name(properties.getBankOwnerName())
                .checksum(generateChecksum(transactionData))
                .invoice_amount(transactionData.getAmount())
                .invoice_currency(transactionData.getCurrency())
                .invoice_reference(transactionData.getInpayUniqueReference())
                .invoice_status(status)
                .invoice_updated_at(new Date().toString())
                .merchant_id(properties.getMerchantId())
                .order_id(transactionData.getEndToEndId())
                .received_sum(transactionData.getAmount())
                .build();
    }

	private String buildEncryptedWebhookV2Data(InpayTransactionData transactionData, String status) throws Exception {
		InpayWebhookDataV2 webhookData = buildWebhookDataV2(transactionData, status);
		String jsonWebhookData = mapper.writeValueAsString(webhookData);
		return cryptoService.signAndEncryptRequest(properties.getMerchantPrivateKey(), properties.getMerchantCertificate(), properties.getInpayCertificate(), jsonWebhookData);
	}

	public InpayWebhookDataV2 buildWebhookDataV2(InpayTransactionData transactionData, String status) {
		return InpayWebhookDataV2.builder()
				.debtorAccount(transactionData.getDebtorAccount())
				.endToEndId(transactionData.getEndToEndId())
				.inpayUniqueReference(transactionData.getInpayUniqueReference())
				.amount(transactionData.getAmount())
				.currency(transactionData.getCurrency())
				.timestamp(transactionData.getTimestamp())
				.status(status)
				.reasons(transactionData.getReasons())
				.build();
	}

    private String generateChecksum(InpayTransactionData transactionData) {
        byte[] bytes = transactionData.toString().getBytes();
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue() + "";
    }

    public void scheduleStep(InpayTransactionData inpayTransactionData, String status, Long delay, boolean showNotification) {
        taskScheduler.schedule(() ->
        {
            try {
                simulateTransactionStep(inpayTransactionData, status, showNotification);
            } catch (Exception ex) {
                log.error(ex.toString());
            }
        }, new Date(System.currentTimeMillis() + delay));
    }

    public InpayTransactionData mapTransaction(InpayTransaction transaction) {
        return InpayTransactionData.builder()
                .debtorAccount(mapDebtorAccount(transaction.getDebtorAccount()))
                .endToEndId(transaction.getEndToEndId())
                .inpayUniqueReference(transaction.getInpayUniqueReference())
                .amount(new BigDecimal(transaction.getAmount()).movePointLeft(2).toString())
                .currency(transaction.getCurrency())
                .timestamp(transaction.getTimestamp().toString())
                .state(transaction.getState())
                .reasons(mapReasons(transaction.getReasons()))
                .build();
    }

    private lithium.service.cashier.processor.inpay.api.data.InpayDebtorAccount mapDebtorAccount(InpayDebtorAccount debtorAccount) {
        return lithium.service.cashier.processor.inpay.api.data.InpayDebtorAccount.builder()
                .id(debtorAccount.getDebtorAccountId())
                .schemeName(debtorAccount.getSchemeName())
                .build();
    }

    private List<lithium.service.cashier.processor.inpay.api.data.InpayReason> mapReasons(List<InpayReason> reasons) {
        List<lithium.service.cashier.processor.inpay.api.data.InpayReason> dtoReasons = new ArrayList<>();
        for (InpayReason reason : reasons) {
            lithium.service.cashier.processor.inpay.api.data.InpayReason dtoReason = lithium.service.cashier.processor.inpay.api.data.InpayReason.builder()
                    .category(reason.getCategory())
                    .code(reason.getCode())
                    .message(reason.getMessage())
                    .build();
            dtoReasons.add(dtoReason);
        }
        return dtoReasons;
    }

    private InpayTransaction appendReason(InpayTransaction transaction) { //adding sample reason for each not completed final transaction state
        if (InpayState.REJECTED.getStatus().equals(transaction.getState())) {
            transaction.addReason(reasonRepository.findByCode("Rejected - Invalid creditor account"));
        } else if (InpayState.RETURNED.getStatus().equals(transaction.getState())) {
            transaction.addReason(reasonRepository.findByCode("Returned - Returned payment as per beneficiary request"));
        } else if (InpayState.PENDING.getStatus().equals(transaction.getState())) {
            transaction.addReason(reasonRepository.findByCode("Payment will be executed on YYYY-MM-DD"));
        } 
        transactionRepository.save(transaction);
        return transaction;
    }

}
