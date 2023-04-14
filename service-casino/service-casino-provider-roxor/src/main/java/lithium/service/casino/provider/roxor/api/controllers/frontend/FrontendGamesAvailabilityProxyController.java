package lithium.service.casino.provider.roxor.api.controllers.frontend;

import lithium.service.casino.provider.roxor.services.ExternalGamesAvailabilityProxyService;
import lithium.service.casino.provider.roxor.storage.entities.GamesAvailability;
import lithium.tokens.LithiumTokenUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping( "/frontend/games-availability" )
public class FrontendGamesAvailabilityProxyController {

    @Autowired
    ExternalGamesAvailabilityProxyService externalGamesAvailabilityProxyService;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping( "/lookup" )
    public List<lithium.service.casino.provider.roxor.data.response.freegames.GamesAvailability> lookup(LithiumTokenUtil tokenUtil) throws Exception {
        return externalGamesAvailabilityProxyService.getPlayerGamesAvailability(tokenUtil.getJwtUser().getGuid(), tokenUtil.domainName(), tokenUtil.getJwtUser().getApiToken());
    }

}
