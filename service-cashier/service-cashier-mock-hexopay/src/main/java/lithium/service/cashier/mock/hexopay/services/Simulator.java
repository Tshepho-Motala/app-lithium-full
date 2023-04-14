package lithium.service.cashier.mock.hexopay.services;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.mock.hexopay.data.Scenario;
import lithium.service.cashier.mock.hexopay.data.entities.TransactionToken;
import lithium.service.cashier.mock.hexopay.data.exceptions.HexopayInvalidInputExeption;
import lithium.service.cashier.mock.hexopay.data.exceptions.HexopayInvalidInputPageExeption;
import lithium.service.cashier.mock.hexopay.data.exceptions.HexopayMockException;
import lithium.service.cashier.mock.hexopay.data.objects.WidgetModel;
import lithium.service.cashier.processor.hexopay.api.gateway.PaymentRequest;
import lithium.service.cashier.processor.hexopay.api.gateway.PayoutRequest;
import lithium.service.cashier.processor.hexopay.api.gateway.data.AvsCvcVerificationRequest;
import lithium.service.cashier.processor.hexopay.api.gateway.data.AvsCvcVerificationResponse;
import lithium.service.cashier.processor.hexopay.api.gateway.data.BeProtectedResult;
import lithium.service.cashier.processor.hexopay.api.gateway.data.BillingAddress;
import lithium.service.cashier.processor.hexopay.api.gateway.data.Customer;
import lithium.service.cashier.processor.hexopay.api.gateway.data.Transaction;
import lithium.service.cashier.processor.hexopay.api.gateway.data.CreditCard;
import lithium.service.cashier.processor.hexopay.api.gateway.data.VerificationRequest;
import lithium.service.cashier.processor.hexopay.api.gateway.data.VerificationResult;
import lithium.service.cashier.processor.hexopay.api.gateway.data.WhiteBlackList;
import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.AvsVerificationCode;
import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.CvcVerificationCode;
import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.Status;
import lithium.service.cashier.processor.hexopay.api.page.PaymentTokenRequest;
import lithium.service.cashier.processor.hexopay.api.page.PaymentTokenResponse;
import lithium.service.cashier.processor.hexopay.api.page.data.Checkout;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class Simulator {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    LithiumConfigurationProperties config;
    @Autowired
    NotificationService notificationService;
    @Autowired
    ExpirationService expirationService;

    @Value("${spring.application.name}")
    private String moduleName;


    public PaymentTokenResponse simulateCreateTransactionToken(PaymentTokenRequest tokenRequest) throws HexopayMockException {
        Scenario scenario = getScenario(tokenRequest.getCheckout().getOrder().getAmount());
        switch (scenario) {
            case ERROR_RESPONSE:
                throw new HexopayInvalidInputPageExeption("Invalid input data.");
            default:
                break;
        }

        TransactionToken transactionToken = transactionService.createTransactionToken(tokenRequest.getCheckout());

        PaymentTokenResponse response = PaymentTokenResponse.builder()
                .checkout(Checkout.builder()
                        .redirectUrl(getRedirectUrl() + "/widget?token=" + transactionToken.getToken())
                        .token(transactionToken.getToken())
                        .build())
                .build();
        return response;
    }

    public String simulateWidget(String token, String nameOncard, String cardNo, Integer month,
                                 Integer year, String cvv, boolean is3DSecure, Scenario scenario) throws Exception {
        lithium.service.cashier.mock.hexopay.data.entities.TransactionToken transactionToken = transactionService.getTransactionToken(token);

        if (transactionToken == null) {
            throw new HexopayInvalidInputExeption("Invalid token");
        } else if (transactionToken.getStatus() == Status.expired) {
            return transactionToken.getReturnUrl();
        }

        lithium.service.cashier.mock.hexopay.data.entities.Transaction transactionEntity = transactionToken.getTransaction();

        if (transactionEntity == null) {
            Status status = is3DSecure ? Status.incomplete : Status.successful;
            String message = "Successfully processed";
            Long amount = transactionToken.getAmount();
            AvsVerificationCode avsVerificationCode = AvsVerificationCode.AVS_NOT_SUPPORTED;
            CvcVerificationCode cvcVerificationCode = CvcVerificationCode.NO_CVC_CHECK;
            switch (scenario) {
                case FAILED:
                    status = is3DSecure ? Status.incomplete : Status.failed;
                    message = "Payment is declined";
                    break;
                case INCORRECT_AMOUNT:
                    amount--;
                case AVS_POSTAL_CODE_UNMATCH:
                case AVS_ADDRESS_AND_POSTAL_CODE_UNMATCH:
                case AVS_ADDRESS_AND_POSTAL_CODE_MATCH:
                case AVS_ADDRESS_UNMATCH:
                case AVS_ADDRESS_UNAVAILABLE:
                case AVS_NOT_VERIFIED_SYSTEM_ERROR:
                case AVS_RESULT_UNKNOWN:
                    avsVerificationCode = AvsVerificationCode.fromCode(scenario.getData());
                    break;
                case CVC_MATCH:
                case CVC_UNMATCH:
                case CVC_NOT_SUPPORTED_BY_BANK:
                case CVC_NOT_VERIFIED_SYSTEM_ERROR:
                case CVC_RESULT_UNKNOWN:
                    cvcVerificationCode = CvcVerificationCode.fromCode(scenario.getData());
                    break;
                case BE_PROTECTED_EMAIL:
                case BE_PROTECTED_IP:
                case BE_PROTECTED_CARD:
                case BE_PROTECTED_DUPLICATE_ACCOUNT:
                    status = Status.failed;
                    message = scenario.getDescription();
                    break;
                case SUCCESS:
                default:
                    break;
            }

            if (cvcVerificationCode == CvcVerificationCode.NO_CVC_CHECK) {
                cvcVerificationCode = simulateCvcVerification(cvv);
            }

            boolean avsCheckResult = checkAvsCvc(avsVerificationCode, cvcVerificationCode, transactionToken.getAvsRejectCodes(), transactionToken.getAvsRejectCodes());
            if (!avsCheckResult && status != Status.failed) {
                status = Status.failed;
                message = "AVS/CVC verification failed";
            }

            transactionEntity = transactionService.createTransactionFromToken(token, status, cardNo, month, year, cvv, nameOncard, message,
                                                                                is3DSecure, scenario, amount, avsVerificationCode, cvcVerificationCode,avsCheckResult);
        }

        if (transactionEntity.getStatus() == Status.incomplete) {
            return getThreeDSecureUrl(transactionEntity.getUid());
        } else {
            notificationService.notify(mapTransaction(transactionEntity), transactionEntity.getNotificationUrl(), scenario);
            return getReturnUrl(transactionEntity);
        }
    }

    public String simulateThreeDSecure(String uid) throws Exception {
        lithium.service.cashier.mock.hexopay.data.entities.Transaction transaction = transactionService.getTransactionByUid(uid);

        if (transaction == null) {
            throw new HexopayInvalidInputExeption("Invalid transaction uid");
        } else if (transaction.getStatus() == Status.incomplete) {
            Status status = Status.successful;
            String message = "Successfully processed";
            switch (transaction.getScenario()) {
                case FAILED:
                    status = Status.failed;
                    message = "Payment declined";
                    break;
                case SUCCESS:
                default:
                    break;
            }
            transaction = transactionService.updateTransaction(transaction.getUid(), status, message, -1L);
            notificationService.notify(mapTransaction(transaction), transaction.getNotificationUrl(), transaction.getScenario());
        }

        return getReturnUrl(transaction);
    }

    public Transaction simulatePayment(PaymentRequest.Request payment) throws HexopayMockException {
        lithium.service.cashier.mock.hexopay.data.entities.CreditCard cardEntity = transactionService.getCreaditCard(payment.getCreditCard().getToken());
        if (cardEntity == null) {
            throw new HexopayInvalidInputExeption("Token does not exist.");
        }

        Scenario scenario = getScenario(payment.getAmount());
        boolean is3dSecure = cardEntity.isSecured() && !payment.getCreditCard().isSkipThreeD();
        Status status = is3dSecure ? Status.incomplete : Status.successful;
        String message = "Successfully processed";
        switch (scenario) {
            case ERROR_RESPONSE:
                throw new HexopayInvalidInputExeption("Invalid input data.");
            case FAILED:
                status = is3dSecure ? Status.incomplete : Status.failed;
                message = "Payment declined";
                break;
            case INCORRECT_AMOUNT:
                payment.setAmount(payment.getAmount() - 1);
                break;
            case BE_PROTECTED_EMAIL:
            case BE_PROTECTED_IP:
            case BE_PROTECTED_CARD:
            case BE_PROTECTED_DUPLICATE_ACCOUNT:
                status = Status.failed;
                message = scenario.getDescription();
                break;
            case SUCCESS:
            default:
                break;
        }

        AvsVerificationCode avsVerificationCode = simulateAvsVerification(payment.getBillingAddress() != null ? payment.getBillingAddress().getAddress() : null);
        CvcVerificationCode cvcVerificationCode = simulateCvcVerification(payment.getCreditCard().getCvv());
        boolean avsCheckResult = checkAvsCvc(avsVerificationCode, cvcVerificationCode, payment.getAdditionData().getAvsCvcVerification());

        if (!avsCheckResult && status != Status.failed) {
            status = Status.failed;
            message = "AVS/CVC verification failed";
        }

        Transaction transaction = mapTransaction(transactionService.createTransaction("payment", payment.getAmount(), payment.getCurrency(), status, message, cardEntity, payment.getReturnUrl(),
                payment.getNotificationUrl(), payment.getTrackingId(), scenario, is3dSecure, payment.getExpiredAt(), avsVerificationCode.code(), cvcVerificationCode.code(), !avsCheckResult));
        if (status != Status.incomplete) {
            notificationService.notify(transaction, payment.getNotificationUrl(), scenario);
        }
        return transaction;

    }

    public Transaction simulatePayout(PayoutRequest.Request payout) throws HexopayMockException {
        lithium.service.cashier.mock.hexopay.data.entities.CreditCard cardEntity = transactionService.getCreaditCard(payout.getCreditCard().getToken());
        if (cardEntity == null) {
            throw new HexopayInvalidInputExeption("Token does not exist.");
        }
        //avs check
        Scenario scenario = getScenario(payout.getAmount());
        Status status = Status.successful;
        String message = "Successfully processed";

        switch (scenario) {
            case ERROR_RESPONSE:
                throw new HexopayInvalidInputExeption("Invalid input data.");
            case FAILED:
                status = Status.failed;
                message = "Payout is declined";
                break;
            case INCORRECT_AMOUNT:
                payout.setAmount(payout.getAmount() + 1);
                break;
            case BE_PROTECTED_EMAIL:
            case BE_PROTECTED_IP:
            case BE_PROTECTED_CARD:
            case BE_PROTECTED_DUPLICATE_ACCOUNT:
                status = Status.failed;
                message = scenario.getDescription();
                break;
            case SUCCESS:
            default:
                break;
        }

        Transaction transaction = mapTransaction(transactionService.createTransaction("payout", payout.getAmount(), payout.getCurrency(), status, message, cardEntity, null, payout.getNotificationUrl(), payout.getTrackingId(), scenario, false, null, null,null, true));
        notificationService.notify(transaction, payout.getNotificationUrl(), scenario);
        return transaction;
    }

    public Transaction[] getTransactionsByTrackingId(String trackingId) {
        List<lithium.service.cashier.mock.hexopay.data.entities.Transaction> transactionEntityList = transactionService.getTransactionsByTrackingId(trackingId);
        //add simulation here according to scenario
        return transactionEntityList.stream().map(this::mapTransaction).toArray(size -> new Transaction[size]);
    }

    public Transaction getTransactionsByUid(String uid) {
        lithium.service.cashier.mock.hexopay.data.entities.Transaction transactionEntity = transactionService.getTransactionByUid(uid);
        return mapTransaction(transactionEntity);
    }

    public WidgetModel getWidgetInitData(String token) throws Exception {

        lithium.service.cashier.mock.hexopay.data.entities.TransactionToken transactionToken = transactionService.getTransactionToken(token);
        if (transactionToken == null) {
            throw new HexopayInvalidInputExeption("Token is invalid");
        }

        WidgetModel data = WidgetModel.builder()
                .amount(new BigDecimal(transactionToken.getAmount()).movePointLeft(2).toString())
                .currency(transactionToken.getCurrency())
                .token(transactionToken.getToken())
                .build();

        if (transactionToken.getTransaction() != null) {
            data.setReturnUrl(getReturnUrl(transactionToken.getTransaction()));
        }

        return data;
    }

    private boolean checkAvsCvc(AvsVerificationCode avsVerificationCode, CvcVerificationCode cvcVerificationCode, AvsCvcVerificationRequest avsCvcVerificationRequest) {
        if (avsCvcVerificationRequest != null) {
            if (avsCvcVerificationRequest.getAvsVerification() != null) {
                String[] rejectCodes = avsCvcVerificationRequest.getAvsVerification().getRejectCodes();
                if (rejectCodes != null && Arrays.stream(rejectCodes).anyMatch(c -> c.equals(avsVerificationCode.code()))) {
                    return false;
                }
            }

            if (avsCvcVerificationRequest.getCvcVerification() != null) {
                String[] rejectCodes = avsCvcVerificationRequest.getCvcVerification().getRejectCodes();
                if (rejectCodes != null && Arrays.stream(rejectCodes).anyMatch(c -> c.equals(cvcVerificationCode.code()))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkAvsCvc(AvsVerificationCode avsVerificationCode, CvcVerificationCode cvcVerificationCode, String avsRejectCodes, String cvcRejectCodes) {
        if (avsRejectCodes != null && Arrays.stream(avsRejectCodes.split(",")).anyMatch(c -> c.equals(avsVerificationCode.code()))) {
            return false;
        }
        if (cvcRejectCodes != null && Arrays.stream(cvcRejectCodes.split(",")).anyMatch(c -> c.equals(cvcVerificationCode.code()))) {
            return false;
        }
        return true;
    }

    private AvsVerificationCode simulateAvsVerification(String address) {
        if (address.startsWith("Test ")) {
            String code = address.substring(5).trim();
            AvsVerificationCode avsVerificationCode = AvsVerificationCode.fromCode(code);
            return avsVerificationCode != null ? avsVerificationCode : AvsVerificationCode.AVS_NOT_SUPPORTED;
        } else {
            return AvsVerificationCode.AVS_NOT_SUPPORTED;
        }
    }

    private CvcVerificationCode simulateCvcVerification(String cvc) {
        switch (cvc) {
            case "111":
                return CvcVerificationCode.CVC_UNMATCH;
            case "222":
                return CvcVerificationCode.CVC_MATCH;
            case "333":
                return CvcVerificationCode.CVC_NOT_SUPPORTED_BY_BANK;
            case "444":
                return CvcVerificationCode.NOT_VERIFIED_SYSTEM_ERROR;
            case "555":
                return CvcVerificationCode.CVC_RESULT_UNKNOWN;
            default:
                return CvcVerificationCode.NO_CVC_CHECK;
        }
    }

    public Transaction mapTransaction(lithium.service.cashier.mock.hexopay.data.entities.Transaction transactionEntity) {
        Transaction transaction = Transaction.builder()
                        .uid(transactionEntity.getUid())
                        .status(transactionEntity.getStatus().toString())
                        .amount(transactionEntity.getAmount())
                        .currency(transactionEntity.getCurrency())
                        .description("Livescore mock")
                        .type(transactionEntity.getType())
                        .paymentMethodType("credit_card")
                        .trackingId(transactionEntity.getTrackingId())
                        .message(transactionEntity.getMessage())
                        .test(true)
                        .createdAt(transactionEntity.getCreatedAt().toString())
                        //.expiredAt()
                        .language("en")
                        .customer(mapCustomer(transactionEntity.getCard().getCustomer()))
                        .beProtected(mapBeProtectedResult(transactionEntity.getScenario()))
                        .build();

        if (transactionEntity.isThreeDSecure()) {
            transaction.setRedirectUrl(getThreeDSecureUrl(transactionEntity.getUid()));
        }
        if (transactionEntity.getType().equals("payment")) {
            transaction.setBillingAddress(mapBillingAddress(transactionEntity.getCard().getCustomer()));
            transaction.setCreditCard(mapCreditCard(transactionEntity.getCard()));
            transaction.setAvsCvcVerification(mapAvsCvcVerification(transactionEntity.getAvsReject(),transactionEntity.getCvcReject()));
        } else if (transactionEntity.getType().equals("payout")) {
            transaction.setRecipientBillingAddress(mapBillingAddress(transactionEntity.getCard().getCustomer()));
            transaction.setRecipientCreditCard(mapCreditCard(transactionEntity.getCard()));
        }
        return transaction;
    }

    private BeProtectedResult mapBeProtectedResult(Scenario scenario) {
        BeProtectedResult beProtectedResult = BeProtectedResult.builder()
                .status("successful")
                .whiteBlackList(WhiteBlackList.builder().build())
                .build();
        switch (scenario) {
            case BE_PROTECTED_EMAIL:
                beProtectedResult.getWhiteBlackList().setEmail("black");
            case BE_PROTECTED_IP:
                beProtectedResult.getWhiteBlackList().setIp("black");
            case BE_PROTECTED_CARD:
                beProtectedResult.getWhiteBlackList().setCard_number("black");
            case BE_PROTECTED_DUPLICATE_ACCOUNT:
                beProtectedResult.setStatus("failed");
                beProtectedResult.setMessage(scenario.getDescription());
                break;
            default:
                break;
        }
        return beProtectedResult;
    }

    private AvsCvcVerificationResponse mapAvsCvcVerification(String avsRejectCode, String cvcRejectCode) {
        return AvsCvcVerificationResponse.builder()
                .avsVerification(VerificationResult.builder().resultCode(avsRejectCode).build())
                .cvcVerification(VerificationResult.builder().resultCode(cvcRejectCode).build())
                .build();
    }

    private CreditCard mapCreditCard(lithium.service.cashier.mock.hexopay.data.entities.CreditCard creditCard) {
        return CreditCard.builder()
                .token(creditCard.getToken())
                .stamp(creditCard.getStamp())
                .brand(creditCard.getBrand())
                .last4Digits(creditCard.getLastFourDigits())
                .firstDigit(creditCard.getFirstDigit())
                .expMonth(creditCard.getExpMonth())
                .expYear(creditCard.getExpYear())
                .holder(creditCard.getHolder())
                .bin(creditCard.getLastFourDigits())
                .issuerName("TEST BANK")
                .build();
    }

    private Customer mapCustomer(lithium.service.cashier.mock.hexopay.data.entities.Customer customer) {
        return Customer.builder()
                .email(customer.getEmail())
                .birthDate(customer.getBirthDate())
                .ip("127.0.0.1")
                .build();
    }
    private BillingAddress mapBillingAddress(lithium.service.cashier.mock.hexopay.data.entities.Customer customer) {
        return BillingAddress.builder()
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .address(customer.getAddress())
                .city(customer.getCity())
                .phone(customer.getPhone())
                .country(customer.getCountry())
                .state(customer.getState())
                .zip(customer.getZip())
                .build();
    }

    private String getThreeDSecureUrl(String uid) {
        return getRedirectUrl() + "/widget/3DSecure?uid=" + uid;
    }

    private String getReturnUrl(lithium.service.cashier.mock.hexopay.data.entities.Transaction transactionEntity) throws Exception {
        String redirectUrl = transactionEntity.getReturnUrl();
        URI uri = new URI(redirectUrl);
        String query = uri.getQuery();
        redirectUrl += query == null ? "?" : "&";
        return redirectUrl + "uid=" + transactionEntity.getUid() + "&status=" + transactionEntity.getStatus().name();
    }

    private Scenario getScenario(Long amount) {
        return Scenario.getByAmount(amount);

    }

    private String getRedirectUrl() {
        return config.getGatewayPublicUrl() + "/" + moduleName;
    }
}
