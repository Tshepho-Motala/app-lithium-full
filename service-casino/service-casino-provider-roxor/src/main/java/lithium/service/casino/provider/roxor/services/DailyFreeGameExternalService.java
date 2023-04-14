package lithium.service.casino.provider.roxor.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.casino.provider.roxor.data.response.freegames.GamesAvailability;
import lithium.service.casino.provider.roxor.data.response.freegames.GamesAvailabilityDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class DailyFreeGameExternalService {

    RestTemplate restTemplate;

    public DailyFreeGameExternalService(@Qualifier("lithium.rest") RestTemplateBuilder restTemplateBuilder) {
        restTemplate = restTemplateBuilder.build();
    }

    public List<GamesAvailability> getFreeGames(String domainName, String userApiToken, String url, String website) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity requestEntity = new HttpEntity<>(headers);
        List<lithium.service.casino.provider.roxor.data.response.freegames.GamesAvailability> gamesAvailabilityList = null;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url + "/free-game/v2/games-availability/website/" + website + "/player/" + userApiToken, String.class, requestEntity);
            log.info("Response Body: " + response.getBody());
            if (response != null && response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
                ObjectMapper objectMapper = new ObjectMapper();
                GamesAvailabilityDto gamesAvailabilityDto = objectMapper.readValue(response.getBody(), GamesAvailabilityDto.class);
                gamesAvailabilityList = gamesAvailabilityDto.getGamesAvailability();
                return gamesAvailabilityList;
            }
            log.debug("Roxor Games Availability Error Response Headers : {}", response.getHeaders());
            log.debug("Roxor Games Availability Error  Response Body : {}", response.getBody());
        } catch (Exception e) {
            log.error("roxor games availability error [domainName=" + domainName
                    + ", playerId=" + userApiToken
                    + ", website=" + website + "] " +
                    e.getMessage(), e);
        }
        return gamesAvailabilityList;
    }
}
