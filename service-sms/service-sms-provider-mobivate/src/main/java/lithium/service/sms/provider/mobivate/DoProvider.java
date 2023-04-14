package lithium.service.sms.provider.mobivate;

import lithium.service.sms.client.internal.DoProviderRequest;
import lithium.service.sms.client.internal.DoProviderResponse;
import lithium.service.sms.client.internal.DoProviderResponseStatus;
import lithium.service.sms.provider.DoProviderInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static lithium.service.sms.provider.mobivate.data.SMSConstants.MESSAGE_TEXT;
import static lithium.service.sms.provider.mobivate.data.SMSConstants.ORIGINATOR;
import static lithium.service.sms.provider.mobivate.data.SMSConstants.PASSWORD;
import static lithium.service.sms.provider.mobivate.data.SMSConstants.RECIPIENT;
import static lithium.service.sms.provider.mobivate.data.SMSConstants.REFERENCE;
import static lithium.service.sms.provider.mobivate.data.SMSConstants.ROUTE;
import static lithium.service.sms.provider.mobivate.data.SMSConstants.USER_NAME;
import static org.springframework.http.HttpMethod.GET;

@Service
@Slf4j
public class DoProvider implements DoProviderInterface {


    @Override
    public DoProviderResponse send(DoProviderRequest request, RestTemplate restTemplate) throws Exception {

        DoProviderResponse response = new DoProviderResponse();
        response.setSmsId(request.getSmsId());

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("User-Agent", "Lithium sms-service User Agent");

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            protected boolean hasError(HttpStatus statusCode) {
                return false;
            }
        });

        UriComponents builder = UriComponentsBuilder.fromHttpUrl(request.getProperty("baseUrl"))
                .queryParam(MESSAGE_TEXT, request.getContent())
                .queryParam(RECIPIENT, request.getTo().get(0))
                .queryParam(ROUTE, request.getProperty(ROUTE))
                .queryParam(USER_NAME, request.getProperty(USER_NAME))
                .queryParam(PASSWORD, request.getProperty(PASSWORD))
                .queryParam(ORIGINATOR, request.getProperty(ORIGINATOR))
                .queryParam(REFERENCE, request.getSmsId())
                .build();
        log.debug("UriComponents builder with values" + builder.toString());

        ResponseEntity<String> exchange = restTemplate.exchange(builder.encode().toUri(),
                GET, httpEntity, String.class);

        log.debug("After mobivate request with SmsId:" + request.getSmsId() + " get:" + exchange.toString());

        if (exchange.getStatusCode().is2xxSuccessful()) {
            response.setStatus(DoProviderResponseStatus.PENDING);
            log.info("Set status to PENDING for SmsId" + request.getSmsId());
        } else {
            response.setStatus(DoProviderResponseStatus.FAILED);
            log.warn("Set status to FAILED for SmsId" + request.getSmsId());
        }
        return response;
    }

}
