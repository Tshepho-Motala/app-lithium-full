package lithium.service.cashier.processor.paysafegateway;

import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.client.internal.DoProcessorRequestUser;
import lithium.service.cashier.processor.paysafegateway.data.BillingDetails;
import lithium.service.cashier.processor.paysafegateway.data.Card;
import lithium.service.cashier.processor.paysafegateway.data.CardExpiry;
import lithium.service.cashier.processor.paysafegateway.data.CardPaymentRequest;
import lithium.service.cashier.processor.paysafegateway.data.CardPaymentResponse;
import lithium.service.cashier.processor.paysafegateway.data.VerificationRequest;
import lithium.service.cashier.processor.paysafegateway.data.VerificationResponse;
import lithium.service.user.client.objects.Address;
import lombok.extern.slf4j.Slf4j;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Random;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class DoProcessorITest {

    @Mock
    private DoProcessorRequestUser user;

    @Mock
    private Address residentialAddress;

    @Mock
    private DoProcessorRequest request;

    private BillingDetails billingDetails;
    private Card card;
    
    @Before
    public void init() throws Exception {
        card=Card.builder().paymentToken(request.stageOutputData(1, "paymentToken")).build();
        CardExpiry cardExpiry=new CardExpiry();
        cardExpiry.setMonth(12);
        cardExpiry.setYear(2027);
        card.setCardExpiry(cardExpiry);
        card.setLastDigits("123");
        card.setCardNum("4111111111111111");
        long tranId=new Random().nextLong();
        log.info("transId {}", tranId);
    	when(request.getTransactionId()).thenReturn(tranId);
    	when(request.processorCommunicationAmount()).thenReturn(BigDecimal.valueOf(2));
    	when(request.getUser()).thenReturn(user);
    	when(user.getResidentialAddress()).thenReturn(residentialAddress);
    	when(residentialAddress.toOneLinerStreet()).thenReturn("100 Queen Street West");
    	when(residentialAddress.getCity()).thenReturn("Toronto");
    	when(residentialAddress.getCountryCode()).thenReturn("CA");
    	when(residentialAddress.getPostalCode()).thenReturn("M5H2N2");
        when(request.getProperty("cardPaymentApiBaseUrl")).thenReturn("https://api.test.paysafe.com");
    	when(request.getProperty("accountId")).thenReturn("1001688240");
    	when(request.getProperty("apiKeyUn")).thenReturn("test_rezakhan");
    	when(request.getProperty("apiKeyPswd")).thenReturn("B-qa2-0-5ea03ded-0-302d021479e92d8bd1a0ee12cbd725711adff97557b07a44021500894004cdc197e0ba1573751e120bf4999d0a813d");
    	billingDetails=BillingDetails.builder()
                .street(residentialAddress.toOneLinerStreet())
                .city(residentialAddress.getCity())
                .country(residentialAddress.getCountryCode())
                .zip(residentialAddress.getPostalCode())
                .build();
    }

    @Ignore
    @Test
    public void shouldValidateDepositStage2() throws Exception {
        RestTemplate rest = new RestTemplate();
        when(request.getProperty("apiKeyUn")).thenReturn("test_playsafetest");
        when(request.getProperty("apiKeyPswd")).thenReturn(
                "B-qa2-0-5eba452c-0-302d021467b1277a540c4883243b9d94c5aa33ec980ddebb02150087ce1e83ce7fa7414cf7aca6b55974ebd8c6c22b");
        VerificationRequest verificationRequest = VerificationRequest.builder()
                .card(card)
                .merchantRefNum(String.valueOf(request.getTransactionId()))
                .billingDetails(billingDetails)
                .build();
        when(request.getProperty("accountId")).thenReturn("1001693120");
        final HttpEntity<VerificationRequest> entity = new HttpEntity<>(verificationRequest,
                buildHeaders(request.getProperty("apiKeyUn"), request.getProperty("apiKeyPswd")));
        ResponseEntity<VerificationResponse> response = rest.exchange("https://api.test.paysafe.com"
                        + "/cardpayments/v1/accounts/" + request.getProperty("accountId") + "/verifications",
                HttpMethod.POST, entity, VerificationResponse.class);

        log.debug("Response " + response);
    }



    @Ignore
    @Test
    public void shouldProcessDepositStage2() throws Exception {
        card.setCardNum("4111111111111111");
        RestTemplate rest = new RestTemplate();
        when(request.getProperty("apiKeyUn")).thenReturn("test_rezakhan");
        when(request.getProperty("apiKeyPswd")).thenReturn("B-qa2-0-5ea03ded-0-302d021479e92d8bd1a0ee12cbd725711adff97557b07a44021500894004cdc197e0ba1573751e120bf4999d0a813d");
        CardPaymentRequest cardPaymentRequest = CardPaymentRequest.builder()
                .merchantRefNum(String.valueOf(request.getTransactionId()))
                .amount(request.processorCommunicationAmount().movePointRight(2).longValue())
                .settleWithAuth(true)
                .card(card)
                .billingDetails(billingDetails)
                .build();
        final HttpEntity<CardPaymentRequest> entity = new HttpEntity<>(cardPaymentRequest,
                buildHeaders(request.getProperty("apiKeyUn"), request.getProperty("apiKeyPswd")));
        ResponseEntity<CardPaymentResponse> response = rest.exchange(request.getProperty("cardPaymentApiBaseUrl")
                        + "/cardpayments/v1/accounts/" + request.getProperty("accountId") + "/auths",
                HttpMethod.POST, entity, CardPaymentResponse.class);
        CardPaymentResponse cardPaymentResponse=response.getBody();
        log.debug("CardPaymentResponse " + response);
    }
    
    private String base64Hash(String username, String password) {
		return Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
	}

	private HttpHeaders buildHeaders(String un, String pswd) {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + base64Hash(un, pswd));

		log.info("{}",base64Hash(un, pswd));
		return headers;
	}

    
}
