package lithium.service.casino.provider.roxor.services;

import lithium.modules.ModuleInfo;
import lithium.service.casino.provider.roxor.config.EvolutionDirectGameLaunchApiProviderConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EvolutionClientService {

    RestTemplateBuilder restTemplateBuilder;

    @Autowired
    ModuleInfo moduleInfo;

    public EvolutionClientService(@Qualifier("lithium.rest") RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    public String getLobbyStateByDomain(EvolutionDirectGameLaunchApiProviderConfig config) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplateBuilder
                    .basicAuthentication(config.getUsername(), config.getPassword())
                    .build()
                    .getForEntity(config.getUrl() + "/api/lobby/v1/" + config.getCasinoId() + "/state", String.class, requestEntity);
            if(response != null && response.getStatusCode() == HttpStatus.OK && response.hasBody()) {
                return response.getBody();
            }
            log.debug("Evolution Error Response Headers : {}", response.getHeaders());
            log.debug("Evolution Error Response Body : {}", response.getBody());
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }

}
