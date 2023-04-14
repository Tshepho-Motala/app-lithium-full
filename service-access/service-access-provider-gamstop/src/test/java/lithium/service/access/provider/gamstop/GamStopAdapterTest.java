package lithium.service.access.provider.gamstop;

import lithium.service.access.provider.gamstop.data.enums.ExclusionType;
import lithium.service.access.provider.gamstop.data.objects.SelfExclusionResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class GamStopAdapterTest {

    @Ignore
    @Test
    public void shouldReturnY(){
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.stage.gamstop.io/v2";
        HttpHeaders headers = setHeaders("4e7a6335-e7b78395e67e87220cc27086f63a38f5");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("firstname","Daniel");
        map.add("lastName","Pasquier");
        map.add("dateOfBirth","1945-05-29");
        map.add("email","umaury@blanc.org");
        map.add("postcode","PE166RY");
        map.add("mobile","07700900000");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map,headers);
        ResponseEntity<?> responseEntity = restTemplate.postForEntity(url, entity, String.class);
        log.debug("Response {}",responseEntity.getHeaders());
        SelfExclusionResponse credentialsResponse = new SelfExclusionResponse();
        List<String> exclusionTypeResponse = responseEntity.getHeaders().get("X-Exclusion");
        if (exclusionTypeResponse != null || !exclusionTypeResponse.isEmpty()) {
            credentialsResponse.setExclusionType(ExclusionType.valueOf(exclusionTypeResponse.get(0)));
        }
        List<String> xUniqueId = responseEntity.getHeaders().get("X-Unique-Id");
        if (xUniqueId != null || !xUniqueId.isEmpty()) {
            credentialsResponse.setXUniqueId(xUniqueId.get(0));
        }
        log.debug("{}",credentialsResponse);
    }

    @Ignore
    @Test
    public void shouldReturnN(){
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.stage.gamstop.io/v2";
        HttpHeaders headers = setHeaders("4e7a6335-e7b78395e67e87220cc27086f63a38f5");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("firstname","James");
        map.add("lastName","Bond");
        map.add("dateOfBirth","1945-05-29");
        map.add("email","james.bond@blanc.org");
        map.add("postcode","PE166RY");
        map.add("mobile","07700900023");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map,headers);
        ResponseEntity<?> responseEntity = restTemplate.postForEntity(url, entity, String.class);
        log.debug("Response {}",responseEntity.getHeaders());
        SelfExclusionResponse credentialsResponse = new SelfExclusionResponse();
        List<String> exclusionTypeResponse = responseEntity.getHeaders().get("X-Exclusion");
        if (exclusionTypeResponse != null || !exclusionTypeResponse.isEmpty()) {
            credentialsResponse.setExclusionType(ExclusionType.valueOf(exclusionTypeResponse.get(0)));
        }
        List<String> xUniqueId = responseEntity.getHeaders().get("X-Unique-Id");
        if (xUniqueId != null || !xUniqueId.isEmpty()) {
            credentialsResponse.setXUniqueId(xUniqueId.get(0));
        }
        log.debug("{}",credentialsResponse);
    }

    private HttpHeaders setHeaders(String apiKey) {
        log.debug(apiKey);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", apiKey);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());
        log.debug(String.valueOf(headers));
        return headers;
    }

    @Ignore
    @Test
    public void shouldPassIfIPWhiteListed() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.stage.gamstop.io/health";
        ResponseEntity<String> response = restTemplate.getForEntity( url , String.class );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
