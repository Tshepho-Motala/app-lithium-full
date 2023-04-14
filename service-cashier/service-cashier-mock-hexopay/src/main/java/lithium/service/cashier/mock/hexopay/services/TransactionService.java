package lithium.service.cashier.mock.hexopay.services;

import lithium.service.cashier.mock.hexopay.data.Scenario;
import lithium.service.cashier.mock.hexopay.data.entities.CreditCard;
import lithium.service.cashier.mock.hexopay.data.entities.Customer;
import lithium.service.cashier.mock.hexopay.data.entities.Transaction;
import lithium.service.cashier.mock.hexopay.data.entities.TransactionToken;
import lithium.service.cashier.mock.hexopay.data.exceptions.HexopayInvalidInputExeption;
import lithium.service.cashier.mock.hexopay.data.repositories.CreditCardRepository;
import lithium.service.cashier.mock.hexopay.data.repositories.CustomerRepository;
import lithium.service.cashier.mock.hexopay.data.repositories.TransactionRepository;
import lithium.service.cashier.mock.hexopay.data.repositories.TransactionTokenRepository;
import lithium.service.cashier.processor.hexopay.api.gateway.data.AvsCvcVerificationRequest;
import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.AvsVerificationCode;
import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.CvcVerificationCode;
import lithium.service.cashier.processor.hexopay.api.gateway.data.enums.Status;
import lithium.service.cashier.processor.hexopay.api.page.PaymentTokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TransactionService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private TransactionTokenRepository transactionTokenRepository;

    public Transaction createTransaction(String type, Long amount, String currency, Status status, String message, CreditCard creditCard,
                             String returnUrl, String notificationUrl, String trackingId, Scenario scenario, boolean is3dSecure, String expireAt,
                             String avsRejectCode, String cvcRejectCode, boolean avsCvcStatus) {

        Transaction transaction = Transaction.builder()
                .uid(getUUID())
                .trackingId(trackingId)
                .amount(amount)
                .currency(currency)
                .type(type)
                .card(creditCard)
                .status(status)
                .message(message)
                .returnUrl(returnUrl)
                .notificationUrl(notificationUrl)
                .scenario(scenario)
                .threeDSecure(is3dSecure)
                .ttl(status == Status.incomplete ? getTtl(expireAt, trackingId) : -1L)
                .avsReject(avsRejectCode)
                .cvcReject(cvcRejectCode)
                .avsCvcStatus(avsCvcStatus)
                .build();

        return transactionRepository.save(transaction);
    }

    private Long getTtl(String expireAt, String trackingId)  {
        try {
            if (expireAt == null || expireAt.isEmpty()) {
                return -1L;
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            Date date = dateFormat.parse(expireAt);
            if (date.before(new Date())) {
                throw new HexopayInvalidInputExeption("Invalid expire date");
            }
            return date.getTime();

        } catch (Exception ex) {
            log.error("Invalid expireAt date: " + expireAt + " trackingId/token" + trackingId);
            throw new HexopayInvalidInputExeption("Invalid expire date");
        }
    }

    public TransactionToken createTransactionToken(PaymentTokenRequest.Checkout checkout) {
        Customer customer = findCustomerOrCreate(checkout.getCustomer());

        String token = getUUID();
        TransactionToken transactionToken = TransactionToken.builder()
                .amount(checkout.getOrder().getAmount())
                .status(Status.error)
                .type(checkout.getTransactionType().name())
                .trackingId(checkout.getTrackingId())
                .token(token)
                .currency(checkout.getOrder().getCurrency())
                .type(checkout.getTransactionType().name())
                .customer(customer)
                .returnUrl(checkout.getSettings().getReturnUrl())
                .notificationUrl(checkout.getSettings().getNotificationUrl())
                .ttl(getTtl(checkout.getOrder().getExpiredAt(), token))
                .build();

        if (checkout.getOrder().getAdditionalData() != null && checkout.getOrder().getAdditionalData().getAvsCvcVerification() != null) {
            AvsCvcVerificationRequest avsCvcVerificationRequest = checkout.getOrder().getAdditionalData().getAvsCvcVerification();
            if (avsCvcVerificationRequest.getAvsVerification() != null && avsCvcVerificationRequest.getAvsVerification().getRejectCodes() != null) {
                transactionToken.setAvsRejectCodes(String.join(",", avsCvcVerificationRequest.getAvsVerification().getRejectCodes()));
            }
            if (avsCvcVerificationRequest.getCvcVerification() != null && avsCvcVerificationRequest.getCvcVerification().getRejectCodes() != null) {
                transactionToken.setCvcRejectCodes(String.join(",", avsCvcVerificationRequest.getCvcVerification().getRejectCodes()));
            }
        }

        return transactionTokenRepository.save(transactionToken);
    }

    public Transaction createTransactionFromToken(String token, Status status, String cardNo, Integer month, Integer year, String cvv, String nameOncard, String message,
                                                  boolean is3DSecure, Scenario scenario, Long amount, AvsVerificationCode avsCode, CvcVerificationCode cvcCode, boolean avsCvcStatus){

        TransactionToken transactionToken = transactionTokenRepository.findByToken(token);


        String stamp = Base64.getEncoder().encodeToString((cardNo + month + year).getBytes());

        CreditCard creditCard = creditCardRepository.findByCustomerAndStamp(transactionToken.getCustomer(),stamp);
        if (creditCard == null) {
            creditCard = creditCardRepository.save(CreditCard.builder()
                    .holder(nameOncard)
                    .stamp(Base64.getEncoder().encodeToString((cardNo + month + year).getBytes()))
                    .cvv(cvv)
                    .firstDigit(cardNo.substring(0, 1))
                    .lastFourDigits(cardNo.substring(cardNo.length() - 4, cardNo.length()))
                    .expMonth(month)
                    .expYear(year)
                    .customer(transactionToken.getCustomer())
                    .brand(getCardBrand(cardNo))
                    .token(getUUID())
                    .number(cardNo)
                    .secured(is3DSecure)
                    .build());
        }

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .status(status)
                .message(message)
                .type(transactionToken.getType())
                .trackingId(transactionToken.getTrackingId())
                .card(creditCard)
                .currency(transactionToken.getCurrency())
                .transactionToken(transactionToken)
                .uid(getUUID())
                .threeDSecure(is3DSecure)
                .returnUrl(transactionToken.getReturnUrl())
                .notificationUrl(transactionToken.getNotificationUrl())
                .scenario(scenario)
                .ttl(status == Status.incomplete ? transactionToken.getTtl() : -1L)
                .avsReject(avsCode.code())
                .cvcReject(cvcCode.code())
                .avsCvcStatus(avsCvcStatus)
                .build();

        transaction = transactionRepository.save(transaction);
        updateTransactionToken(transactionToken.getToken(), transaction.getStatus(), transaction.getTtl(), transaction);
        return transaction;
    }
    public TransactionToken updateTransactionToken(String token, Status status, Long ttl, Transaction transaction) {
        TransactionToken transactionToken = transactionTokenRepository.findByToken(token);
        boolean save = false;
        if (status != null && transactionToken.getStatus() != status){
            transactionToken.setStatus(status);
            save = true;
        }

        if (ttl != null && transactionToken.getTtl() != ttl) {
            transactionToken.setTtl(ttl);
            save = true;
        }

        if (transaction != null) {
            transactionToken.setTransaction(transaction);
            save = true;
        }

        return save ? transactionTokenRepository.save(transactionToken) : transactionToken;
    }

    public Transaction updateTransaction(String uid, Status status, String message, Long ttl) {
        Transaction transaction = transactionRepository.findByUid(uid);
        boolean save = false;
        if (status != null && transaction.getStatus() != status){
            transaction.setStatus(status);
            save = true;
        }

        if (message != null && !transaction.getMessage().equals(message)){
            transaction.setMessage(message);
            save = true;
        }

        if (ttl != null && transaction.getTtl() != ttl) {
            transaction.setTtl(ttl);
            save = true;
        }

        if (save && transaction.getTransactionToken() != null) {
            updateTransactionToken(transaction.getTransactionToken().getToken(), status, ttl, null);
        }

        return save ? transactionRepository.save(transaction) : transaction;
    }

    public CreditCard getCreaditCard(String token) {
        return creditCardRepository.findByToken(token);
    }

    public TransactionToken getTransactionToken(String token) {
        return transactionTokenRepository.findByToken(token);
    }

    public Transaction getTransactionByUid(String uid) {
        return transactionRepository.findByUid(uid);
    }

    public List<Transaction> getTransactionsByTrackingId(String trackingId) {
        return transactionRepository.findByTrackingId(trackingId);
    }

    public List<Transaction> getTransactionToExpire() {
        return transactionRepository.findByTtlNotAndStatus(-1L, Status.incomplete);
    }

    public List<TransactionToken> getTransactionTokenToExpire() {
        return transactionTokenRepository.findByTtlNotAndStatus(-1L, Status.error);
    }

    private Customer findCustomerOrCreate(lithium.service.cashier.processor.hexopay.api.page.data.Customer customer) {
        Customer customerEntity = customerRepository.findByEmail(customer.getEmail());
        if (customerEntity == null) {
            customerEntity = customerRepository.save(Customer.builder()
                    .email(customer.getEmail())
                    .firstName(customer.getFirstName())
                    .lastName(customer.getLastName())
                    .address(customer.getAddress())
                    .birthDate(customer.getBirthDate())
                    .city(customer.getCity())
                    .phone(customer.getPhone())
                    .country(customer.getCountry())
                    .state(customer.getState())
                    .zip(customer.getZip())
                    .build());
        }
        return customerEntity;
    }

    private String getCardBrand(String cardNo) {
        String brand;
        Integer firstDigit =  Integer.parseInt(cardNo.substring(0,1));
        switch (firstDigit) {
            case 4:
                brand = "visa";
                break;
            case 5:
                brand = "master";
                break;
            case 6:
                brand = "discover";
                break;
            default:
                brand = null;
                break;
        }
        return brand;
    }

    private String getUUID() {
        return "mock_" + UUID.randomUUID().toString();
    }
}
