package lithium.service.cashier.mock.hexopay.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.cashier.mock.hexopay.data.Scenario;
import lithium.service.cashier.mock.hexopay.data.exceptions.InvalidNotificationResponseException;
import lithium.service.cashier.processor.hexopay.api.gateway.TransactionResponse;
import lithium.service.cashier.processor.hexopay.api.gateway.data.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Service
@Slf4j
public class NotificationService {
    private final String FAILED_SIGNATURE = "c9qRkRq/KVexlr5jVbSBAb3S/yPZUQ4A1NydzdWlv4jTjISUbnY7smW4NeKvTL2/r4ng4X0ex3tPqwq9IQ2KEoXpJgHc28aGoemmigGVqXNmwsmTH4GSf5a4lW8JieFVzP/1YOg2WRGbQyubM8zroDqnQqWM/Hh0PP/RKyp/bEIZRtmohAmelK+JXi6BLkNffLyKlHvcutzRdUXaboCFQ22bMhdzlLLy9xucahewAi25T9MZ95UDuojYP6ryuooUJhQMclxCqc+eXH9nHOpUjWgRhji4G3qb+10i5Jocdabxnx76vH+w4S8CGhkSh607Q/SZmPujEYyWyKYji0dZrQ==";
    @Value("${lithium.services.cashier.mock.hexopay.private_key:MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC9j4ydXEcqRCqMHxDOgpAUhD7Nhd49IUcOZxmg3FnYDszGm2ZpcGtdrSC9XEJ+TsTkirmVMR5ZacPxmmRc1IUbeQjW+DVu1U6fb8fNT0Es6QmqoV9gXreoqGnwzy6q1gJP0S9cerEbvR1Vn/nmxgYUJRZdPtILVvL5kSz5nXer1J99zUq9b28S7dMu8LApPPrBjSL2iFDN1C0uWLFN1aHNMfmjF7kAMVW/GFmfedTNyCSZIk72xqrdrFrFuu7hL6Gm5bPbZSL6SYctfz2bl8dlJpqK56I7kozJwKVTfYkSr9IpnkqIAO2h642BT6OqTQBl/AE5+FCSdqFH1gP0bLF/AgMBAAECggEBAKROlw2W/MBwEYkfLEwz83s1HdK7ZGUMQTEEwLwkA2Q5f9nuvRhLr2SlOimonKIq2JNYg4AD8VCDUqpKrw+fzjh/8t+SiY4mKl4cX81gExhAbnaOJcO8lekFAb8Pdl3x3lMc+LZqSrXGeHTNk6kqn/46H2Z3m0zk8vrLkXZTvXUb+2w4z+LYMEFg1jppUqTjGLKNwti7KIV7q9uyKPwYBp0058qbK9xk0uvf4yxOkHwYzNIWYDlugIXxBmmKUeQbGuFsKtLkrfJ80+R61NIqwLc8tUkXrcCPi1oFpQAoIKv6FewRHNJ1mquKyY8SIXXgfMpj6g8eildAiRwxIcaJ2IECgYEA5RP3rnHATZa9OP+4VGPh6VGKiQWExTIsEcNzufeGHRUX5csHuBi7UIHKK0YJHJXm21FEWqHrUmA6wgfJ9v2kN7NxYyypugcTsvoTfOjJGn1IdHAPHFL1EjLJOoEAAMWr7FKwI/K1rObBRxvURinFUn4DrUkfuKB4TR5qNhnwMKECgYEA09aqvAzqk5NGzau9FMO3bXRFdFYh1TTVgA1R6fFHIg5L7wS+ywjcUEeYQVkiknoxX3ClYvfyOKBoX+cQnbFIajEk/50YecyhbQx2f+FqiRi7BbZqyM2UNGpY05wb6p2ZJY/NkHHA1UV5XojCvONVCmB9jtiSLSZNPwE0E3juDh8CgYEAocVFDtrjq6c8v2jO+kGDAuqDg5nzFfMFbhHxwq1K93Sy6KNFMzu74/GUHJUb0CUH3293bu2TqhuswuMPPC4IbxF4jEm5HlbrEWRKqNYXVm6M4TVVZnIrAQv+USNSJjlx3LTUEL7Qj2ZxrU64zEepQkUdQZ03GoGwEv3ckZIlraECgYAXtMAqhy+BEEwuG/fu6PAP+aEEIddrapwx1kvCaFPThdINj3RieD1fZoOtXHI3/iCkU5DOgHzExX6orMz5OKun5pQDXussRjASagCg0vL/IJtdSkqs6gSZ0QTbuW6OVNCcmXkhnvk7ZKjuYVOZm78aiC3E3IuPLegbEuFnBHNvdQKBgA0V7mfO3QVRxzSBCgF1Ykh2+Ol1D82BYjoxqP4wekbq/r+TZxOVM1pkxTdrzQpJ3SXzOExfs1cfVOoHC3kua28GSxLg1dyZzE/ZqspAGWj4UoTCHiN4mDsB0yFz4qp3idi87X2Vlc5jHpOtrwEC7dYPQR5PEZH2BzEI3FYVgrqK}")
    private String PRIVATE_KEY;
    @Value("${lithium.services.cashier.mock.hexopay.notification_delay: 2000}")
    private long NOTIFICATION_DELAY;
    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    WebhookService webhookService;

    @Autowired
    private ObjectMapper mapper;

    public void notify(final Transaction transaction, final String url, Scenario scenario) {
        try {
            TransactionResponse request = new TransactionResponse();
            request.setTransaction(transaction);
            final String notificationData = mapper.writeValueAsString(request);

            Long delay = NOTIFICATION_DELAY;
            String signature = getSignature(notificationData);
            switch (scenario) {
                case NO_NOTIFICATION:
                    return;
                case NO_NOTIFICATION_DELAY:
                    delay = 0L;
                    break;
                case NO_NOTIFICATION_SIGNATURE:
                    signature = null;
                    break;
                case FAILED_NOTIFICATION_SIGNATURE:
                    signature = FAILED_SIGNATURE;
                    break;
                default:
                    break;
            }
            String finalSignature = signature;
            taskScheduler.schedule( () -> {
                try {
                    webhookService.callWebhook(notificationData, transaction.getTrackingId(), url, finalSignature);
                } catch (Exception ex) {
                    log.error("Failed to send notification trackingid=" + transaction.getTrackingId() + " Url: " + url + " Data:" + notificationData);
                }
            }, new Date(System.currentTimeMillis() + delay));
        } catch (Exception ex) {
            log.error("Failed to init notification(" + transaction.getTrackingId() + ") Scenario: " + scenario.name() + "Exeption: " + ex.getMessage(), ex);
        }
    }

    private String getSignature(String request) throws Exception {
        try {
            Signature sigEng = Signature.getInstance("SHA256withRSA");
            KeyFactory keyFactory=KeyFactory.getInstance("RSA");
            PrivateKey privKey= keyFactory.generatePrivate(new PKCS8EncodedKeySpec(java.util.Base64.getDecoder().decode(PRIVATE_KEY)));
            sigEng.initSign(privKey);
            sigEng.update(request.getBytes());
            return Base64.getEncoder().encodeToString(sigEng.sign());
        } catch (Exception e) {
            log.error("Signature generation is failed for webhook request: " + request + "Exception: " + e.getMessage(), e);
            throw new Exception("Signature generation is failed");
        }
    }


}
