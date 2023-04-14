package lithium.service.cashier.mock.inpay.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.mock.inpay.InpayConfiguration;
import lithium.service.cashier.mock.inpay.data.Scenario;
import lithium.service.cashier.mock.inpay.data.entities.InpayDebtorAccount;
import lithium.service.cashier.mock.inpay.data.entities.InpayTransaction;
import lithium.service.cashier.mock.inpay.data.exceptions.Status401UnauthorizedException;
import lithium.service.cashier.mock.inpay.data.exceptions.Status412PreconditionFailedException;
import lithium.service.cashier.mock.inpay.data.repositories.InpayDebtorAccountRepository;
import lithium.service.cashier.mock.inpay.data.repositories.InpayReasonRepository;
import lithium.service.cashier.mock.inpay.data.repositories.InpayTransactionRepository;
import lithium.service.cashier.processor.inpay.api.data.InpayRequestData;
import lithium.service.cashier.processor.inpay.api.data.InpayState;
import lithium.service.cashier.processor.inpay.api.data.InpayTransactionData;
import lithium.service.cashier.processor.inpay.services.InpayCryptoService;
import lithium.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@AllArgsConstructor
@Service
public class PaymentService {

    private final InpayConfiguration properties;
    private final InpayCryptoService cryptoService;
    private final ObjectMapper mapper;
    private final InpayTransactionRepository transactionRepository;
    private final SimulatorService simulator;
    private final InpayDebtorAccountRepository debtorAccountRepository;
    private final InpayReasonRepository reasonRepository;

    public String createPayment(String authorization, String xAuthUuid, String xRequestId, String body) throws Exception {
        String xRequestIdAuth = checkAuthAndGetXRequestId(authorization, xAuthUuid, xRequestId);
        String decryptedBody = cryptoService.decryptAndVerifyResponse(properties.getMerchantPrivateKey(), properties.getInpayCaChain(), body);
        InpayRequestData inpayRequestData = mapper.readValue(decryptedBody, InpayRequestData.class);
        String birthDate = inpayRequestData.getCreditor().getBirthDate();
        String encryptedResponse = "";
        String jsonTransactionData = "";
        if (!StringUtil.isEmpty(birthDate)){
            boolean showNotification = Scenario.getByAmount(new BigDecimal(inpayRequestData.getAmount()).movePointRight(2).longValue()) != Scenario.NO_NOTIFICATION;
            InpayTransactionData transactionData = simulator.simulateReceived(inpayRequestData, xRequestIdAuth, showNotification);
            jsonTransactionData = mapper.writeValueAsString(transactionData);
            encryptedResponse = cryptoService.signAndEncryptRequest(properties.getMerchantPrivateKey(), properties.getMerchantCertificate(), properties.getInpayCertificate(), jsonTransactionData);
            simulator.simulatePayment(transactionData);
        } else {
            jsonTransactionData = rejectEmptyBirthdate(inpayRequestData, xRequestId);
            encryptedResponse = cryptoService.signAndEncryptRequest(properties.getMerchantPrivateKey(), properties.getMerchantCertificate(), properties.getInpayCertificate(), jsonTransactionData);
        }
        return encryptedResponse;
    }


    public String getPaymentStatus(String authorization, String xAuthUuid, String inpayUniqueReference) throws Exception {
        checkAuth(authorization, xAuthUuid);
        InpayTransaction transaction = transactionRepository.findTransactionByInpayUniqueReference(inpayUniqueReference);
        String jsonTransactionData = mapper.writeValueAsString(simulator.mapTransaction(transaction));
        String encryptedResponse = cryptoService.signAndEncryptRequest(properties.getMerchantPrivateKey(), properties.getMerchantCertificate(), properties.getInpayCertificate(), jsonTransactionData);
        return encryptedResponse;
    }

    private String checkAuthAndGetXRequestId(String authorization, String xAuthUuid, String xRequestId) throws Exception {
        checkAuth(authorization, xAuthUuid);
        if (transactionRepository.findByxRequestId(xRequestId) != null)
            throw new Status412PreconditionFailedException("Duplicate X-Request-Id header");
        return xRequestId;
    }

    private void checkAuth(String authorization, String xAuthUuid) throws Exception {
        if (!properties.getAuthorization().equals(authorization) || !properties.getXAuthUuid().equals(xAuthUuid))
            throw new Status401UnauthorizedException("Unauthorized");
    }

    private String rejectEmptyBirthdate(InpayRequestData inpayRequestData, String xRequestId) throws Exception {
        InpayDebtorAccount debtorAccount = debtorAccountRepository.findBySchemeName(inpayRequestData.getDebtorAccount().getSchemeName()).orElseGet(() -> InpayDebtorAccount.builder().debtorAccountId(inpayRequestData.getDebtorAccount().getId()).schemeName(inpayRequestData.getDebtorAccount().getSchemeName()).build());
        debtorAccountRepository.save(debtorAccount);
        InpayTransaction transaction = InpayTransaction.builder()
                .debtorAccount(debtorAccount)
                .endToEndId(inpayRequestData.getEndToEndId())
                .inpayUniqueReference(RandomStringUtils.random(7, true, true).toUpperCase())
                .amount(new BigDecimal(inpayRequestData.getAmount()).movePointRight(2).longValue())
                .currency(inpayRequestData.getCurrencyCode())
                .timestamp(new Date())
                .state(InpayState.REJECTED.getStatus())
                .reasons(Arrays.asList(reasonRepository.findByCode("birthdate:[can't be blank]")))
                .xRequestId(xRequestId)
                .build();
        InpayTransactionData transactionData = simulator.mapTransaction(transaction);
        return mapper.writeValueAsString(transactionData);
    }

}
