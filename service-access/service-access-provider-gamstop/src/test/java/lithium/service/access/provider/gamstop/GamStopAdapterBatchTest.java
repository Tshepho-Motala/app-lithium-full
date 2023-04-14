package lithium.service.access.provider.gamstop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.rest.LoggingRequestInterceptor;
import lithium.service.access.client.gamstop.objects.ExclusionRequest;
import lithium.service.access.client.gamstop.objects.ExclusionResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class GamStopAdapterBatchTest {

    @Ignore
    @Test
    public void shouldReturnSuccess() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        //String url = "https://batch.stage.gamstop.io/v2";
        String url = "http://localhost:8080/v2";
        HttpHeaders headers = setHeaders("4e7a6335-e7b78395e67e87220cc27086f63a38f5");
        HttpEntity<List<ExclusionRequest>> entity = new HttpEntity<>(getRequestData(),headers);
        restTemplate.setInterceptors(Arrays.asList(new LoggingRequestInterceptor()));
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {

            }
        });
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
        if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.hasBody()) {
            ObjectMapper mapper = new ObjectMapper();
            List<ExclusionResult> myObjects = Arrays.asList(mapper.readValue(responseEntity.getBody(), ExclusionResult[].class));
            log.debug("Response {}",responseEntity.getHeaders());
            log.debug("{}",responseEntity.getBody());
            log.debug("{}",myObjects);
        }

    }

    private List<ExclusionRequest> getRequestData(){
        List<ExclusionRequest> requestData = new ArrayList<>();
        requestData.add(ExclusionRequest.builder()
                .firstName("Daniel")
                .lastName("Pasquier")
                .dateOfBirth("1945-05-29")
                .email("umaury@blanc.org")
                .postcode("PE166RY")
                .mobile("07700900000")
                .build());
        requestData.add(ExclusionRequest.builder()
                .firstName("Harry")
                .lastName("Potter")
                .dateOfBirth("1970-01-01")
                .email("harry.potter@example.com")
                .postcode("HP11AA")
                .mobile("07700900004")
                .build());
        return requestData;
    }

    private HttpHeaders setHeaders(String apiKey) {
        log.debug(apiKey);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", apiKey);
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        log.debug(String.valueOf(headers));
        return headers;
    }

}
