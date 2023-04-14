package lithium.service.games.provider.google.rge.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.exceptions.Status469InvalidInputException;
import lithium.service.games.provider.google.rge.client.objects.request.PredictInstance;
import lithium.service.games.provider.google.rge.client.objects.request.PredictRequest;
import lithium.service.games.provider.google.rge.client.objects.response.PredictResponse;
import lithium.service.games.provider.google.rge.client.objects.response.Recommendation;
import lithium.service.games.provider.google.rge.data.objects.ProviderSettingPredict;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class RecommendedGamesService {

    private RestTemplate restTemplate;

    @Autowired
    private GoogleAuthenticationService authenticationService;

    @Autowired
    private ConfigurationService configurationService;

    public RecommendedGamesService(@Qualifier("lithium.rest") RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public List<Recommendation> getRecommendations(String userGuid) throws Exception {
        try {
            if (userGuid == null || userGuid.split("/").length != 2) {
                throw  new Status469InvalidInputException("Invalid userGuid: " + userGuid);
            }
            String domainName = userGuid.split("/")[0];
            String bearerToken = "Bearer " + authenticationService.getAccessToken(domainName);
            Integer pageSize = configurationService.getPredictConfigurationByDomain(domainName).getPageSize();
            PredictRequest request = PredictRequest.builder()
                    .instances(Arrays.asList(new PredictInstance(userGuid,pageSize))).build();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            headers.add(HttpHeaders.AUTHORIZATION, bearerToken);

            ProviderSettingPredict settings = configurationService.getPredictConfigurationByDomain(domainName);

            String url = String.format("%s/v1/projects/%s/locations/%s/endpoints/%s:predict",
                    settings.getPredictURL(), settings.getProject(), settings.getLocation(), settings.getEndpoint());

            HttpEntity requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<String> responseString = restTemplate.postForEntity(url, requestEntity, String.class);

            if (responseString != null && responseString.getStatusCode() == HttpStatus.OK && responseString.hasBody()) {
                log.debug("Response: " + responseString);
                ObjectMapper mapper = new ObjectMapper();
                PredictResponse predictResponse = mapper.readValue(responseString.getBody(), PredictResponse.class);
                return predictResponse.getPredictions().stream().findFirst().get().getRecommendations();
            }
            log.debug("Predict Error Response Headers : {}", responseString.getHeaders());
            log.debug("Predict Error Response Body : {}", responseString.getBody());
        } catch (Exception e) {
            log.error("Failed to get recommended games from google rge", e);
            throw e;
        }
        return null;
    }

}
